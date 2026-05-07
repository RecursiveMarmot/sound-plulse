package com.timess.soundpulse.controller;

import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.model.domain.UserPlaylist;
import com.timess.soundpulse.service.UserPlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/userPlaylist")
@Tag(name = "用户-歌单关系管理接口")
public class UserPlaylistController {

    @Resource
    private UserPlaylistService userPlaylistService;

    @PostMapping("/add")
    @Operation(summary = "收藏歌单")
    public BaseResponse<Long> addUserPlaylist(@RequestBody UserPlaylist userPlaylist) {
        boolean result = userPlaylistService.save(userPlaylist);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(userPlaylist.getId());
    }

    @PostMapping("/delete")
    @Operation(summary = "取消收藏歌单")
    public BaseResponse<Boolean> deleteUserPlaylist(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean b = userPlaylistService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }
}
