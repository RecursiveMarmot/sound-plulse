package com.timess.soundpulse.assistant.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * ConversationContext类用于管理对话的上下文信息，包括对话历史、模糊上下文、
 * 音乐播放器状态以及待处理的操作等
 */
@Data
public class ConversationContext {

    private List<String> conversationHistory = new ArrayList<>();

    private String ambiguousContext;

    private MusicPlayerState currentState = MusicPlayerState.defaultState();

}