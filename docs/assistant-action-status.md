# /assistant/message/stream 状态与前端处理（最新）

## 1. 接口

- 路径：`POST /assistant/message/stream`
- 返回：`SseEmitter`（`text/event-stream`）
- 功能：统一处理普通聊天与操作语义

## 2. 最终判定基于 `final` 事件

`final` 数据类型为 `LLMResponse`，前端按以下规则处理：

1. `actions.length == 0`
- 普通聊天
- 展示 `responseText`

2. `actions.length > 0 && needConfirmation == false`
- 可执行操作
- 按 `actionType.action` 分发执行

3. `actions.length > 0 && needConfirmation == true`
- 待确认操作
- 不执行，先弹确认框

## 3. 当前流式事件顺序

1. `phase(thinking)`
2. `text_delta`（占位 + 打字机分段）
3. `phase(finalizing)`
4. `action`（0..n）
5. `final`（完整结果）
6. `complete`

## 4. 关键说明

- 现在的 `text_delta` 来自最终 `responseText` 的分段发送，和 `final.responseText` 一致。
- `action` 事件仅用于前端预展示；最终执行判断必须以 `final` 为准。
- 出错时会发 `error` 事件。

## 5. 最小前端实现建议

1. 用 `EventSource/fetch-sse` 监听事件流。
2. `text_delta` 追加渲染。
3. 缓存 `action`（可选）。
4. 收到 `final` 后按“聊天/执行/确认”三分支处理。
5. 收到 `complete` 结束 loading。
