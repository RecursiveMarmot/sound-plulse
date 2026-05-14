# /assistant/action 前端状态说明（最新版）

## 1. 接口定义

- 方法：`POST /assistant/action`
- 入参：`AssistantChatRequest`

```json
{
  "userId": 10001,
  "content": "下一首并把音量调到60"
}
```

- 出参：`BaseResponse<LLMResponse>`

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "actions": [
      {
        "actionType": "volume_control",
        "action": "set_volume",
        "params": {
          "level": 60
        },
        "originalIntent": "把音量调到60",
        "confidence": 0.95
      }
    ],
    "responseText": "好的，音量已调到60",
    "needConfirmation": false,
    "context": {
      "lastAction": "set_volume",
      "ambiguousQuery": null
    }
  }
}
```

## 2. 外层状态（BaseResponse）

- `code = 0`：接口调用成功
- `code != 0`：接口调用失败，按通用错误处理

常见错误码：

- `40000`：请求参数错误
- `40100`：未登录
- `40101`：无权限
- `40300`：禁止访问
- `40400`：数据不存在
- `42500`：请求过于频繁
- `50000`：系统错误
- `50001`：操作失败

## 3. 业务状态（LLMResponse）

`data` 中业务字段：

- `responseText`：展示给用户的文案
- `needConfirmation`：是否需要二次确认
- `actions`：要执行的动作列表（可多动作）
- `context`：上下文信息

推荐前端状态机：

1. `code != 0`：错误态（toast / 错误提示）
2. `code == 0 && needConfirmation == true`：待确认态（弹窗确认）
3. `code == 0 && needConfirmation == false && actions.length > 0`：执行态（按顺序执行动作）
4. `code == 0 && actions.length == 0`：仅回复态（只渲染 `responseText`）

## 4. 动作结构说明

```json
{
  "actionType": "search",
  "action": "search_song",
  "params": {
    "keyword": "稻香",
    "artist": "周杰伦"
  },
  "originalIntent": "播放周杰伦的稻香",
  "confidence": 0.98
}
```

说明：

- `actionType`：动作大类
- `action`：大类下具体动作
- `params`：JSON 对象（后端按枚举参数契约校验）
- `originalIntent`：模型理解到的原始意图
- `confidence`：置信度（0~1）

## 5. 动作类型总览

- `play_control`：播放控制（play/pause/resume/stop/toggle_play_pause）
- `playback_control`：播放流程控制（next/previous/replay/set_loop_mode/set_shuffle/show_progress）
- `volume_control`：音量控制（volume_up/volume_down/set_volume/mute/unmute/toggle_mute/...）
- `search`：搜索与队列（search_song/search_album/search_artist/search_playlist/search_lyrics/add_to_queue/play_next/play_top/clear_queue）
- `favorite`：收藏相关（like/unlike/add_to_favorites/remove_from_favorites/show_favorites）
- `lyric`：歌词相关（show_lyrics/hide_lyrics/jump_to_lyric）

详细参数请看：`docs/action-param-contract.md`

## 6. 关键实现约束（与后端一致）

- `params` 必须是 JSON 对象
- 后端会根据 `actionType.action` 查对应枚举参数定义
- 对定义为必填的参数进行必填校验
- 对参数类型进行校验（STRING / NUMBER / BOOLEAN）
- 不支持的动作会被判定为非法动作

## 7. 兜底场景（前端必须处理）

即使外层 `code = 0`，也可能出现语义失败兜底：

- `actions = []`
- `needConfirmation = false`
- `responseText = "抱歉，我没能理解您的指令，请再说一遍"`

所以前端不要只判断 `code`，必须结合 `actions` 和 `needConfirmation`。

## 8. 前端最小落地建议

1. 始终先渲染 `responseText`。
2. `needConfirmation = true` 时，阻塞动作执行并提示用户确认。
3. 执行动作前，按 `actionType.action` 做本地参数容错。
4. 对执行失败动作做埋点（包含 `actionType/action/params`）。
5. 对空动作响应（`actions=[]`）按普通聊天回复处理。
