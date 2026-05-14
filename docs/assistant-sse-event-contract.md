# 助手 SSE 事件契约（最新）

## 1. 接口

- `POST /assistant/message/stream`
- `Content-Type: text/event-stream`

## 2. 事件类型

当前实现事件：

- `phase`
- `text_delta`
- `action`
- `final`
- `error`
- `complete`

## 3. 事件结构

## 3.1 `phase`

```json
{"stage":"thinking"}
```

或

```json
{"stage":"finalizing"}
```

## 3.2 `text_delta`

```json
{"delta":"正在处理中，请稍候..."}
```

说明：
- 文本按小块连续推送（打字机效果）
- 最终文本与 `final.responseText` 一致

## 3.3 `action`

```json
{
  "actionType": "search",
  "action": "search_song",
  "params": {"keyword": "薛之谦"},
  "originalIntent": "搜索薛之谦的歌曲",
  "confidence": 0.99
}
```

## 3.4 `final`

```json
{
  "actions": [],
  "responseText": "...",
  "needConfirmation": false,
  "context": null
}
```

## 3.5 `error`

```json
{"message":"..."}
```

## 3.6 `complete`

```json
""
```

## 4. 前端判定规则

- `final.actions.length === 0`：普通聊天
- `final.actions.length > 0 && !final.needConfirmation`：执行动作
- `final.actions.length > 0 && final.needConfirmation`：确认后执行

## 5. 推荐 JSONPath

- 文本：`$.delta`（`text_delta`）或 `$.responseText`（`final`）
- 动作：`$.actions`（`final`）
- 确认：`$.needConfirmation`（`final`）
