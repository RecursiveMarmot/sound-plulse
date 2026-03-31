package com.timess.soundpulse.controller;

import cn.hutool.core.util.StrUtil;

import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.exception.BusinessException;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.model.dto.common.LyricsResponse;
import com.timess.soundpulse.model.dto.common.LyricsSearchRequest;
import com.timess.soundpulse.service.LyricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/lyrics")
@Tag(name = "歌词管理")
@Slf4j
@Validated
public class LyricsController {
    
    @Autowired
    private LyricsService lyricsService;
    
    /**
     * 搜索歌词
     */
    @PostMapping("/search")
    @Operation(summary = "搜索歌词", description = "根据歌曲名和歌手名搜索歌词，返回LRC格式歌词")
    public BaseResponse<LyricsResponse> searchLyrics(@Valid @RequestBody LyricsSearchRequest request) {
        try {
            log.info("搜索歌词请求: 歌曲={}, 歌手={}", request.getTrackName(), request.getArtistName());
            
            LyricsResponse response = lyricsService.searchLyrics(request);
            
            if (!response.getSuccess()) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, response.getMessage());
            }
            
            return ResultUtils.success(response);
            
        } catch (Exception e) {
            log.error("搜索歌词失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"搜索歌词失败");
        }
    }
    
    /**
     * 批量搜索歌词（带容错）
     */
    @PostMapping("/batch-search")
    @Operation(summary = "批量搜索歌词", description = "智能搜索歌词，包含模糊匹配和重试机制")
    public BaseResponse<LyricsResponse> batchSearchLyrics(@Valid @RequestBody LyricsSearchRequest request) {
        try {
            LyricsResponse response = lyricsService.batchSearchLyrics(request);
            
            if (!response.getSuccess()) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, response.getMessage());
            }
            
            return ResultUtils.success(response);
            
        } catch (Exception e) {
            log.error("批量搜索歌词失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"搜索歌词失败");
        }
    }
    
    /**
     * 根据ID获取歌词
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "获取歌词详情", description = "根据歌词ID获取详细信息")
    public BaseResponse<LyricsResponse> getLyricsById(
            @PathVariable @NotNull @Min(1) @Parameter(description = "歌词ID", example = "1") Long id) {
        try {
            LyricsResponse response = lyricsService.getLyricsById(id);
            
            if (!response.getSuccess()) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, response.getMessage());
            }
            
            return ResultUtils.success(response);
            
        } catch (Exception e) {
            log.error("获取歌词失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"搜索歌词失败");
        }
    }
    
    /**
     * 根据歌曲名快速搜索
     */
    @GetMapping("/quick-search")
    @Operation(summary = "快速搜索", description = "根据歌曲名快速搜索歌词")
    public BaseResponse<LyricsResponse> quickSearch(
            @RequestParam @Parameter(description = "歌曲名", example = "Never Gonna Give You Up") String trackName,
            @RequestParam @Parameter(description = "歌手名", example = "Rick Astley") String artistName) {
        
        if (StrUtil.isBlank(trackName) || StrUtil.isBlank(artistName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "歌曲名和歌手名不能为空");
        }
        
        LyricsSearchRequest request = new LyricsSearchRequest();
        request.setTrackName(trackName);
        request.setArtistName(artistName);
        
        return searchLyrics(request);
    }
}