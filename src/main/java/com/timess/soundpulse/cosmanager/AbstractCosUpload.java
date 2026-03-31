package com.timess.soundpulse.cosmanager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.COSClient;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.config.CosClientConfig;
import com.timess.soundpulse.exception.BusinessException;
import com.timess.soundpulse.model.enums.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

@Slf4j
public abstract class AbstractCosUpload implements UploadStrategy {

    @Resource
    protected CosClientConfig cosClientConfig;

    @Resource
    protected COSClient cosClient;

    /**
     * 上传主流程
     * @param file 文件
     * @return
     */
    @Override
    public final String upload(File file) {
        // 进行文件验证
        validate(file);
        File tempFile = null;
        try {
            // 创建临时文件
            String finalFileName = RandomUtil.randomNumbers(15) + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            // 执行文件上传操作
            return doUpload(cosClient, file);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败：" + e.getMessage());
        } finally {
            // 删除临时文件
            if (tempFile != null) {
                boolean delete = tempFile.delete();
                if (!delete) {
                    // 打印日志或者处理删除失败
                    System.out.println("临时文件删除失败：" + tempFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 执行上传操作
     * @param client
     * @param file
     * @return
     */
    protected abstract String doUpload(COSClient client, File file);


    /**
     * 根据url删除文件
     * @param url
     * @return
     */
    @Override
    public final void deleteByUrl(String url){
        try{
            cosClient.deleteObject(cosClientConfig.getBucket(), url);
        }catch (Exception e){
            log.error("文件删除失败：{}, 文件存储地址：{}", e.getMessage(), url);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件删除失败：" + e.getMessage() + "文件存储路径为：" + url);
        }
    }
    /**
     * 验证文件
     * @param file
     */
    private void validate(File file) {
        long fileSize = file.length();
        String fileName = file.getName();
        String suffix = FileUtil.getSuffix(fileName);
        
        FileTypeEnum fileTypeEnum = getFileTypeEnum();
        if (fileTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件类型");
        }
        
        // 校验文件大小
        if (fileSize > fileTypeEnum.getMaxSize()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小超出限制");
        }
        
        // 校验后缀
        if (!fileTypeEnum.getSuffixList().contains(suffix.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件格式不支持");
        }
    }

    /**
     * 获取当前策略对应的文件类型
     * @return
     */
    protected abstract FileTypeEnum getFileTypeEnum();

}
