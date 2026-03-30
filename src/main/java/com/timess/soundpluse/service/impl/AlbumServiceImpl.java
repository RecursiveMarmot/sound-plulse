package com.timess.soundpluse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpluse.common.DeleteRequest;
import com.timess.soundpluse.exception.ErrorCode;
import com.timess.soundpluse.exception.ThrowUtils;
import com.timess.soundpluse.mapper.AlbumMapper;
import com.timess.soundpluse.model.domain.Album;
import com.timess.soundpluse.service.AlbumService;
import org.springframework.stereotype.Service;

/**
 * 针对表【album(专辑表)】的数据库操作Service实现
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album>
    implements AlbumService {

    @Override
    public long addAlbum(Album album) {
        ThrowUtils.throwIf(album == null, ErrorCode.PARAMS_ERROR);
        boolean result = this.save(album);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return album.getId();
    }

    @Override
    public boolean deleteAlbum(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(deleteRequest.getId());
    }

    @Override
    public boolean updateAlbum(Album album) {
        ThrowUtils.throwIf(album == null || album.getId() == null, ErrorCode.PARAMS_ERROR);
        return this.updateById(album);
    }
}
