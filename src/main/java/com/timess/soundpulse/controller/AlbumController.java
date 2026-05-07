package com.timess.soundpulse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.model.domain.Album;
import com.timess.soundpulse.model.dto.album.AlbumAddRequest;
import com.timess.soundpulse.model.dto.album.AlbumQueryRequest;
import com.timess.soundpulse.model.dto.album.AlbumUpdateRequest;
import com.timess.soundpulse.model.vo.AlbumVO;
import com.timess.soundpulse.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/album")
@Tag(name = "专辑管理接口")
public class AlbumController {

    @Resource
    private AlbumService albumService;

    /**
     * 创建专辑
     */
    @PostMapping("/add")
    @Operation(summary = "创建专辑")
    public BaseResponse<Long> addAlbum(@RequestBody AlbumAddRequest albumAddRequest) {
        long result = albumService.addAlbum(albumAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除专辑
     */
    @PostMapping("/delete")
    @Operation(summary = "删除专辑")
    public BaseResponse<Boolean> deleteAlbum(@RequestBody DeleteRequest deleteRequest) {
        boolean result = albumService.deleteAlbum(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新专辑
     */
    @PostMapping("/update")
    @Operation(summary = "更新专辑")
    public BaseResponse<Boolean> updateAlbum(@RequestBody AlbumUpdateRequest albumUpdateRequest) {
        boolean result = albumService.updateAlbum(albumUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取专辑（VO）
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取专辑")
    public BaseResponse<AlbumVO> getAlbumVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Album album = albumService.getById(id);
        ThrowUtils.throwIf(album == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(albumService.getAlbumVO(album));
    }

    /**
     * 分页获取专辑列表
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取专辑列表", description = "分页获取专辑列表，每页最多50条")
    public BaseResponse<Page<AlbumVO>> listAlbumVOByPage(@RequestBody AlbumQueryRequest albumQueryRequest, HttpServletRequest request) {
        long current = albumQueryRequest.getCurrent();
        long size = albumQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<Album> albumPage = albumService.page(new Page<>(current, size),
                albumService.getQueryWrapper(albumQueryRequest));
        return ResultUtils.success(albumService.getAlbumVOPage(albumPage));
    }
}
