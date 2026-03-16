package com.timess.soundplulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundplulse.constant.CommonConstant;
import com.timess.soundplulse.common.DeleteRequest;
import com.timess.soundplulse.exception.BusinessException;
import com.timess.soundplulse.exception.ErrorCode;
import com.timess.soundplulse.exception.ThrowUtils;
import com.timess.soundplulse.manager.MediaManager;
import com.timess.soundplulse.mapper.SongMapper;
import com.timess.soundplulse.model.domain.Song;
import com.timess.soundplulse.model.dto.song.SongAddRequest;
import com.timess.soundplulse.model.dto.song.SongQueryRequest;
import com.timess.soundplulse.model.dto.song.SongUpdateRequest;
import com.timess.soundplulse.model.enums.FileTypeEnum;
import com.timess.soundplulse.model.vo.SongVO;
import com.timess.soundplulse.service.SongService;
import com.timess.soundplulse.utils.CommonUtils;
import com.timess.soundplulse.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    MediaManager mediaManager;

    /**
     * 添加歌曲
     * @param songAddRequest
     * @param file
     * @param coverFile
     * @return
     */
    @Override
    public long addSong(SongAddRequest songAddRequest, MultipartFile file, MultipartFile coverFile) {
        ThrowUtils.throwIf(songAddRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(file == null, ErrorCode.PARAMS_ERROR);
        Song song = new Song();
        BeanUtils.copyProperties(songAddRequest, song);
        //解析file信息
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        originalFilename = originalFilename.replace(" ", "");
        //设置文件名称
        if (StringUtils.isBlank(song.getSongName())) {
            song.setSongName(originalFilename);
        }
        //上传封面
        String coverPath = mediaManager.upload(coverFile, FileTypeEnum.IMAGE);
        song.setCoverUrl(coverPath);
        //上传音频文件
        String filePath = mediaManager.upload(file, FileTypeEnum.AUDIO);
        song.setSongUrl(filePath);
        //计算duration
        double audioDuration = 0;
        try {
            audioDuration = CommonUtils.getAudioDuration(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TikaException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        song.setDuration((int) Math.round(audioDuration));
        boolean result = this.save(song);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return song.getId();
    }

    @Override
    public boolean deleteSong(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return this.removeById(deleteRequest.getId());
    }

    @Override
    public boolean updateSong(SongUpdateRequest songUpdateRequest) {
        if (songUpdateRequest == null || songUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Song song = new Song();
        BeanUtils.copyProperties(songUpdateRequest, song);
        boolean result = this.updateById(song);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
    }

    @Override
    public QueryWrapper<Song> getQueryWrapper(SongQueryRequest songQueryRequest) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        if (songQueryRequest == null) {
            return queryWrapper;
        }

        Long id = songQueryRequest.getId();
        String songName = songQueryRequest.getSongName();
        Long artistId = songQueryRequest.getArtistId();
        String albumName = songQueryRequest.getAlbumName();
        String sortField = songQueryRequest.getSortField();
        String sortOrder = songQueryRequest.getSortOrder();

        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(songName), "song_name", songName);
        queryWrapper.eq(artistId != null, "artist_id", artistId);
        queryWrapper.like(StringUtils.isNotBlank(albumName), "album_name", albumName);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    @Override
    public SongVO getSongVO(Song song) {
        if (song == null) {
            return null;
        }
        SongVO songVO = new SongVO();
        BeanUtils.copyProperties(song, songVO);
        return songVO;
    }

    @Override
    public Page<SongVO> getSongVOPage(Page<Song> songPage) {
        List<Song> songList = songPage.getRecords();
        Page<SongVO> songVOPage = new Page<>(songPage.getCurrent(), songPage.getSize(), songPage.getTotal());
        if (songList.isEmpty()) {
            return songVOPage;
        }
        List<SongVO> songVOList = songList.stream().map(this::getSongVO).collect(Collectors.toList());
        songVOPage.setRecords(songVOList);
        return songVOPage;
    }
}
