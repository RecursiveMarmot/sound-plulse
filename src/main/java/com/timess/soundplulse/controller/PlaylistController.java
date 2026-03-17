package com.timess.soundplulse.controller;

import com.timess.soundplulse.common.BaseResponse;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.common.ResultUtils;
import com.timess.soundplulse.exception.ErrorCode;
import com.timess.soundplulse.exception.ThrowUtils;
import com.timess.soundplulse.model.domain.Playlist;
import com.timess.soundplulse.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/playlist")
@Tag(name = "歌单管理接口")
public class PlaylistController {

    @Resource
    private PlaylistService playlistService;

    @PostMapping("/add")
    @Operation(summary = "创建歌单")
    public BaseResponse<Long> addPlaylist(@RequestBody Playlist playlist) {
        long result = playlistService.addPlaylist(playlist);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除歌单")
    public BaseResponse<Boolean> deletePlaylist(@RequestBody DeleteRequest deleteRequest) {
        boolean result = playlistService.deletePlaylist(deleteRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新歌单")
    public BaseResponse<Boolean> updatePlaylist(@RequestBody Playlist playlist) {
        boolean result = playlistService.updatePlaylist(playlist);
        return ResultUtils.success(result);
    }

    @GetMapping("/get")
    @Operation(summary = "根据 id 获取歌单")
    public BaseResponse<Playlist> getPlaylistById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Playlist playlist = playlistService.getById(id);
        ThrowUtils.throwIf(playlist == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(playlist);
    }
}
