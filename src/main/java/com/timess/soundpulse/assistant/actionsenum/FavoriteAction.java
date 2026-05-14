package com.timess.soundpulse.assistant.actionsenum;

import java.util.List;

public enum FavoriteAction {
    LIKE("like", "喜欢", List.of()),
    UNLIKE("unlike", "取消喜欢", List.of()),
    ADD_TO_FAVORITES("add_to_favorites", "添加到收藏", List.of(
        new ActionParamSpec("songId", ActionParamType.STRING, false, "未传时默认当前播放歌曲")
    )),
    REMOVE_FROM_FAVORITES("remove_from_favorites", "从收藏移除", List.of(
        new ActionParamSpec("songId", ActionParamType.STRING, false, "未传时默认当前播放歌曲")
    )),
    SHOW_FAVORITES("show_favorites", "展示收藏列表", List.of());

    private final String code;
    private final String name;
    private final List<ActionParamSpec> paramSpecs;

    FavoriteAction(String code, String name, List<ActionParamSpec> paramSpecs) {
        this.code = code;
        this.name = name;
        this.paramSpecs = paramSpecs;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<ActionParamSpec> getParamSpecs() { return paramSpecs; }
}
