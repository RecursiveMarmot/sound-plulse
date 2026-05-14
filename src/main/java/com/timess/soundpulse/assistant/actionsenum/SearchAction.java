package com.timess.soundpulse.assistant.actionsenum;

import java.util.List;

public enum SearchAction {
    SEARCH_SONG("search_song", "搜索歌曲", List.of(
        new ActionParamSpec("keyword", ActionParamType.STRING, true, "歌曲关键词"),
        new ActionParamSpec("artist", ActionParamType.STRING, false, "歌手名"),
        new ActionParamSpec("album", ActionParamType.STRING, false, "专辑名")
    )),
    SEARCH_ALBUM("search_album", "搜索专辑", List.of(
        new ActionParamSpec("keyword", ActionParamType.STRING, true, "专辑关键词"),
        new ActionParamSpec("artist", ActionParamType.STRING, false, "歌手名")
    )),
    SEARCH_ARTIST("search_artist", "搜索歌手", List.of(
        new ActionParamSpec("keyword", ActionParamType.STRING, true, "歌手关键词")
    )),
    SEARCH_PLAYLIST("search_playlist", "搜索歌单", List.of(
        new ActionParamSpec("keyword", ActionParamType.STRING, true, "歌单关键词")
    )),
    SEARCH_LYRICS("search_lyrics", "搜索歌词", List.of(
        new ActionParamSpec("keyword", ActionParamType.STRING, true, "歌词关键词"),
        new ActionParamSpec("artist", ActionParamType.STRING, false, "歌手名")
    )),
    ADD_TO_QUEUE("add_to_queue", "添加到播放队列", List.of(
        new ActionParamSpec("songId", ActionParamType.STRING, false, "歌曲 ID，优先使用"),
        new ActionParamSpec("keyword", ActionParamType.STRING, false, "无 songId 时可用关键词兜底")
    )),
    PLAY_NEXT("play_next", "下一首播放", List.of(
        new ActionParamSpec("songId", ActionParamType.STRING, false, "歌曲 ID，优先使用"),
        new ActionParamSpec("keyword", ActionParamType.STRING, false, "无 songId 时可用关键词兜底")
    )),
    PLAY_TOP("play_top", "置顶播放", List.of(
        new ActionParamSpec("songId", ActionParamType.STRING, false, "歌曲 ID，优先使用"),
        new ActionParamSpec("keyword", ActionParamType.STRING, false, "无 songId 时可用关键词兜底")
    )),
    CLEAR_QUEUE("clear_queue", "清空播放队列", List.of());

    private final String code;
    private final String name;
    private final List<ActionParamSpec> paramSpecs;

    SearchAction(String code, String name, List<ActionParamSpec> paramSpecs) {
        this.code = code;
        this.name = name;
        this.paramSpecs = paramSpecs;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<ActionParamSpec> getParamSpecs() { return paramSpecs; }
}
