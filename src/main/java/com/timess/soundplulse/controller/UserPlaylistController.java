package com.timess.soundplulse.controller;

import com.timess.soundplulse.common.BaseResponse;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.common.ResultUtils;
import com.timess.soundplulse.exception.ErrorCode;
import com.timess.soundplulse.exception.ThrowUtils;
import com.timess.soundplulse.model.domain.UserPlaylist;
import com.timess.soundplulse.service.UserPlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
