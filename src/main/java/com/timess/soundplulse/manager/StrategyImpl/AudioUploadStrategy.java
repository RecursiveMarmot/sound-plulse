package com.timess.soundplulse.manager.StrategyImpl;

import com.timess.soundplulse.manager.AbstractCosUpload;
import com.timess.soundplulse.model.enums.FileTypeEnum;
import org.springframework.stereotype.Component;

@Component("audioUploadStrategy")
public class AudioUploadStrategy extends AbstractCosUpload {

    @Override
    protected FileTypeEnum getFileTypeEnum() {
        return FileTypeEnum.AUDIO;
    }
}