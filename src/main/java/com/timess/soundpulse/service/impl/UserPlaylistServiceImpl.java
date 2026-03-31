package com.timess.soundpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpulse.mapper.UserPlaylistMapper;
import com.timess.soundpulse.model.domain.UserPlaylist;
import com.timess.soundpulse.service.UserPlaylistService;
import org.springframework.stereotype.Service;

/**
 * 针对表【user_playlist(用户-歌单关系表)】的数据库操作Service实现
 */
@Service
public class UserPlaylistServiceImpl extends ServiceImpl<UserPlaylistMapper, UserPlaylist>
    implements UserPlaylistService {

}
