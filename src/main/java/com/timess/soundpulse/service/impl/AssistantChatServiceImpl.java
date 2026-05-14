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
import com.timess.soundpulse.assistant.agent.MusicAssistantAgent;
import com.timess.soundpulse.assistant.function.MusicFunctionTools;
import com.timess.soundpulse.assistant.model.ConversationContext;
import com.timess.soundpulse.assistant.model.LLMResponse;
import com.timess.soundpulse.assistant.model.MusicAction;
import com.timess.soundpulse.assistant.model.MusicPlayerState;
import com.timess.soundpulse.assistant.prompt.MusicAssistantPrompt;
import com.timess.soundpulse.model.dto.assistant.AssistantChatRequest;
import com.timess.soundpulse.service.AssistantChatService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
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
import java.util.concurrent.ExecutorService;
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

    @Autowired
    private MusicFunctionTools musicFunctionTools;

    @Autowired
    private ExecutorService actionExecutorPool;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MusicAssistantAgent musicAssistantAgent;

    @PostConstruct
    public void initAgent() {
        this.musicAssistantAgent = AiServices.builder(MusicAssistantAgent.class)
            .chatModel(chatModel)
            .tools(musicFunctionTools)
            .build();
    }

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
        String llmRaw = musicAssistantAgent.chat(fullPrompt);

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
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        actionExecutorPool.execute(() -> {
            try {
                safeSend(emitter, "phase", Map.of("stage", "thinking"));
                safeSend(emitter, "text_delta", Map.of("delta", "正在处理中，请稍候..."));

                LLMResponse response = chat(assistantChatRequest);

                safeSend(emitter, "phase", Map.of("stage", "finalizing"));
                if (response.getResponseText() != null && !response.getResponseText().isBlank()) {
                    streamTypingText(emitter, response.getResponseText());
                }
                if (response.getActions() != null) {
                    for (MusicAction action : response.getActions()) {
                        safeSend(emitter, "action", action);
                    }
                }
                safeSend(emitter, "final", response);
                safeSend(emitter, "complete", "");
                emitter.complete();
            } catch (Exception e) {
                log.error("instruction channel failed", e);
                safeSend(emitter, "error", Map.of("message", e.getMessage()));
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    private void streamTypingText(SseEmitter emitter, String text) {
        String content = text == null ? "" : text;
        if (content.isBlank()) {
            return;
        }
        int step = 2; // 每次推送 2 个字符，平衡流畅度和事件数量
        for (int i = 0; i < content.length(); i += step) {
            int end = Math.min(content.length(), i + step);
            safeSend(emitter, "text_delta", Map.of("delta", content.substring(i, end)));
            try {
                Thread.sleep(30L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void safeSend(SseEmitter emitter, String eventName, Object payload) {
        try {
            synchronized (emitter) {
                emitter.send(SseEmitter.event().name(eventName).data(payload));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        String jsonPayload = extractJsonPayload(raw);
        JsonNode root = objectMapper.readTree(jsonPayload);
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

    private String extractJsonPayload(String raw) {
        if (raw == null) {
            return "{}";
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed;
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
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

        if ("search".equals(actionType) && "search_song".equals(actionCode)) {
            boolean hasAny = hasAnyTextParam(params, "keyword", "artist", "album");
            if (!hasAny) {
                throw new IllegalArgumentException("search.search_song requires at least one of keyword/artist/album");
            }
        }

        for (ActionParamSpec spec : specs) {
            JsonNode value = params.get(spec.key());
            boolean skipRequiredKeywordForSearchSong =
                "search".equals(actionType) && "search_song".equals(actionCode) && "keyword".equals(spec.key());
            if ((value == null || value.isNull()) && spec.required() && !skipRequiredKeywordForSearchSong) {
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

    private boolean hasAnyTextParam(JsonNode params, String... keys) {
        for (String key : keys) {
            JsonNode node = params.get(key);
            if (node != null && !node.isNull() && node.isTextual() && !node.asText().isBlank()) {
                return true;
            }
        }
        return false;
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
