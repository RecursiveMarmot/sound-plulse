# 助手动作参数契约（最新）

> 对应后端：`assistant/actionsenum/*` + `AssistantChatServiceImpl.validateAction`

## 1. 总体规则

- 动作主键：`actionType.action`
- `params` 必须是 JSON 对象
- 参数基础类型：`STRING | NUMBER | BOOLEAN`
- `search.search_song` 特殊规则：`keyword / artist / album` 三者至少一个非空

## 2. 动作参数明细

## 2.1 play_control

- `play_control.play`：无参数
- `play_control.pause`：无参数
- `play_control.resume`：无参数
- `play_control.stop`：无参数
- `play_control.toggle_play_pause`：无参数

## 2.2 playback_control

- `playback_control.next`：无参数
- `playback_control.previous`：无参数
- `playback_control.replay`：无参数
- `playback_control.show_progress`：无参数
- `playback_control.set_loop_mode`
  - `mode | STRING | 必填 | 循环模式，例如 single/list/random`
- `playback_control.set_shuffle`
  - `enabled | BOOLEAN | 必填 | 是否开启随机播放`

## 2.3 volume_control

- `volume_control.volume_up`：无参数
- `volume_control.volume_down`：无参数
- `volume_control.mute`：无参数
- `volume_control.unmute`：无参数
- `volume_control.toggle_mute`：无参数
- `volume_control.volume_up_small`：无参数
- `volume_control.volume_down_small`：无参数
- `volume_control.set_volume`
  - `level | NUMBER | 必填 | 音量目标值（建议 0-100）`

## 2.4 search

- `search.search_song`
  - `keyword | STRING | 条件必填 | 关键词`
  - `artist | STRING | 条件必填 | 歌手`
  - `album | STRING | 条件必填 | 专辑`
  - 说明：三者至少一个有值即可
- `search.search_album`
  - `keyword | STRING | 必填 | 专辑关键词`
  - `artist | STRING | 非必填 | 歌手名`
- `search.search_artist`
  - `keyword | STRING | 必填 | 歌手关键词`
- `search.search_playlist`
  - `keyword | STRING | 必填 | 歌单关键词`
- `search.search_lyrics`
  - `keyword | STRING | 必填 | 歌词关键词`
  - `artist | STRING | 非必填 | 歌手名`
- `search.add_to_queue`
  - `songId | STRING | 非必填 | 歌曲 ID`
  - `keyword | STRING | 非必填 | 关键词兜底`
- `search.play_next`
  - `songId | STRING | 非必填 | 歌曲 ID`
  - `keyword | STRING | 非必填 | 关键词兜底`
- `search.play_top`
  - `songId | STRING | 非必填 | 歌曲 ID`
  - `keyword | STRING | 非必填 | 关键词兜底`
- `search.clear_queue`：无参数

## 2.5 favorite

- `favorite.like`：无参数
- `favorite.unlike`：无参数
- `favorite.show_favorites`：无参数
- `favorite.add_to_favorites`
  - `songId | STRING | 非必填 | 默认当前歌曲`
- `favorite.remove_from_favorites`
  - `songId | STRING | 非必填 | 默认当前歌曲`

## 2.6 lyric

- `lyric.show_lyrics`：无参数
- `lyric.hide_lyrics`：无参数
- `lyric.jump_to_lyric`
  - `time | NUMBER | 非必填 | 秒`
  - `position | NUMBER | 非必填 | 秒`
