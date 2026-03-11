package com.timess.soundplulse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.model.domain.Artist;
import com.timess.soundplulse.model.dto.artist.ArtistAddRequest;
import com.timess.soundplulse.model.dto.artist.ArtistQueryRequest;
import com.timess.soundplulse.model.dto.artist.ArtistUpdateRequest;
import com.timess.soundplulse.model.vo.ArtistVO;

public interface ArtistService extends IService<Artist> {
    /**
     * 创建歌手
     */
    long addArtist(ArtistAddRequest artistAddRequest);

    /**
     * 删除歌手
     */
    boolean deleteArtist(DeleteRequest deleteRequest);

    /**
     * 更新歌手
     */
    boolean updateArtist(ArtistUpdateRequest artistUpdateRequest);

    /**
     * 获取查询条件
     */
    QueryWrapper<Artist> getQueryWrapper(ArtistQueryRequest artistQueryRequest);

    /**
     * 获取脱敏对象
     */
    ArtistVO getArtistVO(Artist artist);

    /**
     * 获取脱敏分页
     */
    Page<ArtistVO> getArtistVOPage(Page<Artist> artistPage);
}
