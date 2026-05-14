package com.timess.soundpulse.assistant.actionsenum;

import java.util.List;

public enum LyricAction {
    SHOW_LYRICS("show_lyrics", "显示歌词", List.of()),
    HIDE_LYRICS("hide_lyrics", "隐藏歌词", List.of()),
    JUMP_TO_LYRIC("jump_to_lyric", "跳转歌词", List.of(
        new ActionParamSpec("time", ActionParamType.NUMBER, false, "目标时间（秒），与 position 二选一"),
        new ActionParamSpec("position", ActionParamType.NUMBER, false, "目标时间（秒），与 time 二选一")
    ));

    private final String code;
    private final String name;
    private final List<ActionParamSpec> paramSpecs;

    LyricAction(String code, String name, List<ActionParamSpec> paramSpecs) {
        this.code = code;
        this.name = name;
        this.paramSpecs = paramSpecs;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<ActionParamSpec> getParamSpecs() { return paramSpecs; }
}
