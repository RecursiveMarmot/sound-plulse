package com.timess.soundpulse.assistant.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MusicAction {
    @Schema(description = "操作类型")
    private String actionType;

    @Schema(description = "具体操作")
    private String action;

    @Schema(description = "操作参数（JSON对象）")
    private JsonNode params;

    @Schema(description = "原始意图描述")
    private String originalIntent;

    @Schema(description = "置信度(0-1)")
    private Double confidence;
}
