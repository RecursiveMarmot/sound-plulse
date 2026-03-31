package com.timess.soundpulse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundpulse.model.domain.PlaylistSong;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongAddRequest;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongQueryRequest;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongUpdateRequest;
import com.timess.soundpulse.model.vo.PlaylistSongVO;
import com.timess.soundpulse.model.vo.SongVO;

import java.util.List;

/**
 * 针对表【playlist_song(歌单-歌曲关联表)】的数据库操作Service
 */
public interface PlaylistSongService extends IService<PlaylistSong> {

    /**
     * 添加歌曲到歌单
     *
     * @param playlistSongAddRequest
     * @return
     */
    long addPlaylistSong(PlaylistSongAddRequest playlistSongAddRequest);

    /**
     * 更新歌单-歌曲关联
     *
     * @param playlistSongUpdateRequest
     * @return
     */
    boolean updatePlaylistSong(PlaylistSongUpdateRequest playlistSongUpdateRequest);

    /**
     * 获取查询包装类
     *
     * @param playlistSongQueryRequest
     * @return
     */
    QueryWrapper<PlaylistSong> getQueryWrapper(PlaylistSongQueryRequest playlistSongQueryRequest);

    /**
     * 获取歌单-歌曲关联封装
     *
     * @param playlistSong
     * @return
     */
    PlaylistSongVO getPlaylistSongVO(PlaylistSong playlistSong);

    /**
     * 分页获取歌单-歌曲关联封装
     *
     * @param playlistSongPage
     * @return
     */
    Page<PlaylistSongVO> getPlaylistSongVOPage(Page<PlaylistSong> playlistSongPage);

    /**
     * 根据歌单id获取所有关联的歌曲详细信息
     *
     * @param playlistId
     * @return
     */
    List<SongVO> getSongsByPlaylistId(long playlistId);
}
