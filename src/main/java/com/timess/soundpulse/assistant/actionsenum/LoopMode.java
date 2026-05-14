package com.timess.soundpulse.assistant.actionsenum;

/**
 * 循环模式枚举
 */
public enum LoopMode {
    SINGLE("single", "单曲循环"),
    ALL("all", "全部循环"),
    RANDOM("random", "随机播放"),
    OFF("off", "顺序播放");
    
    private String code;
    private String name;
    
    LoopMode(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() { return code; }
    public String getName() { return name; }
}