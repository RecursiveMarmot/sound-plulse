package com.timess.soundpulse.controller;

import com.timess.soundpulse.model.dto.assistant.AssistantChatRequest;
import com.timess.soundpulse.service.AssistantChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 助手聊天控制器。
 * 统一提供流式消息接口，自动识别普通聊天与操作请求。
 */
@Slf4j
@RestController
@RequestMapping("/assistant")
@Tag(name = "助手聊天接口")
public class AssistantChatController {

    @Autowired
    private AssistantChatService assistantChatService;

    /**
     * 统一流式接口：自动识别普通聊天与操作执行。
     */
    @PostMapping("/message/stream")
    @Operation(summary = "统一流式消息", description = "自动识别聊天或操作，并以流式方式返回结果")
    public SseEmitter messageStream(@RequestBody AssistantChatRequest assistantChatRequest) {
        String content = assistantChatRequest == null ? null : assistantChatRequest.getContent();
        log.info("收到统一流式消息请求: {}", content);
        return assistantChatService.streamChat(assistantChatRequest);
    }
}
