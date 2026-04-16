package com.timess.soundpulse.controller;

import com.timess.soundpulse.service.LyricsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lyrics")
@Tag(name = "歌词管理")
@Slf4j
@Validated
public class LyricsController {
    
    @Autowired
    private LyricsService lyricsService;

}