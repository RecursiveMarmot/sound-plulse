package com.timess.soundpulse.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timess.soundpulse.assistant.actionsenum.ActionParamSpec;
import com.timess.soundpulse.assistant.actionsenum.ActionParamType;
import com.timess.soundpulse.assistant.actionsenum.FavoriteAction;
import com.timess.soundpulse.assistant.actionsenum.LyricAction;
import com.timess.soundpulse.assistant.actionsenum.PlayControlAction;
import com.timess.soundpulse.assistant.actionsenum.PlaybackControlAction;
import com.timess.soundpulse.assistant.actionsenum.SearchAction;
import com.timess.soundpulse.assistant.actionsenum.VolumeControlAction;
import com.timess.soundpulse.assistant.model.ConversationContext;
import com.timess.soundpulse.assistant.model.LLMResponse;
import com.timess.soundpulse.assistant.model.MusicAction;
import com.timess.soundpulse.assistant.model.MusicPlayerState;
import com.timess.soundpulse.assistant.prompt.MusicAssistantPrompt;
import com.timess.soundpulse.model.dto.assistant.AssistantChatRequest;
import com.timess.soundpulse.service.AssistantChatService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 音乐助手聊天服务实现。
 * 负责将用户自然语言指令解析为结构化动作，并执行对应操作。
 */
@Service
@Slf4j
public class AssistantChatServiceImpl implements AssistantChatService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private StreamingChatModel streamingChatModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 简单内存会话上下文（按用户维度隔离）。
     */
    private final Map<Long, ConversationContext> conversationContexts = new ConcurrentHashMap<>();

    /**
     * 处理用户语音/文本指令。
     */
    public LLMResponse processUserCommand(Long userId, String userInput, MusicPlayerState currentState) {
        long safeUserId = userId == null ? 0L : userId;
        String safeInput = userInput == null ? "" : userInput;
        MusicPlayerState safeState = currentState == null ? MusicPlayerState.defaultState() : currentState;

        ConversationContext context = conversationContexts.getOrDefault(safeUserId, new ConversationContext());
        context.setCurrentState(safeState);

        String fullPrompt = buildPrompt(safeInput, context);
        String llmRaw = chatModel.chat(fullPrompt);

        try {
            LLMResponse response = parseResponse(llmRaw);
            normalizeAndValidate(response);

            if (Boolean.TRUE.equals(response.getNeedConfirmation())) {
                context.setPendingAction(response);
                conversationContexts.put(safeUserId, context);
                return response;
            }

            for (MusicAction action : response.getActions()) {
                executeAction(action);
            }
            updateContext(context, response);
            conversationContexts.put(safeUserId, context);
            return response;
        } catch (Exception e) {
            log.error("解析 LLM 响应失败, raw={}", llmRaw, e);
            return buildFallbackResponse();
        }
    }

    /**
     * 处理用户确认（用于歧义澄清后的执行）。
     */
    public LLMResponse handleConfirmation(Long userId, boolean confirm) {
        long safeUserId = userId == null ? 0L : userId;
        ConversationContext context = conversationContexts.get(safeUserId);
        if (context == null || context.getPendingAction() == null) {
            return simpleReply("当前没有待确认的操作");
        }

        if (!confirm) {
            context.setPendingAction(null);
            return simpleReply("好的，已取消本次操作");
        }

        LLMResponse pending = context.getPendingAction();
        for (MusicAction action : pending.getActions()) {
            executeAction(action);
        }
        context.setPendingAction(null);
        pending.setResponseText("好的，已为您执行对应操作");
        pending.setNeedConfirmation(false);
        return pending;
    }

    private String buildPrompt(String userInput, ConversationContext context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(MusicAssistantPrompt.SYSTEM_PROMPT).append("\n\n");

        if (!context.getConversationHistory().isEmpty()) {
            prompt.append("## 对话历史\n");
            for (String history : context.getConversationHistory()) {
                prompt.append(history).append("\n");
            }
        }

        MusicPlayerState state = context.getCurrentState();
        int totalDuration = Math.max(1, state.getTotalDuration());
        double progressPercent = (double) state.getCurrentPosition() / totalDuration * 100;

        prompt.append(String.format(Locale.ROOT, """

                ## 当前播放状态
                - 当前歌曲: %s
                - 歌手: %s
                - 播放进度: %d/%d秒(%.1f%%)
                - 音量: %d/100
                - 循环模式: %s
                - 随机播放: %s

                """,
            state.getCurrentSongName(),
            state.getCurrentArtist(),
            state.getCurrentPosition(),
            state.getTotalDuration(),
            progressPercent,
            state.getVolume(),
            state.getLoopMode(),
            state.isShuffle() ? "开启" : "关闭"
        ));

        prompt.append("## 用户指令\n");
        prompt.append(userInput).append("\n");

        if (context.getAmbiguousContext() != null) {
            prompt.append("## 上下文提示\n");
            prompt.append("用户上次有歧义的查询: ").append(context.getAmbiguousContext()).append("\n");
        }

        prompt.append("\n请严格输出 JSON，不要输出 markdown 或额外解释。\n");
        return prompt.toString();
    }

    private void updateContext(ConversationContext context, LLMResponse response) {
        context.getConversationHistory().add("助手: " + response.getResponseText());
        if (context.getConversationHistory().size() > 10) {
            context.getConversationHistory().remove(0);
        }

        if (response.getContext() != null && response.getContext().getAmbiguousQuery() != null) {
            context.setAmbiguousContext(response.getContext().getAmbiguousQuery());
        } else {
            context.setAmbiguousContext(null);
        }
    }

    @Override
    public SseEmitter streamChat(AssistantChatRequest assistantChatRequest) {
        String userMessage = assistantChatRequest.getContent();
        log.info("收到流式聊天请求: {}", userMessage);

        // 创建 SSE 发射器，超时时间设置为 5 分钟
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        // 用于缓存完整响应的 StringBuilder
        StringBuilder fullResponse = new StringBuilder();

        // 调用流式 API
        streamingChatModel.chat(userMessage, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                // 每收到一个 token，立即发送给前端
                try {
                    log.debug("发送 token: {}", partialResponse);
                    fullResponse.append(partialResponse);
                    emitter.send(SseEmitter.event().data(partialResponse));
                } catch (IOException e) {
                    log.error("发送 token 失败", e);
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                // 响应完成，发送结束标记
                try {
                    log.info("流式响应完成，完整内容长度: {}", fullResponse.length());
                    emitter.send(SseEmitter.event().name("complete").data(""));
                    emitter.complete();
                } catch (IOException e) {
                    log.error("发送完成标记失败", e);
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onError(Throwable error) {
                log.error("流式响应出错", error);
                emitter.completeWithError(error);
            }
        });

        return emitter;
    }

    @Override
    public LLMResponse chat(AssistantChatRequest assistantChatRequest) {
        Long userId = assistantChatRequest == null ? 0L : assistantChatRequest.getUserId();
        String content = assistantChatRequest == null ? "" : assistantChatRequest.getContent();
        LLMResponse response = processUserCommand(userId, content, MusicPlayerState.defaultState());
        log.info("assistant actions: {}, reply: {}", response.getActions(), response.getResponseText());
        return response;
    }

    /**
     * 解析从大语言模型返回的原始响应字符串。
     */
    private LLMResponse parseResponse(String raw) throws IOException {
        JsonNode root = objectMapper.readTree(raw);
        LLMResponse response = new LLMResponse();

        response.setResponseText(readText(root, "responseText", "response_text"));
        response.setNeedConfirmation(readBoolean(root, "needConfirmation", "need_confirmation"));

        List<MusicAction> actions = new ArrayList<>();
        JsonNode actionsNode = readNode(root, "actions");
        if (actionsNode != null && actionsNode.isArray()) {
            for (JsonNode node : actionsNode) {
                MusicAction action = new MusicAction();
                action.setActionType(readText(node, "actionType", "action_type"));
                action.setAction(readText(node, "action"));

                JsonNode paramsNode = readNode(node, "params");
                if (paramsNode != null && paramsNode.isObject()) {
                    action.setParams(paramsNode);
                } else {
                    action.setParams(objectMapper.createObjectNode());
                }

                action.setOriginalIntent(readText(node, "originalIntent", "original_intent"));
                action.setConfidence(readDouble(node, "confidence"));
                actions.add(action);
            }
        }

        response.setActions(actions);
        return response;
    }

    private void normalizeAndValidate(LLMResponse response) {
        if (response.getActions() == null) {
            response.setActions(Collections.emptyList());
        }
        if (response.getNeedConfirmation() == null) {
            response.setNeedConfirmation(false);
        }
        if (response.getResponseText() == null || response.getResponseText().isBlank()) {
            response.setResponseText("好的");
        }

        for (MusicAction action : response.getActions()) {
            if (action.getParams() == null) {
                action.setParams(objectMapper.createObjectNode());
            }
            validateAction(action);
        }
    }

    private void validateAction(MusicAction action) {
        if (action.getActionType() == null || action.getAction() == null) {
            throw new IllegalArgumentException("actionType or action is null");
        }

        String actionType = action.getActionType().toLowerCase(Locale.ROOT);
        String actionCode = action.getAction().toLowerCase(Locale.ROOT);
        JsonNode params = action.getParams();

        List<ActionParamSpec> specs = paramSpecMap().get(actionType + "." + actionCode);
        if (specs == null) {
            throw new IllegalArgumentException("unsupported action: " + actionType + "." + actionCode);
        }

        if (params == null || !params.isObject()) {
            throw new IllegalArgumentException("params must be a JSON object");
        }

        for (ActionParamSpec spec : specs) {
            JsonNode value = params.get(spec.key());
            if ((value == null || value.isNull()) && spec.required()) {
                throw new IllegalArgumentException("missing required param: " + spec.key());
            }
            if (value == null || value.isNull()) {
                continue;
            }
            if (!matchesType(value, spec.type())) {
                throw new IllegalArgumentException("invalid param type: " + spec.key() + ", expect=" + spec.type());
            }
        }
    }

    private Map<String, List<ActionParamSpec>> paramSpecMap() {
        Map<String, List<ActionParamSpec>> map = new HashMap<>();
        putEnumSpecs(map, "play_control", PlayControlAction.values());
        putEnumSpecs(map, "playback_control", PlaybackControlAction.values());
        putEnumSpecs(map, "volume_control", VolumeControlAction.values());
        putEnumSpecs(map, "search", SearchAction.values());
        putEnumSpecs(map, "favorite", FavoriteAction.values());
        putEnumSpecs(map, "lyric", LyricAction.values());
        return map;
    }

    private void putEnumSpecs(Map<String, List<ActionParamSpec>> map, String actionType, PlayControlAction[] values) {
        for (PlayControlAction item : values) {
            map.put(actionType + "." + item.getCode(), item.getParamSpecs());
        }
    }

    private void putEnumSpecs(Map<String, List<ActionParamSpec>> map, String actionType, PlaybackControlAction[] values) {
        for (PlaybackControlAction item : values) {
            map.put(actionType + "." + item.getCode(), item.getParamSpecs());
        }
    }

    private void putEnumSpecs(Map<String, List<ActionParamSpec>> map, String actionType, VolumeControlAction[] values) {
        for (VolumeControlAction item : values) {
            map.put(actionType + "." + item.getCode(), item.getParamSpecs());
        }
    }

    private void putEnumSpecs(Map<String, List<ActionParamSpec>> map, String actionType, SearchAction[] values) {
        for (SearchAction item : values) {
            map.put(actionType + "." + item.getCode(), item.getParamSpecs());
        }
    }

    private void putEnumSpecs(Map<String, List<ActionParamSpec>> map, String actionType, FavoriteAction[] values) {
        for (FavoriteAction item : values) {
            map.put(actionType + "." + item.getCode(), item.getParamSpecs());
        }
    }

    private void putEnumSpecs(Map<String, List<ActionParamSpec>> map, String actionType, LyricAction[] values) {
        for (LyricAction item : values) {
            map.put(actionType + "." + item.getCode(), item.getParamSpecs());
        }
    }

    private boolean matchesType(JsonNode value, ActionParamType type) {
        return switch (type) {
            case STRING -> value.isTextual();
            case NUMBER -> value.isNumber();
            case BOOLEAN -> value.isBoolean();
        };
    }

    /**
     * 动作执行入口。
     */
    private void executeAction(MusicAction action) {
        log.info("execute action: type={}, action={}, params={}, confidence={}",
            action.getActionType(),
            action.getAction(),
            action.getParams(),
            action.getConfidence());
    }

    private LLMResponse buildFallbackResponse() {
        return simpleReply("抱歉，我没能理解您的指令，请再说一遍");
    }

    private LLMResponse simpleReply(String text) {
        LLMResponse response = new LLMResponse();
        response.setActions(Collections.emptyList());
        response.setNeedConfirmation(false);
        response.setResponseText(text);
        return response;
    }

    private JsonNode readNode(JsonNode root, String... keys) {
        if (root == null || keys == null) {
            return null;
        }
        for (String key : keys) {
            if (root.has(key)) {
                return root.get(key);
            }
        }
        return null;
    }

    private String readText(JsonNode root, String... keys) {
        JsonNode node = readNode(root, keys);
        return node == null || node.isNull() ? null : node.asText();
    }

    private Boolean readBoolean(JsonNode root, String... keys) {
        JsonNode node = readNode(root, keys);
        return node == null || node.isNull() ? null : node.asBoolean();
    }

    private Double readDouble(JsonNode root, String... keys) {
        JsonNode node = readNode(root, keys);
        return node == null || node.isNull() ? null : node.asDouble();
    }
}
