package com.timess.soundplulse.manager.StrategyImpl;

import com.timess.soundplulse.manager.AbstractCosUpload;
import com.timess.soundplulse.model.enums.FileTypeEnum;
import org.springframework.stereotype.Component;

@Component("videoUploadStrategy")
public class VideoUploadStrategy extends AbstractCosUpload {

    @Override
    protected FileTypeEnum getFileTypeEnum() {
        return FileTypeEnum.VIDEO;
    }
}