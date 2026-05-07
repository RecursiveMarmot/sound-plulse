package com.timess.soundpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpulse.exception.BusinessException;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.exception.ThrowUtils;
import com.timess.soundpulse.mapper.PlaylistSongMapper;
import com.timess.soundpulse.model.domain.Playlist;
import com.timess.soundpulse.model.domain.PlaylistSong;
import com.timess.soundpulse.model.domain.Song;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongAddRequest;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongQueryRequest;
import com.timess.soundpulse.model.dto.playlistsong.PlaylistSongUpdateRequest;
import com.timess.soundpulse.model.vo.PlaylistSongVO;
import com.timess.soundpulse.model.vo.SongVO;
import com.timess.soundpulse.service.PlaylistService;
import com.timess.soundpulse.service.PlaylistSongService;
import com.timess.soundpulse.service.SongService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【playlist_song(歌单-歌曲关联表)】的数据库操作Service实现
 */
@Service
public class PlaylistSongServiceImpl extends ServiceImpl<PlaylistSongMapper, PlaylistSong>
    implements PlaylistSongService {

    @Resource
    @Lazy
    private PlaylistService playlistService;

    @Resource
    @Lazy
    private SongService songService;

    @Override
    public long addPlaylistSong(PlaylistSongAddRequest playlistSongAddRequest) {
        Long playlistId = playlistSongAddRequest.getPlaylistId();
        Long songId = playlistSongAddRequest.getSongId();
        Integer sortOrder = playlistSongAddRequest.getSortOrder();

        ThrowUtils.throwIf(playlistId == null || playlistId <= 0, ErrorCode.PARAMS_ERROR, "歌单ID不能为空");
        ThrowUtils.throwIf(songId == null || songId <= 0, ErrorCode.PARAMS_ERROR, "歌曲ID不能为空");

        Playlist playlist = playlistService.getById(playlistId);
        ThrowUtils.throwIf(playlist == null, ErrorCode.NOT_FOUND_ERROR, "歌单不存在");

        Song song = songService.getById(songId);
        ThrowUtils.throwIf(song == null, ErrorCode.NOT_FOUND_ERROR, "歌曲不存在");

        QueryWrapper<PlaylistSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", playlistId);
        queryWrapper.eq("song_id", songId);
        long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR, "歌曲已存在于歌单中");

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylistId(playlistId);
        playlistSong.setSongId(songId);
        playlistSong.setSortOrder(sortOrder != null ? sortOrder : 0);
        playlistSong.setAddTime(new Date());

        boolean result = this.save(playlistSong);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");

        return playlistSong.getId();
    }

    @Override
    public boolean updatePlaylistSong(PlaylistSongUpdateRequest playlistSongUpdateRequest) {
        Long id = playlistSongUpdateRequest.getId();
        Integer sortOrder = playlistSongUpdateRequest.getSortOrder();

        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "关联ID不能为空");
        PlaylistSong oldPlaylistSong = this.getById(id);
        ThrowUtils.throwIf(oldPlaylistSong == null, ErrorCode.NOT_FOUND_ERROR, "关联不存在");

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setId(id);
        if (sortOrder != null) {
            playlistSong.setSortOrder(sortOrder);
        }

        return this.updateById(playlistSong);
    }

    @Override
    public QueryWrapper<PlaylistSong> getQueryWrapper(PlaylistSongQueryRequest playlistSongQueryRequest) {
        QueryWrapper<PlaylistSong> queryWrapper = new QueryWrapper<>();
        if (playlistSongQueryRequest == null) {
            return queryWrapper;
        }

        Long id = playlistSongQueryRequest.getId();
        Long playlistId = playlistSongQueryRequest.getPlaylistId();
        Long songId = playlistSongQueryRequest.getSongId();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(playlistId != null && playlistId > 0, "playlist_id", playlistId);
        queryWrapper.eq(songId != null && songId > 0, "song_id", songId);

        queryWrapper.orderByAsc("sort_order");
        queryWrapper.orderByDesc("add_time");

        return queryWrapper;
    }

    @Override
    public PlaylistSongVO getPlaylistSongVO(PlaylistSong playlistSong) {
        if (playlistSong == null) {
            return null;
        }

        PlaylistSongVO playlistSongVO = new PlaylistSongVO();
        BeanUtils.copyProperties(playlistSong, playlistSongVO);

        Long songId = playlistSong.getSongId();
        if (songId != null && songId > 0) {
            Song song = songService.getById(songId);
            if (song != null) {
                SongVO songVO = songService.getSongVO(song);
                playlistSongVO.setSong(songVO);
            }
        }

        return playlistSongVO;
    }

    @Override
    public Page<PlaylistSongVO> getPlaylistSongVOPage(Page<PlaylistSong> playlistSongPage) {
        List<PlaylistSong> playlistSongList = playlistSongPage.getRecords();
        Page<PlaylistSongVO> playlistSongVOPage = new Page<>(playlistSongPage.getCurrent(), playlistSongPage.getSize(), playlistSongPage.getTotal());

        if (playlistSongList == null || playlistSongList.isEmpty()) {
            return playlistSongVOPage;
        }

        List<PlaylistSongVO> playlistSongVOList = playlistSongList.stream().map(this::getPlaylistSongVO).collect(Collectors.toList());
        playlistSongVOPage.setRecords(playlistSongVOList);

        return playlistSongVOPage;
    }

    @Override
    public List<SongVO> getSongsByPlaylistId(long playlistId) {
        ThrowUtils.throwIf(playlistId <= 0, ErrorCode.PARAMS_ERROR, "歌单ID错误");

        QueryWrapper<PlaylistSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", playlistId);
        queryWrapper.orderByAsc("sort_order");
        queryWrapper.orderByDesc("add_time");

        List<PlaylistSong> playlistSongList = this.list(queryWrapper);
        if (playlistSongList == null || playlistSongList.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Long> songIds = playlistSongList.stream()
                .map(PlaylistSong::getSongId)
                .collect(Collectors.toList());

        List<Song> songs = songService.listByIds(songIds);
        if (songs == null || songs.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // 把 songs 转成 Map 方便按 sortOrder 排序输出
        java.util.Map<Long, SongVO> songVOMap = songs.stream()
                .map(songService::getSongVO)
                .collect(Collectors.toMap(SongVO::getId, songVO -> songVO));

        return playlistSongList.stream()
                .map(ps -> songVOMap.get(ps.getSongId()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}
