package com.timess.soundpulse.model.dto.playlist;

import com.timess.soundpulse.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询歌单请求
 */
@Schema(description = "查询歌单请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistQueryRequest extends PageRequest implements Serializable {

    @Schema(description = "歌单ID")
    private Long id;

    @Schema(description = "创建者用户ID")
    private Long userId;

    @Schema(description = "歌单名称")
    private String playlistName;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "是否公开（0-私有，1-公开）")
    private Integer isPublic;

    private static final long serialVersionUID = 1L;
}
