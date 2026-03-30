package com.timess.soundpluse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timess.soundpluse.constant.CommonConstant;
import com.timess.soundpluse.common.DeleteRequest;
import com.timess.soundpluse.exception.BusinessException;
import com.timess.soundpluse.exception.ErrorCode;
import com.timess.soundpluse.exception.ThrowUtils;
import com.timess.soundpluse.manager.MediaManager;
import com.timess.soundpluse.mapper.SongMapper;
import com.timess.soundpluse.model.domain.Artist;
import com.timess.soundpluse.model.domain.Song;
import com.timess.soundpluse.model.dto.song.SongAddRequest;
import com.timess.soundpluse.model.dto.song.SongQueryRequest;
import com.timess.soundpluse.model.dto.song.SongUpdateRequest;
import com.timess.soundpluse.model.enums.FileTypeEnum;
import com.timess.soundpluse.model.vo.SongVO;
import com.timess.soundpluse.service.ArtistService;
import com.timess.soundpluse.service.SongService;
import com.timess.soundpluse.utils.CommonUtils;
import com.timess.soundpluse.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    MediaManager mediaManager;

    @Autowired
    ArtistService artistService;

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

        File tempFile = null;
        File tempCoverFile = null;
        try {
            // 1. 先创建临时文件保存上传的文件内容
            tempFile = File.createTempFile("audio_", "." + FilenameUtils.getExtension(file.getOriginalFilename()));
            tempCoverFile = File.createTempFile("image_", "." + FilenameUtils.getExtension(coverFile.getOriginalFilename()));
            file.transferTo(tempFile);
            coverFile.transferTo(tempCoverFile);
            //计算duration
            double audioDuration = CommonUtils.getAudioDuration(tempFile);
            //上传音频文件
            String filePath = mediaManager.upload(tempFile, FileTypeEnum.AUDIO);
            song.setSongUrl(filePath);
            //上传封面
            String coverPath = mediaManager.upload(tempCoverFile, FileTypeEnum.IMAGE);
            song.setCoverUrl(coverPath);
            song.setDuration((int) Math.round(audioDuration));
            boolean result = this.save(song);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return song.getId();
        } catch (IOException | TikaException | SAXException e) {
            throw new RuntimeException(e);
        }finally{
            deleteTempFile(tempFile);
            deleteTempFile(tempCoverFile);
        }
    }

    // 添加工具方法
    private void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            try {
                boolean deleted = file.delete();
                if (!deleted) {
                    // 如果立即删除失败，尝试在JVM退出时删除
                    file.deleteOnExit();
                    log.warn("临时文件将在JVM退出时删除: {}", file.getAbsolutePath());
                }
            } catch (Exception e) {
                log.error("删除临时文件失败: {}", file.getAbsolutePath(), e);
            }
        }
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

    /**
     * 查询歌曲列表
     * @param songPage
     * @return
     */
    @Override
    public Page<SongVO> getSongVOPage(Page<Song> songPage) {
        List<Song> songList = songPage.getRecords();
        Page<SongVO> songVOPage = new Page<>(songPage.getCurrent(), songPage.getSize(), songPage.getTotal());
        if (songList.isEmpty()) {
            return songVOPage;
        }
        List<SongVO> songVOList = songList.stream().map(this::getSongVO).collect(Collectors.toList());
        //根据artistId查询歌手信息
        List<Long> artistIds = songList.stream().map(Song::getArtistId).collect(Collectors.toList());
        List<Artist> artistList = artistService.listByIds(artistIds);
        Map<Long, Artist> artistMap = artistList.stream().collect(Collectors.toMap(Artist::getId, artist -> artist));
        songVOList.forEach(songVO -> {
            Artist artist = artistMap.get(songVO.getArtistId());
            if (artist != null)
                songVO.setArtistName(artist.getArtistName());
                });
        songVOPage.setRecords(songVOList);
        return songVOPage;
    }
}
