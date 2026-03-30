package com.timess.soundpluse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundpluse.common.DeleteRequest;
import com.timess.soundpluse.model.domain.Playlist;

/**
 * 针对表【playlist(歌单表)】的数据库操作Service
 */
public interface PlaylistService extends IService<Playlist> {

    /**
     * 添加歌单
     * @param playlist
     * @return
     */
    long addPlaylist(Playlist playlist);

    /**
     * 删除歌单
     * @param deleteRequest
     * @return
     */
    boolean deletePlaylist(DeleteRequest deleteRequest);

    /**
     * 更新歌单
     * @param playlist
     * @return
     */
    boolean updatePlaylist(Playlist playlist);
}
