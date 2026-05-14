package com.timess.soundpulse.assistant.actionsenum;

import java.util.List;

public enum PlayControlAction {
    PLAY("play", "播放", List.of()),
    PAUSE("pause", "暂停", List.of()),
    RESUME("resume", "继续播放", List.of()),
    STOP("stop", "停止", List.of()),
    TOGGLE_PLAY_PAUSE("toggle_play_pause", "切换播放/暂停", List.of());

    private final String code;
    private final String name;
    private final List<ActionParamSpec> paramSpecs;

    PlayControlAction(String code, String name, List<ActionParamSpec> paramSpecs) {
        this.code = code;
        this.name = name;
        this.paramSpecs = paramSpecs;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<ActionParamSpec> getParamSpecs() { return paramSpecs; }
}
