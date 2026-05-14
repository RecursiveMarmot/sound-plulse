# Function Calling 规格（最新）

## 1. 已注册工具

- `search_music`
- `get_player_state`
- `control_player`
- `confirm_action`

## 2. search_music（重点）

### 入参

```json
{
  "searchType": "search_song",
  "keyword": "薛之谦",
  "artist": "薛之谦",
  "album": "",
  "limit": 20
}
```

### 约束

- `searchType` 仅支持 `search_song`
- `keyword/artist/album` 至少一个有效

### 实现行为

- 若 `keyword` 更像歌手名且 `artist` 为空，自动转成按歌手查询
- 若 `keyword == artist`，自动清空 `keyword` 避免误当歌名过滤
- 实际查询调用 `SongService.querySong(...)`

### 返回

`ToolResult(type=search_result)`，`data` 包含：

- `searchType`
- `keyword`
- `artist`
- `album`
- `limit`
- `total`
- `items`（歌曲列表）

## 3. get_player_state

- 无入参
- 返回当前播放器状态快照

## 4. control_player

### 入参

```json
{
  "actionType": "play_control",
  "action": "pause",
  "params": {}
}
```

- 当前返回回显型 `action_result`，可继续接入真实播放器控制层

## 5. confirm_action

### 入参

```json
{
  "question": "你是要播放《意外》吗？",
  "candidateActions": [
    {
      "actionType": "search",
      "action": "search_song",
      "params": {"keyword": "薛之谦"}
    }
  ]
}
```

- 返回 `confirm_required`

## 6. 注意事项

- 模型最终输出必须是 `LLMResponse` JSON。
- 后端有 JSON 提取容错（允许模型前后夹杂解释文本）。
- `search_song` 动作参数校验已改为组合规则（非 keyword 单必填）。
