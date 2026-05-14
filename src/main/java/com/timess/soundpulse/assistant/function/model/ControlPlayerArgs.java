package com.timess.soundpulse.assistant.function.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "control_player function 参数")
public class ControlPlayerArgs {

    @Schema(description = "动作类型", allowableValues = {"play_control", "playback_control", "volume_control", "search", "favorite", "lyric"})
    private String actionType;

    @Schema(description = "具体动作，例如 play / set_volume / next")
    private String action;

    @Schema(description = "动作参数 JSON 对象")
    private JsonNode params;
}

