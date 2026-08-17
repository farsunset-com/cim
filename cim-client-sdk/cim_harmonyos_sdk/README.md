# HKCIMKit

HarmonyOS（OpenHarmony / HarmonyOS NEXT）上的 **CIM 即时通信 TCP 客户端** HAR 包：实现与既有 iOS / Web CIM 协议对齐的二进制帧（TLV 头 + protobuf 负载），并提供 **`HKCIMTool` 单例门面**，便于在 Ability / 页面中快速接入连接、用户绑定、推送与上行发送。

| 项 | 说明 |
|----|------|
| 包名（ohpm） | `hkcimkit` |
| 当前版本 | `1.0.1`（以 [`oh-package.json5`](oh-package.json5) 为准） |
| 入口 | [`Index.ets`](Index.ets)（`main` 字段指向该文件） |
| 协议 | TCP；帧格式 `[1 字节类型][2 字节长度 LE][payload]`，下行推送与应答为 protobuf 子集解析 |
| 许可证 | Apache-2.0 |
| 维护 | zhangshouhai（张寿海） |

---

## 功能概览

- **连接与用户绑定**：`configHost` → `connectionBindUserId`（`client_bind` SentBody）。
- **事件回调**：通过 `HKCIMToolCallbacks` 接收推送消息、绑定结果、连接成功/关闭/错误；无需手写 `Observer` 接口实现（门面内部已注册内核观察者）。
- **前后台**：`enterBackground` 断连省电；`enterForeground` / `reconnect` 在仍保存 `userId` 时自动重连。
- **上行**：`sendSentBody` 通用 SentBody；`sendClientIsReceiveAck` 已读回执（`client_is_receive`）。
- **会话与安全**：`endSessionClearUser` 清空本地用户并断开，避免错误账号被前后台自动重连；`CIM_ACTION_SESSION_REJECTED`（`999`）与推送 `action` 对齐，可在 `onMessage` 中处理踢下线等场景。
- **调试**：`setDebugLog(true)` 同时打开工具层 `【CIM ===>】` 与内核 `[HKCIMKit]` 日志。

更底层的 **`HKCIMKitService`**（内核单例）位于 [`HKCIMKit.ets`](src/main/ets/components/HKCIMKit.ets)，一般业务优先使用 **`HKCIMTool`**；详见源码顶部说明与 [`HKCIMToolUsageExample.ets`](src/main/ets/components/HKCIMToolUsageExample.ets) 中的分步示例。

---

## 环境要求

- DevEco Studio 与工程需支持 **HAR** 模块引用及 **ArkTS**。
- 网络能力：使用 `@ohos.net.socket` 建立 TCP（需在应用侧配置相应网络权限，按项目 `module.json5` 与隐私声明要求添加）。

---

## 安装依赖

在**应用或库工程根目录**（存在 **`oh-package.json5`** 的目录）执行 CLI 安装（包名为 **`hkcimkit`**）：

```bash
ohpm install hkcimkit
```

`install` 可简写为 **`i`**：

```bash
ohpm i hkcimkit
```

指定版本（示例，请以 registry 上实际版本为准）：

```bash
ohpm install hkcimkit@1.0.1
ohpm i hkcimkit@1.0.1
```

---

### 手动写入依赖

若不使用上述命令，也可在 **`oh-package.json5`** 中直接加入依赖（本地路径示例，路径按你的仓库布局修改）：

```json5
{
  "dependencies": {
    "hkcimkit": "file:../path/to/HKCIMKit"
  }
}
```

再在工程根目录执行 **`ohpm install`**（无参数）以同步依赖。

---

### 安装后：在代码中引用

安装或同步依赖完成后，在 ArkTS 源码中：

```typescript
import {
  HKCIMTool,
  HKCIMToolCallbacks,
  HKCIMToolUsageExample,
  CIMMessageModel,
  CIMMessageType,
  CIM_ACTION_SESSION_REJECTED,
  CIMSendMessageData,
  createCIMPeerMessageObserver,
  createCIMConnectionObserver
} from 'hkcimkit';
```

完整导出列表与注释见 [`Index.ets`](Index.ets)。

---

## 快速开始（推荐流程）

1. `const cim = HKCIMTool.getInstance();`
2. （可选）`cim.setDebugLog(true);`
3. （可选）`cim.setDeviceId(...)`、`cim.setChannel(...)`（默认渠道为 `HarmonyOS`，需与服务端约定）
4. `cim.setCallbacks({ ... });` — 实现需要的 `HKCIMToolCallbacks` 字段
5. `cim.configHost('服务器地址', 端口);`
6. `cim.connectionBindUserId('用户ID');`
7. 生命周期：退后台 `cim.enterBackground()`；回前台 `cim.enterForeground()`
8. 登出或会话结束：`cim.endSessionClearUser()` 或 `cim.disconnect()`；页面销毁可 `cim.clearCallbacks()` 避免悬空回调

可复制示例（不连真实服务 / 完整连接等）：`HKCIMToolUsageExample.demoCallbacksOnly()`、`HKCIMToolUsageExample.demoConnect(host, port, userId)`。

---

## 完整连接示例

下面示例演示：**注册回调** → **配置主机与端口** → **`connectionBindUserId` 发起 TCP 与绑定**。请将 `host` / `port` / `userId` 替换为测试或生产环境真实值；`setDeviceId` / `setChannel` 请按与服务端的约定修改。

```typescript
import type { BusinessError } from '@kit.BasicServicesKit';
import {
  HKCIMTool,
  HKCIMToolCallbacks,
  CIMMessageModel,
  CIM_ACTION_SESSION_REJECTED
} from 'hkcimkit';

/**
 * 示例：完整连接流程（请将 host/port/userId 换成测试环境真实值）。
 * @param host 服务器域名或 IP
 * @param port TCP 端口
 * @param userId 业务用户唯一标识
 */
function Connect(host: string, port: number, userId: string): void {
  const cim: HKCIMTool = HKCIMTool.getInstance();
  // 可选：固定设备号、渠道，与服务端约定一致
  cim.setDeviceId('example-device-id');
  cim.setChannel('HarmonyOS');
  cim.setDebugLog(true);

  const callbacks: HKCIMToolCallbacks = {
    onConnectSuccess: (): void => {
      // TCP connect 成功；随后服务端会处理 client_bind，结果见 onBindUserSuccess
      console.info('【CIM ===>】 [示例回调] onConnectSuccess：TCP 已建立');
    },
    onBindUserSuccess: (ok: boolean): void => {
      // ReplyBody.code === '200' 时为 true
      console.info(`【CIM ===>】 [示例回调] onBindUserSuccess：${ok ? '绑定成功' : '绑定失败'}`);
    },
    onMessage: (msg: CIMMessageModel): void => {
      // 服务端推送；若 action 为会话拒绝，应停止自动重连并提示用户
      console.info(`【CIM ===>】 [示例回调] onMessage：action=${msg.action} content=${msg.content}`);
      if (msg.action === CIM_ACTION_SESSION_REJECTED) {
        console.info('【CIM ===>】 [示例回调] 会话被拒绝，将调用 endSessionClearUser（演示）');
        cim.endSessionClearUser();
      }
    },
    onMessageError: (data: Uint8Array): void => {
      console.info(`【CIM ===>】 [示例回调] onMessageError：解析失败，payload 长度=${data.length}`);
    },
    onConnectClose: (): void => {
      console.info('【CIM ===>】 [示例回调] onConnectClose：连接已关闭');
    },
    onConnectError: (error: BusinessError | Error): void => {
      console.info(`【CIM ===>】 [示例回调] onConnectError：${error.message}`);
    }
  };
  cim.setCallbacks(callbacks);
  // 必须先配置地址再绑定用户
  cim.configHost(host, port);
  cim.connectionBindUserId(userId);
}
```

说明：若希望与 ArkTS 常见命名一致，可将函数名 `Connect` 改为 `connect`；逻辑不变。

---

## 日志过滤

| 前缀 | 含义 |
|------|------|
| `【CIM ===>】` | `HKCIMTool` 工具层步骤日志（`setDebugLog(true)` 时；单独调用 `setDebugLog` 也会打印一行带此前缀） |
| `[HKCIMKit]` | 内核 `HKCIMKitService`（同样需要 `setDebugLog(true)`） |
| `【CIM ===>】 [示例回调]` | 示例类中的演示日志，与业务日志区分 |

在 DevEco 日志窗口可搜索 `【CIM ===>` 或 `HKCIMKit`。

---

## 模块结构（简要）

| 路径 | 说明 |
|------|------|
| `Index.ets` | HAR 对外入口与再导出 |
| `HKCIMTool.ets` | 单例门面，封装 `HKCIMKitService` 与回调 |
| `HKCIMKit.ets` | TCP、组帧、protobuf 子集、观察者工厂与服务实现 |

---

## 版本与变更记录

详见 [`CHANGELOG.md`](CHANGELOG.md)。

---

## 相关文档

- 协议与字段约定以源码注释及与服务端统一文档为准；时间戳使用 Cocoa 参考历元毫秒，与 iOS `NSDate.timeIntervalSinceReferenceDate * 1000` 对齐。
