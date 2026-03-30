package com.timess.soundpluse.controller;

import com.timess.soundpluse.common.BaseResponse;
import com.timess.soundpluse.common.DeleteRequest;
import com.timess.soundpluse.common.ResultUtils;
import com.timess.soundpluse.exception.ErrorCode;
import com.timess.soundpluse.exception.ThrowUtils;
import com.timess.soundpluse.model.domain.UserPlaylist;
import com.timess.soundpluse.service.UserPlaylistService;
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
