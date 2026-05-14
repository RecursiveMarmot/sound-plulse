package com.timess.soundpulse.assistant.model;

import lombok.Data;

/**
 * 当前播放器状态快照。
 */
@Data
public class MusicPlayerState {

    /**
     * 当前播放的歌曲名称，默认值为"未知"
     */
    private String currentSongName = "未知";

    /**
     * 当前播放的艺术家名称，默认值为"未知"
     */
    private String currentArtist = "未知";

    /**
     * 当前播放位置（秒），默认值为0
     */
    private int currentPosition = 0;

    /**
     * 歌曲总时长（秒），默认值为1
     */
    private int totalDuration = 1;

    /**
     * 音量大小，范围0-100，默认值为50
     */
    private int volume = 50;

    /**
     * 循环模式变量，用于控制播放器的循环播放状态
     * 默认值为"OFF"，表示不启用循环模式
     */
    private String loopMode = "OFF";

    /**
     * 随机播放标志变量，用于控制播放器是否开启随机播放功能
     * 默认值为false，表示不启用随机播放
     */
    private boolean shuffle = false;

    public static MusicPlayerState defaultState() {
        return new MusicPlayerState();
    }
}
