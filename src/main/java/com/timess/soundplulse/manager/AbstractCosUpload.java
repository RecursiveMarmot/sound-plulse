package com.timess.soundplulse.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.timess.soundplulse.exception.ErrorCode;
import com.timess.soundplulse.config.CosClientConfig;
import com.timess.soundplulse.exception.BusinessException;
import com.timess.soundplulse.model.enums.FileTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

public abstract class AbstractCosUpload implements UploadStrategy {

    @Resource
    protected CosClientConfig cosClientConfig;

    @Resource
    protected COSClient cosClient;

    /**
     * 上传主流程
     * @param multipartFile 文件
     * @return
     */
    @Override
    public final String upload(MultipartFile multipartFile) {
        // 进行文件验证
        validate(multipartFile);
        File tempFile = null;
        try {
            // 创建临时文件
            String finalFileName = RandomUtil.randomNumbers(15) + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            tempFile = File.createTempFile(finalFileName, null);
            multipartFile.transferTo(tempFile);
            // 执行文件上传操作
            return doUpload(cosClient, tempFile, multipartFile);
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
    protected abstract String doUpload(COSClient client, File file, MultipartFile multipartFile);
    /**
     * 验证文件
     * @param multipartFile
     */
    private void validate(MultipartFile multipartFile) {
        long fileSize = multipartFile.getSize();
        String fileName = multipartFile.getOriginalFilename();
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
