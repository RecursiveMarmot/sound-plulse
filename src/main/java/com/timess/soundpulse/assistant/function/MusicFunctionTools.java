package com.timess.soundpulse.assistant.function;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timess.soundpulse.assistant.function.model.ConfirmActionArgs;
import com.timess.soundpulse.assistant.function.model.ControlPlayerArgs;
import com.timess.soundpulse.assistant.function.model.SearchMusicArgs;
import com.timess.soundpulse.assistant.function.model.ToolResult;
import com.timess.soundpulse.assistant.model.MusicPlayerState;
import com.timess.soundpulse.model.domain.Artist;
import com.timess.soundpulse.model.domain.Song;
import com.timess.soundpulse.model.dto.song.SongQueryRequest;
import com.timess.soundpulse.model.vo.SongVO;
import com.timess.soundpulse.service.ArtistService;
import com.timess.soundpulse.service.SongService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MusicFunctionTools {

    private final SongService songService;
    private final ArtistService artistService;

    @Tool("按条件检索音乐内容，返回候选列表供后续播放或追问确认")
    public ToolResult search_music(@P("搜索参数对象") SearchMusicArgs args) {
        if (args == null) {
            args = new SearchMusicArgs();
        }

        int limit = args.getLimit() == null ? 10 : Math.min(Math.max(args.getLimit(), 1), 20);
        String searchType = StringUtils.defaultIfBlank(args.getSearchType(), "search_song");

        if (!"search_song".equalsIgnoreCase(searchType)) {
            return ToolResult.builder()
                .success(true)
                .type("search_result")
                .message("search_music executed (unsupported searchType fallback)")
                .data(Map.of(
                    "searchType", searchType,
                    "keyword", args.getKeyword(),
                    "artist", args.getArtist(),
                    "album", args.getAlbum(),
                    "limit", limit,
                    "items", List.of()
                ))
                .build();
        }

        String rawKeyword = StringUtils.trimToEmpty(args.getKeyword());
        String rawArtist = StringUtils.trimToEmpty(args.getArtist());
        String normalizedKeyword = rawKeyword;
        String normalizedArtist = rawArtist;

        if (StringUtils.isBlank(normalizedArtist) && StringUtils.isNotBlank(normalizedKeyword)) {
            QueryWrapper<Artist> detectArtistQuery = new QueryWrapper<>();
            detectArtistQuery.like("artist_name", normalizedKeyword);
            detectArtistQuery.last("limit 1");
            Artist detectedArtist = artistService.getOne(detectArtistQuery, false);
            if (detectedArtist != null) {
                normalizedArtist = detectedArtist.getArtistName();
                normalizedKeyword = "";
            }
        }

        if (StringUtils.isNotBlank(normalizedArtist) && StringUtils.equals(normalizedKeyword, normalizedArtist)) {
            normalizedKeyword = "";
        }

        SongQueryRequest queryRequest = new SongQueryRequest();
        queryRequest.setSongName(normalizedKeyword);
        queryRequest.setAlbumName(args.getAlbum());
        queryRequest.setCurrent(1);
        queryRequest.setPageSize(limit);

        if (StringUtils.isNotBlank(normalizedArtist)) {
            QueryWrapper<Artist> artistQuery = new QueryWrapper<>();
            artistQuery.like("artist_name", normalizedArtist);
            artistQuery.last("limit 1");
            Artist matchedArtist = artistService.getOne(artistQuery, false);
            if (matchedArtist != null) {
                queryRequest.setArtistId(matchedArtist.getId());
            }
        }

        Page<Song> songPage = songService.querySong(queryRequest, new Page<>(1, limit));
        List<Map<String, Object>> items = new ArrayList<>();
        for (Song song : songPage.getRecords()) {
            SongVO vo = songService.getSongVO(song);
            Map<String, Object> item = new HashMap<>();
            item.put("id", vo.getId());
            item.put("songName", vo.getSongName());
            item.put("artistId", vo.getArtistId());
            item.put("albumName", vo.getAlbumName());
            item.put("coverUrl", vo.getCoverUrl());
            item.put("songUrl", vo.getSongUrl());
            item.put("duration", vo.getDuration());
            items.add(item);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("searchType", searchType);
        payload.put("keyword", normalizedKeyword);
        payload.put("artist", normalizedArtist);
        payload.put("album", args.getAlbum());
        payload.put("limit", limit);
        payload.put("items", items);
        payload.put("total", songPage.getTotal());

        return ToolResult.builder()
            .success(true)
            .type("search_result")
            .message("search_music executed")
            .data(payload)
            .build();
    }

    @Tool("获取当前播放器状态，用于上下文理解和状态播报")
    public ToolResult get_player_state() {
        MusicPlayerState state = MusicPlayerState.defaultState();
        return ToolResult.builder()
            .success(true)
            .type("player_state")
            .message("get_player_state executed")
            .data(state)
            .build();
    }

    @Tool("执行播放器动作，动作定义遵循 actionType/action/params")
    public ToolResult control_player(@P("播放器控制参数对象") ControlPlayerArgs args) {
        if (args == null) {
            args = new ControlPlayerArgs();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("actionType", args.getActionType());
        payload.put("action", args.getAction());
        payload.put("params", args.getParams());

        return ToolResult.builder()
            .success(true)
            .type("action_result")
            .message("control_player accepted")
            .data(payload)
            .build();
    }

    @Tool("创建待确认动作，不直接执行")
    public ToolResult confirm_action(@P("待确认动作参数对象") ConfirmActionArgs args) {
        if (args == null) {
            args = new ConfirmActionArgs();
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", args.getQuestion());
        payload.put("candidateActions", args.getCandidateActions());

        return ToolResult.builder()
            .success(true)
            .type("confirm_required")
            .message("confirm_action required")
            .data(payload)
            .build();
    }
}
