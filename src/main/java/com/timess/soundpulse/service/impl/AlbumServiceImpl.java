package com.timess.soundpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.constant.CommonConstant;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.mapper.AlbumMapper;
import com.timess.soundpulse.model.domain.Album;
import com.timess.soundpulse.model.dto.album.AlbumAddRequest;
import com.timess.soundpulse.model.dto.album.AlbumQueryRequest;
import com.timess.soundpulse.model.dto.album.AlbumUpdateRequest;
import com.timess.soundpulse.model.vo.AlbumVO;
import com.timess.soundpulse.service.AlbumService;
import com.timess.soundpulse.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【album(专辑表)】的数据库操作Service实现
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album>
    implements AlbumService {

    @Override
    public long addAlbum(AlbumAddRequest albumAddRequest) {
        ThrowUtils.throwIf(albumAddRequest == null, ErrorCode.PARAMS_ERROR);
        Album album = new Album();
        BeanUtils.copyProperties(albumAddRequest, album);
        boolean result = this.save(album);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return album.getId();
    }

    @Override
    public boolean deleteAlbum(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(deleteRequest.getId());
    }

    @Override
    public boolean updateAlbum(AlbumUpdateRequest albumUpdateRequest) {
        ThrowUtils.throwIf(albumUpdateRequest == null || albumUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        Album album = new Album();
        BeanUtils.copyProperties(albumUpdateRequest, album);
        return this.updateById(album);
    }

    @Override
    public QueryWrapper<Album> getQueryWrapper(AlbumQueryRequest albumQueryRequest) {
        QueryWrapper<Album> queryWrapper = new QueryWrapper<>();
        if (albumQueryRequest == null) {
            return queryWrapper;
        }

        Long id = albumQueryRequest.getId();
        String albumName = albumQueryRequest.getAlbumName();
        Long artistId = albumQueryRequest.getArtistId();
        String artistName = albumQueryRequest.getArtistName();
        String sortField = albumQueryRequest.getSortField();
        String sortOrder = albumQueryRequest.getSortOrder();

        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(albumName), "album_name", albumName);
        queryWrapper.eq(artistId != null, "artist_id", artistId);
        queryWrapper.like(StringUtils.isNotBlank(artistName), "artist_name", artistName);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    @Override
    public AlbumVO getAlbumVO(Album album) {
        if (album == null) {
            return null;
        }
        AlbumVO albumVO = new AlbumVO();
        BeanUtils.copyProperties(album, albumVO);
        return albumVO;
    }

    /**
     * 获取专辑分页
     */
    @Override
    public Page<AlbumVO> getAlbumVOPage(Page<Album> albumPage) {
        List<Album> albumList = albumPage.getRecords();
        Page<AlbumVO> albumVOPage = new Page<>(albumPage.getCurrent(), albumPage.getSize(), albumPage.getTotal());
        if (albumList.isEmpty()) {
            return albumVOPage;
        }
        List<AlbumVO> albumVOList = albumList.stream().map(this::getAlbumVO).collect(Collectors.toList());
        albumVOPage.setRecords(albumVOList);
        return albumVOPage;
    }
}
