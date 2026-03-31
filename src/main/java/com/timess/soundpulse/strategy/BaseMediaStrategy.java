package com.timess.soundpulse.strategy;

import com.timess.soundpulse.constant.MediaTypeEnum;
import com.timess.soundpulse.model.dto.common.MediaInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class BaseMediaStrategy implements MediaStrategy {
    
    @Resource
    RestTemplate restTemplate;
    
    @Override
    public MediaInfoDTO getMediaInfo(String url) {
        try {
            // 使用 HEAD 请求获取头信息
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.HEAD,
                    null,
                    Void.class
            );
            
            String contentType = response.getHeaders().getContentType() != null ? 
                    response.getHeaders().getContentType().toString() : null;
            Long contentLength = response.getHeaders().getContentLength();
            long lastModified = response.getHeaders().getLastModified();
            
            MediaTypeEnum mediaType = MediaTypeEnum.fromContentType(contentType);
            
            String fileName = extractFileName(url);
            String extension = extractExtension(fileName);
            
            MediaInfoDTO.MediaInfoDTOBuilder builder = MediaInfoDTO.builder()
                    .url(url)
                    .mediaType(mediaType)
                    .contentType(contentType)
                    .contentLength(contentLength != null ? contentLength : -1)
                    .lastModified(lastModified)
                    .fileName(fileName)
                    .extension(extension)
                    .supported(mediaType == getSupportedType());
            
            return builder.build();
            
        } catch (Exception e) {
            log.error("获取媒体信息失败: {}", url, e);
            return null;
        }
    }
    
    @Override
    public File download(String url) throws Exception {
        MediaInfoDTO info = getMediaInfo(url);
        if (info == null || !info.isSupported()) {
            throw new IllegalArgumentException("不支持的媒体类型: " + url);
        }
        
        // 检查文件大小
        if (info.getContentLength() > getSupportedType().getMaxSize()) {
            throw new IllegalArgumentException(
                String.format("文件过大，超过限制: %dMB", getSupportedType().getMaxSize() / 1024 / 1024)
            );
        }
        
        // 下载文件
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("下载失败，状态码: " + response.getStatusCode());
        }
        
        byte[] fileData = response.getBody();
        if (fileData == null || fileData.length == 0) {
            throw new RuntimeException("下载的文件为空");
        }
        
        // 创建临时文件
        String extension = info.getExtension() != null ? info.getExtension() : getDefaultExtension();
        File tempFile = File.createTempFile(getFilePrefix(), "." + extension);
        
        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileData);
            fos.flush();
        }
        
        // 验证文件
        if (!validate(tempFile)) {
            tempFile.delete();
            throw new IllegalArgumentException("下载的文件验证失败");
        }
        
        log.info("媒体文件下载成功: {}, 大小: {} bytes", tempFile.getAbsolutePath(), fileData.length);
        return tempFile;
    }
    
    protected abstract String getFilePrefix();
    
    protected abstract String getDefaultExtension();
    
    private String extractFileName(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            return URLDecoder.decode(fileName, StandardCharsets.UTF_8.name()).split("\\?")[0];
        } catch (Exception e) {
            log.warn("提取文件名失败: {}", url);
            return "unknown";
        }
    }
    
    private String extractExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1);
        }
        return null;
    }
}