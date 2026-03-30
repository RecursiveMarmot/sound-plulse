package com.timess.soundpluse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpluse.constant.CommonConstant;
import com.timess.soundpluse.common.DeleteRequest;
import com.timess.soundpluse.exception.BusinessException;
import com.timess.soundpluse.exception.ErrorCode;
import com.timess.soundpluse.exception.ThrowUtils;
import com.timess.soundpluse.mapper.ArtistMapper;
import com.timess.soundpluse.model.domain.Artist;
import com.timess.soundpluse.model.dto.artist.ArtistAddRequest;
import com.timess.soundpluse.model.dto.artist.ArtistQueryRequest;
import com.timess.soundpluse.model.dto.artist.ArtistUpdateRequest;
import com.timess.soundpluse.model.vo.ArtistVO;
import com.timess.soundpluse.service.ArtistService;
import com.timess.soundpluse.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistServiceImpl extends ServiceImpl<ArtistMapper, Artist> implements ArtistService {

    @Override
    public long addArtist(ArtistAddRequest artistAddRequest) {
        ThrowUtils.throwIf(artistAddRequest == null, ErrorCode.PARAMS_ERROR);
        Artist artist = new Artist();
        BeanUtils.copyProperties(artistAddRequest, artist);
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

    @Override
    public boolean updateArtist(ArtistUpdateRequest artistUpdateRequest) {
        if (artistUpdateRequest == null || artistUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Artist artist = new Artist();
        BeanUtils.copyProperties(artistUpdateRequest, artist);
        boolean result = this.updateById(artist);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
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
