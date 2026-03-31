package com.timess.soundpulse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.model.domain.Playlist;
import com.timess.soundpulse.model.dto.playlist.PlaylistAddRequest;
import com.timess.soundpulse.model.dto.playlist.PlaylistQueryRequest;
import com.timess.soundpulse.model.dto.playlist.PlaylistUpdateRequest;
import com.timess.soundpulse.model.vo.PlaylistVO;

/**
 * 针对表【playlist(歌单表)】的数据库操作Service
 */
public interface PlaylistService extends IService<Playlist> {

    /**
     * 添加歌单
     */
    long addPlaylist(PlaylistAddRequest playlistAddRequest);

    /**
     * 删除歌单
     */
    boolean deletePlaylist(DeleteRequest deleteRequest);

    /**
     * 更新歌单
     */
    boolean updatePlaylist(PlaylistUpdateRequest playlistUpdateRequest);

    /**
     * 获取查询条件
     */
    QueryWrapper<Playlist> getQueryWrapper(PlaylistQueryRequest playlistQueryRequest);

    /**
     * 获取歌单脱敏对象
     */
    PlaylistVO getPlaylistVO(Playlist playlist);

    /**
     * 获取歌单脱敏分页
     */
    Page<PlaylistVO> getPlaylistVOPage(Page<Playlist> playlistPage);
}
