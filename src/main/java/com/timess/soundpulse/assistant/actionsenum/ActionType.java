package com.timess.soundpulse.assistant.actionsenum;

/**
 * 操作类型枚举
 */
public enum ActionType {
    PLAY_CONTROL("播放控制"),
    PLAYBACK_CONTROL("播放进度控制"),
    VOLUME_CONTROL("音量控制"),
    SEARCH("搜索点歌"),
    SYSTEM("系统控制"),
    FAVORITE("收藏管理"),
    PLAYLIST("播放列表管理");


    private String description;
    
    ActionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}