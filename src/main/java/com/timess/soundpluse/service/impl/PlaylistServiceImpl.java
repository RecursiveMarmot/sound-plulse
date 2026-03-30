package com.timess.soundpluse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpluse.common.DeleteRequest;
import com.timess.soundpluse.exception.ErrorCode;
import com.timess.soundpluse.exception.ThrowUtils;
import com.timess.soundpluse.mapper.PlaylistMapper;
import com.timess.soundpluse.model.domain.Playlist;
import com.timess.soundpluse.service.PlaylistService;
import org.springframework.stereotype.Service;

/**
 * 针对表【playlist(歌单表)】的数据库操作Service实现
 */
@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist>
    implements PlaylistService {

    @Override
    public long addPlaylist(Playlist playlist) {
        ThrowUtils.throwIf(playlist == null, ErrorCode.PARAMS_ERROR);
        boolean result = this.save(playlist);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return playlist.getId();
    }

    @Override
    public boolean deletePlaylist(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(deleteRequest.getId());
    }

    @Override
    public boolean updatePlaylist(Playlist playlist) {
        ThrowUtils.throwIf(playlist == null || playlist.getId() == null, ErrorCode.PARAMS_ERROR);
        return this.updateById(playlist);
    }
}
