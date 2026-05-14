package com.timess.soundpulse.assistant.function.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "confirm_action function 参数")
public class ConfirmActionArgs {

    @Schema(description = "给用户的确认问题")
    private String question;

    @Schema(description = "候选动作列表")
    private List<ControlPlayerArgs> candidateActions;
}

