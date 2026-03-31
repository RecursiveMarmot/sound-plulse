package com.timess.soundpulse.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpulse.constant.CommonConstant;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.cosmanager.CosManager;
import com.timess.soundpulse.exception.BusinessException;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.mapper.ArtistMapper;
import com.timess.soundpulse.model.domain.Artist;
import com.timess.soundpulse.model.dto.artist.ArtistAddRequest;
import com.timess.soundpulse.model.dto.artist.ArtistQueryRequest;
import com.timess.soundpulse.model.dto.artist.ArtistUpdateRequest;
import com.timess.soundpulse.model.enums.FileTypeEnum;
import com.timess.soundpulse.model.vo.ArtistVO;
import com.timess.soundpulse.service.ArtistService;
import com.timess.soundpulse.service.MediaService;
import com.timess.soundpulse.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistServiceImpl extends ServiceImpl<ArtistMapper, Artist> implements ArtistService {

    @Autowired
    CosManager cosManager;

    @Autowired
    MediaService mediaService;

    @Override
    public long addArtist(ArtistAddRequest artistAddRequest) {
        ThrowUtils.throwIf(artistAddRequest == null, ErrorCode.PARAMS_ERROR);
        Artist artist = new Artist();
        BeanUtils.copyProperties(artistAddRequest, artist);
        //上传文件到图片文件到cos
        boolean result = this.save(artist);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return artist.getId();
    }

    @Override
    public boolean deleteArtist(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return this.removeById(deleteRequest.getId());
    }


    /**
     * 更新歌手简介信息
     * @param artistUpdateRequest
     * @return
     */
    @Override
    public boolean updateArtistProfile(ArtistUpdateRequest artistUpdateRequest) {
        if (artistUpdateRequest == null || artistUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //上传头像
        String newAvatarUrl = mediaService.processExternalMedia(artistUpdateRequest.getArtistAvatar());
        artistUpdateRequest.setArtistAvatar(newAvatarUrl);
        Artist artist = this.getById(artistUpdateRequest.getId());
        //删除cos上的旧头像
        cosManager.delete(artist.getArtistAvatar(), FileTypeEnum.IMAGE);
        BeanUtils.copyProperties(artistUpdateRequest, artist);
        boolean result = this.updateById(artist);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
    }

    @Override
    public boolean updateArtistAvatar(long id, MultipartFile file) {
        Artist artist = this.getById(id);
        File tempFile = null;
        try{
            //获取文件后缀
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            // 创建临时文件
            tempFile = File.createTempFile("upload_", "." + suffix);
            // 使用 Spring 工具类复制
            FileCopyUtils.copy(file.getBytes(), tempFile);
            String avatarUrl = cosManager.upload(tempFile, FileTypeEnum.IMAGE);
            artist.setArtistAvatar(avatarUrl);
            updateById(artist);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            // 删除临时文件
            if (tempFile != null){
                FileUtil.del(tempFile);
            }
        }
        return true;
    }


    @Override
    public QueryWrapper<Artist> getQueryWrapper(ArtistQueryRequest artistQueryRequest) {
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        if (artistQueryRequest == null) {
            return queryWrapper;
        }

        Long id = artistQueryRequest.getId();
        String artistName = artistQueryRequest.getArtistName();
        String region = artistQueryRequest.getRegion();
        String sortField = artistQueryRequest.getSortField();
        String sortOrder = artistQueryRequest.getSortOrder();

        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(artistName), "artist_name", artistName);
        queryWrapper.eq(StringUtils.isNotBlank(region), "region", region);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    @Override
    public ArtistVO getArtistVO(Artist artist) {
        if (artist == null) {
            return null;
        }
        ArtistVO artistVO = new ArtistVO();
        BeanUtils.copyProperties(artist, artistVO);
        return artistVO;
    }

    @Override
    public Page<ArtistVO> getArtistVOPage(Page<Artist> artistPage) {
        List<Artist> artistList = artistPage.getRecords();
        Page<ArtistVO> artistVOPage = new Page<>(artistPage.getCurrent(), artistPage.getSize(), artistPage.getTotal());
        if (artistList.isEmpty()) {
            return artistVOPage;
        }
        List<ArtistVO> artistVOList = artistList.stream().map(this::getArtistVO).collect(Collectors.toList());
        artistVOPage.setRecords(artistVOList);
        return artistVOPage;
    }
}
