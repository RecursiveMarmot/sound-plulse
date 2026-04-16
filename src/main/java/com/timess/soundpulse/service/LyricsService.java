package com.timess.soundpulse.service;

import com.timess.soundpulse.exception.BusinessException;
import com.timess.soundpulse.model.dto.common.TtmlLyricsRequest;

/**
 * 歌词服务接口
 */
public interface LyricsService {

    /**
     * 获取TTML歌词
     * @param ttmlLyricsRequest
     * @return
     */
    String searchLyricsTTML(TtmlLyricsRequest ttmlLyricsRequest) throws BusinessException;

}