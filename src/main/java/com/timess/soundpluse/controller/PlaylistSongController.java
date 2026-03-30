package com.timess.soundpluse.controller;

import com.timess.soundpluse.common.BaseResponse;
import com.timess.soundpluse.common.DeleteRequest;
import com.timess.soundpluse.common.ResultUtils;
import com.timess.soundpluse.exception.ErrorCode;
import com.timess.soundpluse.exception.ThrowUtils;
import com.timess.soundpluse.model.domain.PlaylistSong;
import com.timess.soundpluse.service.PlaylistSongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/playlistSong")
@Tag(name = "歌单-歌曲关联管理接口")
public class PlaylistSongController {

    @Resource
    private PlaylistSongService playlistSongService;

    @PostMapping("/add")
    @Operation(summary = "添加歌曲到歌单")
    public BaseResponse<Long> addPlaylistSong(@RequestBody PlaylistSong playlistSong) {
        boolean result = playlistSongService.save(playlistSong);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(playlistSong.getId());
    }

    @PostMapping("/delete")
    @Operation(summary = "从歌单移除歌曲")
    public BaseResponse<Boolean> deletePlaylistSong(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean b = playlistSongService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }
}
