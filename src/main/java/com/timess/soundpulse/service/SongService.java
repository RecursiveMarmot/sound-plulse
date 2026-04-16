package com.timess.soundpulse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundpulse.common.DeleteRequest;
import com.timess.soundpulse.model.domain.Song;
import com.timess.soundpulse.model.dto.song.SongAddRequest;
import com.timess.soundpulse.model.dto.song.SongQueryRequest;
import com.timess.soundpulse.model.dto.song.SongUpdateRequest;
import com.timess.soundpulse.model.vo.SongVO;
import org.springframework.web.multipart.MultipartFile;

public interface SongService extends IService<Song> {
    /**
     * 创建歌曲
     */
    long addSong(SongAddRequest songAddRequest, MultipartFile file, MultipartFile coverFile);

    /**
     * 删除歌曲
     */
    boolean deleteSong(DeleteRequest deleteRequest);

    /**
     * 更新歌曲
     */
    boolean updateSong(SongUpdateRequest songUpdateRequest);

    /**
     * 获取查询条件
     */
    QueryWrapper<Song> getQueryWrapper(SongQueryRequest songQueryRequest);

    /**
     * 根据歌曲id获取歌词
     * @return
     */
    String getLyricsById(long id);
    /**
     * 获取脱敏对象
     */
    SongVO getSongVO(Song song);
    /**
     * 获取脱敏分页
     */
    Page<SongVO> getSongVOPage(Page<Song> songPage);
}
