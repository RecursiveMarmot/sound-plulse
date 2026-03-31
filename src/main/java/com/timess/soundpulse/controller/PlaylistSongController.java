package com.timess.soundpulse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.model.domain.PlaylistSong;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongAddRequest;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongQueryRequest;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongUpdateRequest;
import com.timess.soundpulse.model.vo.PlaylistSongVO;
import com.timess.soundpulse.model.vo.SongVO;
import com.timess.soundpulse.service.PlaylistSongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/playlistSong")
@Tag(name = "歌单-歌曲关联管理接口")
public class PlaylistSongController {

    @Resource
    private PlaylistSongService playlistSongService;

    @PostMapping("/add")
    @Operation(summary = "添加歌曲到歌单")
    public BaseResponse<Long> addPlaylistSong(@RequestBody PlaylistSongAddRequest playlistSongAddRequest) {
        ThrowUtils.throwIf(playlistSongAddRequest == null, ErrorCode.PARAMS_ERROR);
        long result = playlistSongService.addPlaylistSong(playlistSongAddRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    @Operation(summary = "从歌单移除歌曲")
    public BaseResponse<Boolean> deletePlaylistSong(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean b = playlistSongService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    @Operation(summary = "更新歌单中的歌曲")
    public BaseResponse<Boolean> updatePlaylistSong(@RequestBody PlaylistSongUpdateRequest playlistSongUpdateRequest) {
        ThrowUtils.throwIf(playlistSongUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        boolean result = playlistSongService.updatePlaylistSong(playlistSongUpdateRequest);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取歌单歌曲关联")
    public BaseResponse<PlaylistSongVO> getPlaylistSongVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        PlaylistSong playlistSong = playlistSongService.getById(id);
        ThrowUtils.throwIf(playlistSong == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(playlistSongService.getPlaylistSongVO(playlistSong));
    }

    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取歌单的歌曲列表", description = "分页获取歌单的歌曲列表，每页最多50条")
    public BaseResponse<Page<PlaylistSongVO>> listPlaylistSongVOByPage(@RequestBody PlaylistSongQueryRequest playlistSongQueryRequest, HttpServletRequest request) {
        long current = playlistSongQueryRequest.getCurrent();
        long size = playlistSongQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<PlaylistSong> playlistSongPage = playlistSongService.page(new Page<>(current, size),
                playlistSongService.getQueryWrapper(playlistSongQueryRequest));
        return ResultUtils.success(playlistSongService.getPlaylistSongVOPage(playlistSongPage));
    }

    @GetMapping("/get/songs")
    @Operation(summary = "根据歌单id获取所有关联的歌曲详细信息")
    public BaseResponse<List<SongVO>> getSongsByPlaylistId(@RequestParam("playlistId") long playlistId) {
        ThrowUtils.throwIf(playlistId <= 0, ErrorCode.PARAMS_ERROR);
        List<SongVO> songVOList = playlistSongService.getSongsByPlaylistId(playlistId);
        return ResultUtils.success(songVOList);
    }
}
