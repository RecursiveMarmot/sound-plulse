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

    // 对话历史记录列表，存储对话中的所有文本内容
    private List<String> conversationHistory = new ArrayList<>();

    // 模糊上下文信息，用于处理对话中的不明确或需要进一步澄清的内容
    private String ambiguousContext;

    // 音乐播放器的当前状态，使用MusicPlayerState类进行管理
    private MusicPlayerState currentState = MusicPlayerState.defaultState();

    // 待处理的LLM响应，可能包含需要执行的操作或后续的对话内容
    private LLMResponse pendingAction;
}