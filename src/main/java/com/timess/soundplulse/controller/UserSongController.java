package com.timess.soundplulse.controller;

import com.timess.soundplulse.common.BaseResponse;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.common.ResultUtils;
import com.timess.soundplulse.exception.ErrorCode;
import com.timess.soundplulse.exception.ThrowUtils;
import com.timess.soundplulse.model.domain.UserSong;
import com.timess.soundplulse.service.UserSongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/userSong")
@Tag(name = "用户-歌曲关系管理接口")
public class UserSongController {

    @Resource
    private UserSongService userSongService;

    @PostMapping("/add")
    @Operation(summary = "添加用户歌曲记录(喜欢/收藏等)")
    public BaseResponse<Long> addUserSong(@RequestBody UserSong userSong) {
        boolean result = userSongService.save(userSong);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(userSong.getId());
    }

    @PostMapping("/delete")
    @Operation(summary = "删除用户歌曲记录")
    public BaseResponse<Boolean> deleteUserSong(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean b = userSongService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }
}
