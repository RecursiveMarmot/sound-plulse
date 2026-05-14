package com.timess.soundpulse.assistant.function.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "function calling 结果")
public class ToolResult {

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "结果类型，例如 search_result/player_state/action_result/confirm_required")
    private String type;

    @Schema(description = "返回文本")
    private String message;

    @Schema(description = "返回数据")
    private Object data;
}

