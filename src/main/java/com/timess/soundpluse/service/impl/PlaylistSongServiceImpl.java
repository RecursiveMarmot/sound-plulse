package com.timess.soundpluse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpluse.mapper.PlaylistSongMapper;
import com.timess.soundpluse.model.domain.PlaylistSong;
import com.timess.soundpluse.service.PlaylistSongService;
import org.springframework.stereotype.Service;

/**
 * 针对表【playlist_song(歌单-歌曲关联表)】的数据库操作Service实现
 */
@Service
public class PlaylistSongServiceImpl extends ServiceImpl<PlaylistSongMapper, PlaylistSong>
    implements PlaylistSongService {

}
