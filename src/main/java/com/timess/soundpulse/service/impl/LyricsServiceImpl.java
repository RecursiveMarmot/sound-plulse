package com.timess.soundpulse.service.impl;

import cn.hutool.http.HttpRequest;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.timess.soundpulse.exception.BusinessException;
import com.timess.soundpulse.exception.ErrorCode;
import com.timess.soundpulse.model.dto.common.LyricEntry;
import com.timess.soundpulse.model.dto.common.TtmlLyricsRequest;
import com.timess.soundpulse.service.LyricsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * 歌词服务实现类
 * 支持多个歌词源：LRCLIB、网易云音乐、QQ音乐
 */
@Service
@Slf4j
public class LyricsServiceImpl implements LyricsService {



    @Value("${ttml.list}")
    private String ttmlLyricsListUrl;

    @Value("${ttml.download}")
    private String ttmlLyricsDownloadUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String searchLyricsTTML(TtmlLyricsRequest ttmlLyricsRequest) throws BusinessException {
        Map<String, String> paramMap = new HashMap<>();
        if (StringUtils.isBlank(ttmlLyricsRequest.getTrackName())) {
            throw new IllegalArgumentException("歌曲名不能为空");
        }

        paramMap.put("query", ttmlLyricsRequest.getTrackName());
        paramMap.put("type", "all");

        Gson gson = new Gson();

        try {
            String json = gson.toJson(paramMap);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

            // 第一次请求
            ResponseEntity<String> listResponse = restTemplate.exchange(
                    ttmlLyricsListUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            Type listType = new TypeToken<List<LyricEntry>>() {
            }.getType();
            List<LyricEntry> ttmlLyricList = gson.fromJson(listResponse.getBody(), listType);

            if (ttmlLyricList.isEmpty()) {
                throw new IllegalArgumentException("未找到ttml歌词文件");
            }

            log.info("获取到{}个ttml歌词文件，默认选择第一个", ttmlLyricList.size());
            String id = ttmlLyricList.get(0).getId();

            // 第二次请求
            ResponseEntity<byte[]> downloadResponse = restTemplate.exchange(
                    ttmlLyricsDownloadUrl + id,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            byte[] bytes = downloadResponse.getBody();

            return parseTTMLToTargetFormat(bytes);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
        }
    }

    public static String parseTTMLToTargetFormat(byte[] ttmlBytes) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(ttmlBytes));

            StringBuilder output = new StringBuilder();
            NodeList pNodes = doc.getElementsByTagName("p");

            for (int i = 0; i < pNodes.getLength(); i++) {
                Element p = (Element) pNodes.item(i);
                String begin = p.getAttribute("begin");

                if (begin.isEmpty()) continue;

                // 转换时间格式 00:00.000
                String time = begin.replaceAll("(\\d{2}):(\\d{2})\\.(\\d{3})", "$1:$2.$3");
                output.append("[").append(time).append("] ");

                NodeList spans = p.getChildNodes();
                for (int j = 0; j < spans.getLength(); j++) {
                    Node node = spans.item(j);
                    if (node.getNodeType() == Node.ELEMENT_NODE && "span".equals(node.getNodeName())) {
                        Element span = (Element) node;
                        String text = span.getTextContent();
                        // 格式：<0,0,0>文本
                        output.append("<0,0,0>").append(text);
                    }
                }
                output.append("\n");
            }
            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}