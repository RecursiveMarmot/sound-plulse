package com.timess.soundpulse.model.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Schema(description = "Field description")
@Data
public class SongLyricsSearchRequest implements Serializable {

    @Schema(description = "Field description")
    private Long id;

    @Schema(description = "Field description")
    private String songName;

    private static final long serialVersionUID = 1L;
}
