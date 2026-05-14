# 动作参数契约表（Enum 强类型版）

## 1. 契约来源

后端以枚举定义参数契约：

- `PlayControlAction`
- `PlaybackControlAction`
- `VolumeControlAction`
- `SearchAction`
- `FavoriteAction`
- `LyricAction`

每个枚举常量都绑定参数定义列表：`List<ActionParamSpec>`。

`ActionParamSpec` 字段：

- `key`：参数名
- `type`：参数类型（`STRING` / `NUMBER` / `BOOLEAN`）
- `required`：是否必填
- `description`：参数说明

## 2. 校验规则（后端）

后端校验流程：

1. 校验 `actionType` 与 `action` 非空
2. 用 `actionType.action` 匹配动作枚举
3. 校验 `params` 必须是 JSON 对象
4. 对每个 `ActionParamSpec` 做：
   - 必填校验
   - 类型校验

校验失败会走异常兜底回复。

## 3. 契约明细

## 3.1 play_control

### `play_control.play`
- 无参数

### `play_control.pause`
- 无参数

### `play_control.resume`
- 无参数

### `play_control.stop`
- 无参数

### `play_control.toggle_play_pause`
- 无参数

## 3.2 playback_control

### `playback_control.next`
- 无参数

### `playback_control.previous`
- 无参数

### `playback_control.replay`
- 无参数

### `playback_control.show_progress`
- 无参数

### `playback_control.set_loop_mode`
- `mode | STRING | 必填 | 循环模式，例如 single/list/random`

### `playback_control.set_shuffle`
- `enabled | BOOLEAN | 必填 | 是否开启随机播放`

## 3.3 volume_control

### `volume_control.volume_up`
- 无参数

### `volume_control.volume_down`
- 无参数

### `volume_control.mute`
- 无参数

### `volume_control.unmute`
- 无参数

### `volume_control.toggle_mute`
- 无参数

### `volume_control.volume_up_small`
- 无参数

### `volume_control.volume_down_small`
- 无参数

### `volume_control.set_volume`
- `level | NUMBER | 必填 | 目标音量，建议范围 0-100`

## 3.4 search

### `search.search_song`
- `keyword | STRING | 必填 | 歌曲关键词`
- `artist | STRING | 非必填 | 歌手名`
- `album | STRING | 非必填 | 专辑名`

### `search.search_album`
- `keyword | STRING | 必填 | 专辑关键词`
- `artist | STRING | 非必填 | 歌手名`

### `search.search_artist`
- `keyword | STRING | 必填 | 歌手关键词`

### `search.search_playlist`
- `keyword | STRING | 必填 | 歌单关键词`

### `search.search_lyrics`
- `keyword | STRING | 必填 | 歌词关键词`
- `artist | STRING | 非必填 | 歌手名`

### `search.add_to_queue`
- `songId | STRING | 非必填 | 歌曲 ID，优先使用`
- `keyword | STRING | 非必填 | 无 songId 时可用关键词兜底`

### `search.play_next`
- `songId | STRING | 非必填 | 歌曲 ID，优先使用`
- `keyword | STRING | 非必填 | 无 songId 时可用关键词兜底`

### `search.play_top`
- `songId | STRING | 非必填 | 歌曲 ID，优先使用`
- `keyword | STRING | 非必填 | 无 songId 时可用关键词兜底`

### `search.clear_queue`
- 无参数

## 3.5 favorite

### `favorite.like`
- 无参数

### `favorite.unlike`
- 无参数

### `favorite.show_favorites`
- 无参数

### `favorite.add_to_favorites`
- `songId | STRING | 非必填 | 未传时默认当前播放歌曲`

### `favorite.remove_from_favorites`
- `songId | STRING | 非必填 | 未传时默认当前播放歌曲`

## 3.6 lyric

### `lyric.show_lyrics`
- 无参数

### `lyric.hide_lyrics`
- 无参数

### `lyric.jump_to_lyric`
- `time | NUMBER | 非必填 | 目标时间（秒），与 position 二选一`
- `position | NUMBER | 非必填 | 目标时间（秒），与 time 二选一`

## 4. 对前端的建议

1. 优先按 `actionType.action` 精确分发执行器。
2. 执行前做一次本地参数校验，规则与后端保持一致。
3. 对可选参数缺失做降级，不要直接崩溃。
4. 对不认识的新动作保留兜底日志，避免影响主流程。
