package com.timess.soundplulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundplulse.mapper.UserSongMapper;
import com.timess.soundplulse.model.domain.UserSong;
import com.timess.soundplulse.service.UserSongService;
import org.springframework.stereotype.Service;

/**
 * 针对表【user_song(用户-歌曲关系表)】的数据库操作Service实现
 */
@Service
public class UserSongServiceImpl extends ServiceImpl<UserSongMapper, UserSong>
    implements UserSongService {

}
