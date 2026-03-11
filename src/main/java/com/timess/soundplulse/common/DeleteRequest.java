package com.timess.soundplulse.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求类
 */
@Schema(description = "删除请求")
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 7794472253139139229L;

    @Schema(description = "要删除的数据ID")
    private Long id;
}
