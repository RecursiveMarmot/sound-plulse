package com.timess.soundpulse.assistant.actionsenum;

public record ActionParamSpec(
    String key,
    ActionParamType type,
    boolean required,
    String description
) {
}
