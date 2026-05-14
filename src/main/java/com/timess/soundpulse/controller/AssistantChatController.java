package com.timess.soundpulse.controller;

import com.timess.soundpulse.assistant.model.LLMResponse;
import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.model.dto.assistant.AssistantChatRequest;
import com.timess.soundpulse.service.AssistantChatService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/assistant")
@Tag(name = "助手聊天接口")
public class AssistantChatController {


    @Autowired
    private ChatModel chatModel;

    @Autowired
    private AssistantChatService assistantChatService;

    /**
     * 流式聊天接口
     * 前端使用 EventSource 或 fetch API 接收流式数据
     */
    @PostMapping("/chat/stream")
    @Operation(summary = "流式聊天", description = "与助手进行流式对话，实时返回响应")
    public SseEmitter streamChat(@RequestBody AssistantChatRequest assistantChatRequest) {
        return assistantChatService.streamChat(assistantChatRequest);
    }

    /**
     *
     */
    @PostMapping("/chat")
    @Operation(summary = "普通聊天", description = "非流式聊天，等待完整响应")
    public BaseResponse<String> chat(@RequestBody AssistantChatRequest assistantChatRequest) {
        String userMessage = assistantChatRequest.getContent();
        log.info("收到普通聊天请求: {}", userMessage);
        String chat = chatModel.chat(userMessage);
        return ResultUtils.success(chat);
    }

    /**
     *
     */
    @PostMapping("/action")
    @Operation(summary = "执行操作", description = "语言解析，并响应操作")
    public BaseResponse<LLMResponse> action(@RequestBody AssistantChatRequest assistantChatRequest) {
        return ResultUtils.success(assistantChatService.chat(assistantChatRequest));
    }
}