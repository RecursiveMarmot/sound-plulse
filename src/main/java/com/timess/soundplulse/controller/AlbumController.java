package com.timess.soundplulse.controller;

import com.timess.soundplulse.common.BaseResponse;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.common.ResultUtils;
import com.timess.soundplulse.exception.ErrorCode;
import com.timess.soundplulse.exception.ThrowUtils;
import com.timess.soundplulse.model.domain.Album;
import com.timess.soundplulse.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/album")
@Tag(name = "专辑管理接口")
public class AlbumController {

    @Resource
    private AlbumService albumService;

    @PostMapping("/add")
    @Operation(summary = "创建专辑")
    public BaseResponse<Long> addAlbum(@RequestBody Album album) {
        long result = albumService.addAlbum(album);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除专辑")
    public BaseResponse<Boolean> deleteAlbum(@RequestBody DeleteRequest deleteRequest) {
        boolean result = albumService.deleteAlbum(deleteRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新专辑")
    public BaseResponse<Boolean> updateAlbum(@RequestBody Album album) {
        boolean result = albumService.updateAlbum(album);
        return ResultUtils.success(result);
    }

    @GetMapping("/get")
    @Operation(summary = "根据 id 获取专辑")
    public BaseResponse<Album> getAlbumById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Album album = albumService.getById(id);
        ThrowUtils.throwIf(album == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(album);
    }
}
