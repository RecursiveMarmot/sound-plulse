package com.timess.soundpulse.model.dto.assistant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "聊天请求")
@Data
public class AssistantChatRequest implements Serializable {
    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "消息")
    private String content;

    private static final long serialVersionUID = 1L;

}
