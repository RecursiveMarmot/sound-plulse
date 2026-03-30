package com.timess.soundpluse.utils;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.util.DigestUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
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
    public static double getAudioDuration(File file) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        ParseContext context = new ParseContext();

        try (InputStream input = new FileInputStream(file)) {
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
