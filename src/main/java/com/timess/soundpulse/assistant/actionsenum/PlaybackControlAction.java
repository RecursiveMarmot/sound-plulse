package com.timess.soundpulse.assistant.actionsenum;

import java.util.List;

public enum PlaybackControlAction {
    NEXT("next", "下一首", List.of()),
    PREVIOUS("previous", "上一首", List.of()),
    REPLAY("replay", "重播", List.of()),
    SET_LOOP_MODE("set_loop_mode", "设置循环模式", List.of(
        new ActionParamSpec("mode", ActionParamType.STRING, true, "循环模式，例如 single/list/random")
    )),
    SET_SHUFFLE("set_shuffle", "设置随机播放", List.of(
        new ActionParamSpec("enabled", ActionParamType.BOOLEAN, true, "是否开启随机播放")
    )),
    SHOW_PROGRESS("show_progress", "显示播放进度", List.of());

    private final String code;
    private final String name;
    private final List<ActionParamSpec> paramSpecs;

    PlaybackControlAction(String code, String name, List<ActionParamSpec> paramSpecs) {
        this.code = code;
        this.name = name;
        this.paramSpecs = paramSpecs;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<ActionParamSpec> getParamSpecs() { return paramSpecs; }
}
