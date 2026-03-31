package com.timess.soundpulse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.model.domain.Album;
import com.timess.soundpulse.model.dto.album.AlbumAddRequest;
import com.timess.soundpulse.model.dto.album.AlbumQueryRequest;
import com.timess.soundpulse.model.dto.album.AlbumUpdateRequest;
import com.timess.soundpulse.model.vo.AlbumVO;

/**
 * 针对表【album(专辑表)】的数据库操作Service
 */
public interface AlbumService extends IService<Album> {

    /**
     * 添加专辑
     */
    long addAlbum(AlbumAddRequest albumAddRequest);

    /**
     * 删除专辑
     */
    boolean deleteAlbum(DeleteRequest deleteRequest);

    /**
     * 更新专辑
     */
    boolean updateAlbum(AlbumUpdateRequest albumUpdateRequest);

    /**
     * 获取查询条件
     */
    QueryWrapper<Album> getQueryWrapper(AlbumQueryRequest albumQueryRequest);

    /**
     * 获取专辑脱敏对象
     */
    AlbumVO getAlbumVO(Album album);

    /**
     * 获取专辑脱敏分页
     */
    Page<AlbumVO> getAlbumVOPage(Page<Album> albumPage);
}
