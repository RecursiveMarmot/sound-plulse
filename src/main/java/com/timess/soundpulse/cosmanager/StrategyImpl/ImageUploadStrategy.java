package com.timess.soundpulse.cosmanager.StrategyImpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.timess.soundpulse.cosmanager.AbstractCosUpload;
import com.timess.soundpulse.model.enums.FileTypeEnum;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component("imageUploadStrategy")
public class ImageUploadStrategy extends AbstractCosUpload {

    @Override
    protected String doUpload(COSClient client, File file) {
        // 生成文件名称和路径
        String originalFilename = file.getName();
        originalFilename = originalFilename.replace(" ", "");
        String suffix = FileUtil.getSuffix(originalFilename);
        String finalFileName = FileUtil.getPrefix(originalFilename) + DateUtil.format(new Date(), "yyyyMMdd") + "_" + RandomUtil.randomString(8) + "." + suffix;
        //获取待上传的数据类型
        String type =  getFileTypeEnum().getValue();
        String finalPath = String.format("%s/%s", type, finalFileName);
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), finalPath, file);
        client.putObject(putObjectRequest);
        return cosClientConfig.getHost() + "/" + finalPath;
    }

    @Override
    protected FileTypeEnum getFileTypeEnum() {
        return FileTypeEnum.IMAGE;
    }
}