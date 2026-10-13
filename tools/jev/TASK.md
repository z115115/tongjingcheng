# 童锦程jev 判断层校准说明

本目录保留 Jev 判断接口的本地校准脚手架。当前题目口径必须与 Android 端 `JevQuestions.kt` 对齐，并以 `docs/tong-jincheng-skill/SKILL.md` 为唯一人格与认知框架来源。

## 固定判断维度

- `literal_question`：字面表达，还是试探、暗示、阴阳或体面台阶。
- `true_intent`：明确兴趣 / 模糊兴趣 / 礼貌事务 / 具体请求 / 在乎或体面 / 边界拒绝。
- `danger_level`：继续推进的风险，0–9，安全危机优先。
- `should_reply_now`：现在是否存在真实、有用、可执行的一句话。
- `best_action`：直接说、给台阶、解决问题、真诚试一次、停止加码、回到成长、先保安全。
- `she_needs`：真诚 / 体面 / 准备时间 / 解决办法 / 空间 / 无需额外动作。
- `tension_resolved`：紧张或不确定是否已经结束。
- `best_reply`：从 3 条候选中选择最符合童锦程框架的一条。

## 原则

1. 真诚、自律、忠诚、直接、体面。
2. 吸引力大于讨好；长期模糊时停止加码。
3. 给台阶但不操纵；不突袭、不冷战、不测试人性。
4. 经验性判断不是科学定律，不确定就写不确定。
5. 明确拒绝、诈骗、胁迫、暴力、自伤或伤人风险优先按安全问题处理。

## 运行

API Key 只从 `OPENROUTER_API_KEY` 环境变量读取，禁止写入文件或输出：

```bash
python tools/jev/demo_meme.py
python tools/jev/calibrate.py
```

报告输出到 `tools/jev/report/`。运行校准会调用外部付费接口，只有得到明确授权时才执行。
