package com.timess.soundpulse.controller;

import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.model.domain.UserSong;
import com.timess.soundpulse.service.UserSongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


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
