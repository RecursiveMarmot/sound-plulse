package com.timess.soundpulse.assistant.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * LLM响应模型类，用于封装大语言模型返回的结果
 * 包含操作列表、回复文本、确认状态和上下文信息等字段
 */
@Data
public class LLMResponse {
    /**
     * 操作列表（支持多个操作）
     */
    @Schema(description = "操作列表（支持多个操作）")
    private List<MusicAction> actions;
    
    /**
     * 给用户的回复文本
     */
    @Schema(description = "给用户的回复文本")
    private String responseText;
    
    /**
     * 是否需要用户确认
     */
    @Schema(description = "是否需要用户确认")
    private Boolean needConfirmation;
    
    /**
     * 上下文信息（用于多轮对话）
     */
    @Schema(description = "上下文信息（用于多轮对话）")
    private ContextInfo context;
    
    /**
     * 上下文信息内部类
     */
    @Data
    public static class ContextInfo {
        /**
         * 记录用户执行的上一个操作类型
         */
        @Schema(description = "上一个操作")
        private String lastAction;
        /**
         * 记录用户最近播放的歌曲ID
         */
        @Schema(description = "上一首歌id")
        private String lastSongId;
        /**
         * 模糊查询信息
         */
        @Schema(description = "模糊查询信息")
        private String ambiguousQuery;
    }
}