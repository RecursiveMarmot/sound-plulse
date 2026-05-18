package com.timess.soundpulse.service;

import com.timess.soundpulse.model.dto.assistant.AssistantChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AssistantChatService {

    /**
     *  流式聊天
     */
    SseEmitter streamChat(AssistantChatRequest assistantChatRequest);
}
