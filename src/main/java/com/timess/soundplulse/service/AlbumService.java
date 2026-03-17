package com.timess.soundplulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.model.domain.Album;

/**
 * 针对表【album(专辑表)】的数据库操作Service
 */
public interface AlbumService extends IService<Album> {

    /**
     * 添加专辑
     * 
     * @param album
     * @return
     */
    long addAlbum(Album album);

    /**
     * 删除专辑
     * 
     * @param deleteRequest
     * @return
     */
    boolean deleteAlbum(DeleteRequest deleteRequest);

    /**
     * 更新专辑
     * 
     * @param album
     * @return
     */
    boolean updateAlbum(Album album);
}
