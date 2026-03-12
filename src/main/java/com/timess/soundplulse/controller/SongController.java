package com.timess.soundplulse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timess.soundplulse.common.BaseResponse;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.common.ResultUtils;
import com.timess.soundplulse.exception.ErrorCode;
import com.timess.soundplulse.exception.ThrowUtils;
import com.timess.soundplulse.model.domain.Song;
import com.timess.soundplulse.model.dto.song.SongAddRequest;
import com.timess.soundplulse.model.dto.song.SongQueryRequest;
import com.timess.soundplulse.model.dto.song.SongUpdateRequest;
import com.timess.soundplulse.model.vo.SongVO;
import com.timess.soundplulse.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/song")
@Tag(name = "歌曲管理接口")
public class SongController {

    @Resource
    private SongService songService;

    /**
     * 创建歌曲
     */
    @PostMapping(value = "/add")
    @Operation(summary = "创建歌曲", description = "创建一首新歌曲，需要管理员权限")
    // @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE,
    // UserConstant.SUPER_ADMIN_ROLE})
    public BaseResponse<Long> addSong(
            @Parameter(description = "歌曲信息")
            @RequestPart("songInfo") @Valid SongAddRequest songAddRequest,
            @Parameter(description = "音频文件")
            @RequestPart("file") MultipartFile file) {
        long result = songService.addSong(songAddRequest, file);
        return ResultUtils.success(result);
    }

    /**
     * 删除歌曲
     */
    @PostMapping("/delete")
    @Operation(summary = "删除歌曲", description = "根据ID删除歌曲，需要管理员权限")
    // @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE,
    // UserConstant.SUPER_ADMIN_ROLE})
    public BaseResponse<Boolean> deleteSong(@RequestBody DeleteRequest deleteRequest) {
        boolean b = songService.deleteSong(deleteRequest);
        return ResultUtils.success(b);
    }

    /**
     * 更新歌曲
     */
    @PostMapping("/update")
    @Operation(summary = "更新歌曲", description = "更新歌曲信息，需要管理员权限")
    // @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE,
    // UserConstant.SUPER_ADMIN_ROLE})
    public BaseResponse<Boolean> updateSong(@RequestBody SongUpdateRequest songUpdateRequest) {
        boolean result = songService.updateSong(songUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取歌曲
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取歌曲", description = "根据歌曲ID获取歌曲详细信息")
    public BaseResponse<SongVO> getSongVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Song song = songService.getById(id);
        ThrowUtils.throwIf(song == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(songService.getSongVO(song));
    }

    /**
     * 分页获取歌曲列表
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取歌曲列表", description = "分页获取歌曲列表，每页最多50条")
    public BaseResponse<Page<SongVO>> listSongVOByPage(@RequestBody SongQueryRequest songQueryRequest,
            HttpServletRequest request) {
        long current = songQueryRequest.getCurrent();
        long size = songQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<Song> songPage = songService.page(new Page<>(current, size),
                songService.getQueryWrapper(songQueryRequest));
        return ResultUtils.success(songService.getSongVOPage(songPage));
    }
}
