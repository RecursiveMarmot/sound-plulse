package com.timess.soundpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.constant.CommonConstant;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.mapper.PlaylistMapper;
import com.timess.soundpulse.model.domain.Playlist;
import com.timess.soundpulse.model.dto.playlist.PlaylistAddRequest;
import com.timess.soundpulse.model.dto.playlist.PlaylistQueryRequest;
import com.timess.soundpulse.model.dto.playlist.PlaylistUpdateRequest;
import com.timess.soundpulse.model.vo.PlaylistVO;
import com.timess.soundpulse.service.PlaylistService;
import com.timess.soundpulse.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【playlist(歌单表)】的数据库操作Service实现
 */
@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist>
    implements PlaylistService {

    @Override
    public long addPlaylist(PlaylistAddRequest playlistAddRequest) {
        ThrowUtils.throwIf(playlistAddRequest == null, ErrorCode.PARAMS_ERROR);
        Playlist playlist = new Playlist();
        BeanUtils.copyProperties(playlistAddRequest, playlist);
        boolean result = this.save(playlist);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return playlist.getId();
    }

    @Override
    public boolean deletePlaylist(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(deleteRequest.getId());
    }

    @Override
    public boolean updatePlaylist(PlaylistUpdateRequest playlistUpdateRequest) {
        ThrowUtils.throwIf(playlistUpdateRequest == null || playlistUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        Playlist playlist = new Playlist();
        BeanUtils.copyProperties(playlistUpdateRequest, playlist);
        return this.updateById(playlist);
    }

    @Override
    public QueryWrapper<Playlist> getQueryWrapper(PlaylistQueryRequest playlistQueryRequest) {
        QueryWrapper<Playlist> queryWrapper = new QueryWrapper<>();
        if (playlistQueryRequest == null) {
            return queryWrapper;
        }

        Long id = playlistQueryRequest.getId();
        Long userId = playlistQueryRequest.getUserId();
        String playlistName = playlistQueryRequest.getPlaylistName();
        String tags = playlistQueryRequest.getTags();
        Integer isPublic = playlistQueryRequest.getIsPublic();
        String sortField = playlistQueryRequest.getSortField();
        String sortOrder = playlistQueryRequest.getSortOrder();

        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.like(StringUtils.isNotBlank(playlistName), "playlist_name", playlistName);
        queryWrapper.like(StringUtils.isNotBlank(tags), "tags", tags);
        queryWrapper.eq(isPublic != null, "is_public", isPublic);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    @Override
    public PlaylistVO getPlaylistVO(Playlist playlist) {
        if (playlist == null) {
            return null;
        }
        PlaylistVO playlistVO = new PlaylistVO();
        BeanUtils.copyProperties(playlist, playlistVO);
        return playlistVO;
    }

    @Override
    public Page<PlaylistVO> getPlaylistVOPage(Page<Playlist> playlistPage) {
        List<Playlist> playlistList = playlistPage.getRecords();
        Page<PlaylistVO> playlistVOPage = new Page<>(playlistPage.getCurrent(), playlistPage.getSize(), playlistPage.getTotal());
        if (playlistList.isEmpty()) {
            return playlistVOPage;
        }
        List<PlaylistVO> playlistVOList = playlistList.stream().map(this::getPlaylistVO).collect(Collectors.toList());
        playlistVOPage.setRecords(playlistVOList);
        return playlistVOPage;
    }
}
