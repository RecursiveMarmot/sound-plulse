package com.timess.soundpulse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timess.soundpulse.common.BaseResponse;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.common.ResultUtils;
import com.timess.soundpulse.exception.BusinessException;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.model.domain.Artist;
import com.timess.soundpulse.model.dto.artist.ArtistAddRequest;
import com.timess.soundpulse.model.dto.artist.ArtistQueryRequest;
import com.timess.soundpulse.model.dto.artist.ArtistUpdateRequest;
import com.timess.soundpulse.model.vo.ArtistVO;
import com.timess.soundpulse.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/artist")
@Tag(name = "歌手管理接口")
public class ArtistController {

    @Resource
    private ArtistService artistService;

    /**
     * 创建歌手
     */
    @PostMapping("/add")
    @Operation(summary = "创建歌手", description = "创建一个新的歌手，需要管理员权限")
//    @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE, UserConstant.SUPER_ADMIN_ROLE})
    public BaseResponse<Long> addArtist(@RequestBody ArtistAddRequest artistAddRequest) {
        long result = artistService.addArtist(artistAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除歌手
     */
    @PostMapping("/delete")
    @Operation(summary = "删除歌手", description = "根据ID删除歌手，需要管理员权限")
//    @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE, UserConstant.SUPER_ADMIN_ROLE})
    public BaseResponse<Boolean> deleteArtist(@RequestBody DeleteRequest deleteRequest) {
        boolean b = artistService.deleteArtist(deleteRequest);
        return ResultUtils.success(b);
    }

    /**
     * 更新歌手简介
     */
    @PostMapping("/update/profile")
    @Operation(summary = "更新歌手简介", description = "更新歌手信息，需要管理员权限")
//    @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE, UserConstant.SUPER_ADMIN_ROLE})
    public BaseResponse<Boolean> updateArtistInfo(@RequestBody ArtistUpdateRequest artistUpdateRequest) {
        boolean result = artistService.updateArtistProfile(artistUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新歌手头像
     */
    @PostMapping("/update/avatar")
    @Operation(summary = "更新歌手头像", description = "更新歌手头像，需要管理员权限")
//    @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE, UserConstant.SUPER_ADMIN_ROLE})
    public BaseResponse<Boolean> updateArtistAvatar(@RequestParam("id") Long id, @RequestPart("avatar") MultipartFile avatar) {
        try {
            boolean result = artistService.updateArtistAvatar(id, avatar);
            return ResultUtils.success(result);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新歌手头像失败");
        }
    }


    /**
     * 根据 id 获取歌手
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取歌手", description = "根据歌手ID获取歌手详细信息")
    public BaseResponse<ArtistVO> getArtistVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Artist artist = artistService.getById(id);
        ThrowUtils.throwIf(artist == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(artistService.getArtistVO(artist));
    }

    /**
     * 分页获取歌手列表
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取歌手列表", description = "分页获取歌手列表，每页最多50条")
    public BaseResponse<Page<ArtistVO>> listArtistVOByPage(@RequestBody ArtistQueryRequest artistQueryRequest, HttpServletRequest request) {
        long current = artistQueryRequest.getCurrent();
        long size = artistQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<Artist> artistPage = artistService.page(new Page<>(current, size),
                artistService.getQueryWrapper(artistQueryRequest));
        return ResultUtils.success(artistService.getArtistVOPage(artistPage));
    }
}
