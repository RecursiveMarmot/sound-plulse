package com.timess.soundpulse.assistant.function.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "search_music function 参数")
public class SearchMusicArgs {

    @Schema(description = "检索类型", allowableValues = {"search_song"})
    private String searchType;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "歌手（可选）")
    private String artist;

    @Schema(description = "专辑（可选）")
    private String album;

    @Schema(description = "返回数量，默认 10，最大 20")
    private Integer limit;
}
