package com.timess.soundpulse.controller;

import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.model.dto.assistant.AssistantChatRequest;
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
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/assistant")
@Tag(name = "助手聊天接口")
public class AssistantChatController {

    @Autowired
    private StreamingChatModel streamingChatModel;  // 改用 StreamingChatModel

    @Autowired
    private ChatModel chatModel;

    /**
     * 流式聊天接口
     * 前端使用 EventSource 或 fetch API 接收流式数据
     */
    @PostMapping("/chat/stream")
    @Operation(summary = "流式聊天", description = "与助手进行流式对话，实时返回响应")
    public SseEmitter streamChat(@RequestBody AssistantChatRequest assistantChatRequest) {
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
                    // 使用 SSE 格式发送
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(partialResponse));
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
                    emitter.send(SseEmitter.event()
                            .name("complete")
                            .data(""));
                    emitter.complete();
                } catch (IOException e) {
                    log.error("发送完成标记失败", e);
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onError(Throwable error) {
                // 发生错误
                log.error("流式响应出错", error);
                emitter.completeWithError(error);
            }
        });

        return emitter;
    }

    /**
     * 保留原有的非流式接口，用于兼容
     */
    @PostMapping("/chat")
    @Operation(summary = "普通聊天", description = "非流式聊天，等待完整响应")
    public BaseResponse<String> chat(@RequestBody AssistantChatRequest assistantChatRequest) {
        String userMessage = assistantChatRequest.getContent();
        log.info("收到普通聊天请求: {}", userMessage);
        String chat = chatModel.chat(userMessage);
        return ResultUtils.success(chat);
    }
}