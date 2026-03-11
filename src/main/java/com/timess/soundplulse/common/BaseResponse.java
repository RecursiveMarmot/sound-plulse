package com.timess.soundplulse.common;

import com.timess.soundplulse.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 全局响应类
 */
@Schema(description = "全局响应结构")
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 6716332470891757076L;

    @Schema(description = "响应码，0表示成功")
    private int code;

    @Schema(description = "响应信息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int code,T data) {
       this(code, "",data);
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(), errorCode.getMessage(), null);
    }

}
