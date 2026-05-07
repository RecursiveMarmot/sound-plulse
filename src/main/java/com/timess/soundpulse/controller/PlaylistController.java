package com.timess.soundpulse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.model.domain.Playlist;
import com.timess.soundpulse.model.dto.playlist.PlaylistAddRequest;
import com.timess.soundpulse.model.dto.playlist.PlaylistQueryRequest;
import com.timess.soundpulse.model.dto.playlist.PlaylistUpdateRequest;
import com.timess.soundpulse.model.vo.PlaylistVO;
import com.timess.soundpulse.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/playlist")
@Tag(name = "歌单管理接口")
public class PlaylistController {

    @Resource
    private PlaylistService playlistService;

    /**
     * 创建歌单
     */
    @PostMapping("/add")
    @Operation(summary = "创建歌单")
    public BaseResponse<Long> addPlaylist(@RequestBody PlaylistAddRequest playlistAddRequest) {
        long result = playlistService.addPlaylist(playlistAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除歌单
     */
    @PostMapping("/delete")
    @Operation(summary = "删除歌单")
    public BaseResponse<Boolean> deletePlaylist(@RequestBody DeleteRequest deleteRequest) {
        boolean result = playlistService.deletePlaylist(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新歌单
     */
    @PostMapping("/update")
    @Operation(summary = "更新歌单")
    public BaseResponse<Boolean> updatePlaylist(@RequestBody PlaylistUpdateRequest playlistUpdateRequest) {
        boolean result = playlistService.updatePlaylist(playlistUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取歌单（VO）
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取歌单")
    public BaseResponse<PlaylistVO> getPlaylistVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Playlist playlist = playlistService.getById(id);
        ThrowUtils.throwIf(playlist == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(playlistService.getPlaylistVO(playlist));
    }

    /**
     * 分页获取歌单列表
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取歌单列表", description = "分页获取歌单列表，每页最多50条")
    public BaseResponse<Page<PlaylistVO>> listPlaylistVOByPage(@RequestBody PlaylistQueryRequest playlistQueryRequest, HttpServletRequest request) {
        long current = playlistQueryRequest.getCurrent();
        long size = playlistQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<Playlist> playlistPage = playlistService.page(new Page<>(current, size),
                playlistService.getQueryWrapper(playlistQueryRequest));
        return ResultUtils.success(playlistService.getPlaylistVOPage(playlistPage));
    }
}
