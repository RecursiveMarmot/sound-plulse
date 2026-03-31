package com.timess.soundpulse.service.impl;

import cn.hutool.core.io.FileUtil;
import com.timess.soundpulse.constant.MediaTypeEnum;
import com.timess.soundpulse.cosmanager.CosManager;
import com.timess.soundpulse.model.enums.FileTypeEnum;
import com.timess.soundpulse.service.MediaService;
import com.timess.soundpulse.model.dto.common.MediaInfoDTO;
import com.timess.soundpulse.strategy.MediaStrategyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;


@Service
@Slf4j
public class MediaServiceImpl implements MediaService {

    @Autowired
    private MediaStrategyManager mediaStrategyManager;

    @Autowired
    private CosManager cosManager;

    /**
     * 处理外部媒体资源
     */
    @Override
    public String processExternalMedia(String url) {
        try {
            // 1. 获取媒体信息
            MediaInfoDTO mediaInfo = mediaStrategyManager.getMediaInfo(url);
            if (mediaInfo == null || !mediaInfo.isSupported()) {
                log.warn("不支持的媒体类型或无法访问: {}", url);
                return url; // 返回原始 URL
            }

            log.info("媒体信息: 类型={}, 大小={} bytes, 格式={}",
                    mediaInfo.getMediaType().getName(),
                    mediaInfo.getContentLength(),
                    mediaInfo.getExtension());

            // 2. 根据类型决定处理方式
            if (mediaInfo.getMediaType() == MediaTypeEnum.IMAGE) {
                return processImage(url, mediaInfo);
            } else if (mediaInfo.getMediaType() == MediaTypeEnum.AUDIO) {
                return processAudio(url, mediaInfo);
            } else if (mediaInfo.getMediaType() == MediaTypeEnum.VIDEO) {
                return processVideo(url, mediaInfo);
            }

            return url;

        } catch (Exception e) {
            log.error("处理外部媒体失败: {}", url, e);
            return url; // 失败时返回原始 URL
        }
    }

    /**
     * 处理图片
     */
    @Override
    public String processImage(String url, MediaInfoDTO mediaInfo) throws Exception {
        // 下载图片
        File imageFile = mediaStrategyManager.downloadMedia(url);
        try {
            // 提取元数据
            Map<String, Object> metadata = mediaStrategyManager.extractMetadata(
                    imageFile, MediaTypeEnum.IMAGE);
            log.info("图片元数据: {}", metadata);
            //上传到cos存储
            return cosManager.upload(imageFile, FileTypeEnum.IMAGE);
        } finally {
            // 清理临时文件
            if (imageFile.exists()) {
                FileUtil.del(imageFile);
                log.info("临时文件已删除: {}", imageFile.getAbsolutePath());
            }
        }
    }

    /**
     * 处理音频
     */
    @Override
    public String processAudio(String url, MediaInfoDTO mediaInfo) throws Exception {
        File audioFile = mediaStrategyManager.downloadMedia(url);

        try {
            Map<String, Object> metadata = mediaStrategyManager.extractMetadata(
                    audioFile, MediaTypeEnum.AUDIO);
            log.info("音频元数据: {}", metadata);
            // 上传到云存储
            return cosManager.upload(audioFile, FileTypeEnum.AUDIO);

        } finally {
            if (audioFile.exists()) {
                FileUtil.del(audioFile);
            }
        }
    }

    /**
     * 处理视频
     */
    @Override
    public String processVideo(String url, MediaInfoDTO mediaInfo) throws Exception {
        File videoFile = mediaStrategyManager.downloadMedia(url);

        try {
            Map<String, Object> metadata = mediaStrategyManager.extractMetadata(
                    videoFile, MediaTypeEnum.VIDEO);
            log.info("视频元数据: {}", metadata);

            return cosManager.upload(videoFile, FileTypeEnum.VIDEO);

        } finally {
            if (videoFile.exists()) {
                FileUtil.del(videoFile);
            }
        }
    }

}