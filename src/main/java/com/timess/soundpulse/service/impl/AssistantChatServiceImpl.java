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
import dev.langchain4j.service.TokenStream;
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
    private StreamingChatModel streamingChatModel;

    @Autowired
    private MusicFunctionTools musicFunctionTools;

    @Autowired
    private ExecutorService actionExecutorPool;

    private MusicAssistantAgent musicAssistantAgent;

    @PostConstruct
    public void initAgent() {
        this.musicAssistantAgent = AiServices.builder(MusicAssistantAgent.class)
            .streamingChatModel(streamingChatModel)
            .tools(musicFunctionTools)
            .build();
    }

    /**
     * 简单内存会话上下文（按用户维度隔离）。
     */
    private final Map<Long, ConversationContext> conversationContexts = new ConcurrentHashMap<>();

    @Override
    public SseEmitter streamChat(AssistantChatRequest request) {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        actionExecutorPool.execute(() -> {
            TokenStream tokenStream = musicAssistantAgent.chatStream(request.getContent());

            tokenStream
                    .onPartialResponse(token -> {
                        // 直接转发 token 给前端
                        safeSend(emitter, "text_delta", Map.of("delta", token));
                    })
                    .onCompleteResponse(response -> {
                        // 流式完成
                        safeSend(emitter, "complete", Map.of("status", "ok"));
                        emitter.complete();
                    })
                    .onError(error -> {
                        log.error("流式出错", error);
                        safeSend(emitter, "error", Map.of("message", error.getMessage()));
                        emitter.completeWithError(error);
                    })
                    .start();
        });

        return emitter;
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
}

