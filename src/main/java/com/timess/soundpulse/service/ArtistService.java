package com.timess.soundpulse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.model.domain.Artist;
import com.timess.soundpulse.model.dto.artist.ArtistAddRequest;
import com.timess.soundpulse.model.dto.artist.ArtistQueryRequest;
import com.timess.soundpulse.model.dto.artist.ArtistUpdateRequest;
import com.timess.soundpulse.model.vo.ArtistVO;
import org.springframework.web.multipart.MultipartFile;

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
     * 更新歌手基础信息
     */
    boolean updateArtistProfile(ArtistUpdateRequest artistUpdateRequest);

    /**
     * 更新歌手头像
     * @param id
     * @param file
     * @return
     */
    boolean updateArtistAvatar(long id, MultipartFile file);
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
