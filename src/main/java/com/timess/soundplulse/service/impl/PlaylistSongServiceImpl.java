package com.timess.soundplulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundplulse.mapper.PlaylistSongMapper;
import com.timess.soundplulse.model.domain.PlaylistSong;
import com.timess.soundplulse.service.PlaylistSongService;
import org.springframework.stereotype.Service;

/**
 * 针对表【playlist_song(歌单-歌曲关联表)】的数据库操作Service实现
 */
@Service
public class PlaylistSongServiceImpl extends ServiceImpl<PlaylistSongMapper, PlaylistSong>
    implements PlaylistSongService {

}
