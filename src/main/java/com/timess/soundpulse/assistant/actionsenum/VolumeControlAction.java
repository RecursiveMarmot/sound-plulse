package com.timess.soundpulse.assistant.actionsenum;

import java.util.List;

public enum VolumeControlAction {
    VOLUME_UP("volume_up", "增大音量", List.of()),
    VOLUME_DOWN("volume_down", "减小音量", List.of()),
    SET_VOLUME("set_volume", "设置指定音量", List.of(
        new ActionParamSpec("level", ActionParamType.NUMBER, true, "目标音量，建议范围 0-100")
    )),
    MUTE("mute", "静音", List.of()),
    UNMUTE("unmute", "取消静音", List.of()),
    TOGGLE_MUTE("toggle_mute", "切换静音", List.of()),
    VOLUME_UP_SMALL("volume_up_small", "小幅增大音量", List.of()),
    VOLUME_DOWN_SMALL("volume_down_small", "小幅减小音量", List.of());

    private final String code;
    private final String name;
    private final List<ActionParamSpec> paramSpecs;

    VolumeControlAction(String code, String name, List<ActionParamSpec> paramSpecs) {
        this.code = code;
        this.name = name;
        this.paramSpecs = paramSpecs;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<ActionParamSpec> getParamSpecs() { return paramSpecs; }
}
