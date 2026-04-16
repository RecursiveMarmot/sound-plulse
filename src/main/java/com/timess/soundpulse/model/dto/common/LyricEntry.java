package com.timess.soundpulse.model.dto.common;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;


@Data
public class LyricEntry {
    /**
     * 平台
     */
    @SerializedName("platform")
    private String platform;
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("titles")
    private List<String> titles;
    
    @SerializedName("artist")
    private String artist;
    
    @SerializedName("artists")
    private List<String> artists;
    
    @SerializedName("album")
    private List<String> album;
    
    @SerializedName("albums")
    private List<String> albums;
    
    @SerializedName("authorIds")
    private List<String> authorIds;
    
    @SerializedName("authorNames")
    private List<String> authorNames;
    
    @SerializedName("ncmIds")
    private List<String> ncmIds;
    
    @SerializedName("qqIds")
    private List<String> qqIds;
    
    @SerializedName("amIds")
    private List<String> amIds;
    
    @SerializedName("spotifyIds")
    private List<String> spotifyIds;
    
    @SerializedName("file")
    private String file;
    
    @SerializedName("score")
    private int score;
    
    @SerializedName("lyricMatches")
    private List<Object> lyricMatches;

}