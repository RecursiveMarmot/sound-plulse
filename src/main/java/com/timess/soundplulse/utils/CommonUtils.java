package com.timess.soundplulse.utils;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author 33363
 * 工具类
 */
public class CommonUtils {

    /**
     * 密码加密
     * @param userPassword
     * @return
     */
    public static String getEncryptPassword(String userPassword){
        final String SALT = "timess";
        return DigestUtils.md5DigestAsHex(
                (SALT + userPassword).getBytes(StandardCharsets.UTF_8));
    }
    public static double getAudioDuration(MultipartFile file) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler();  // 现在应该可以解析了
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        ParseContext context = new ParseContext();

        try (InputStream input = file.getInputStream()) {
            parser.parse(input, handler, metadata, context);

            // 获取时长（单位：秒）
            String duration = metadata.get("xmpDM:duration");
            if (duration != null) {
                return Double.parseDouble(duration);
            }

            // 有些格式可能使用不同的属性名
            duration = metadata.get("duration");
            if (duration != null) {
                return Double.parseDouble(duration);
            }
        }
        return 0;
    }
}
