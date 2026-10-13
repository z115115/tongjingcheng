package com.jev.probe.jev

import com.jev.probe.core.ChatSnapshot
import com.jev.probe.core.Prefs
import com.jev.probe.core.kb.ChatContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * The generative route: any OpenAI-compatible `/chat/completions` endpoint.
 * Drafts the 3 candidate replies, and (D stage) summarizes text. Reads
 * replyBaseUrl / replyKey / replyModel from [Prefs].
 */
class ReplyClient(private val prefs: Prefs) {

    /**
     * Exactly 3 varied candidate replies in Chinese.
     *
     * @param ctx D-stage knowledge context. When present its background and
     *        history are prepended to the prompt with an instruction to stay
     *        consistent with them and invent nothing beyond them.
     */
    fun draft(snapshot: ChatSnapshot, relationship: String, ctx: ChatContext? = null): List<String> {
        val convo = snapshot.messages.takeLast(10).joinToString("\n") {
            (if (it.side == "me") "我" else "对方") + "：" + it.text
        }
        val sys = "你是童锦程jev，用童锦程视角帮助用户拟下一句话，但候选回复必须以用户本人的口吻发送，" +
            "不能冒充童锦程，也不能把兄弟们、深情祖师爷等人设词硬塞进对话。核心顺序：真诚、自律、忠诚、直接、体面。" +
            "判断时使用这些框架：吸引力大于讨好；给对方一个体面且可拒绝的台阶；人性不可考验，不用突袭、冷战或试探；" +
            "反复炫耀和自我标签可能暴露不安全感，但这只是概率信号；真喜欢可以真实地试一次，长期模糊或明确拒绝就停止加码；" +
            "遇到冲突不发泄，直接想解决办法；瓶颈期把精力放回读书、健身和事业；钱与时间优先给真正重要、长期互相负责的人。" +
            "不把街头经验包装成科学定律，不编造童锦程没说过的立场；商业、创业等素材不足的领域要承认只能按人性逻辑推断。" +
            "不读心，不承诺话术能让特定的人喜欢用户。明确拒绝、不适或危险时停止推进；出现威胁、胁迫、跟踪、诈骗、" +
            "自伤或伤人风险时优先安全处置。只输出一个 JSON 数组，含且仅含 3 条可直接发送的中文候选回复。" +
            "三条依次为：真诚直接、给台阶的自然推进、保留自尊与退出空间。每条只做一个动作，不操控、不羞辱、不施压，" +
            "不编造事实。每条不超过 60 字，口语、短句、先结论后动作，像真人聊天。不要输出分析、Markdown、标题或解释，" +
            "直接输出 JSON 数组，例如：[\"回复一\",\"回复二\",\"回复三\"]。"
        val user = knowledgeBlock(relationship, ctx) +
            "关系：$relationship\n\n最近对话：\n$convo\n\n请给出 3 条候选回复。"
        return ReplyParser.parseThree(chat(sys, user, temperature = 0.8))
    }

    /** The background + history preamble; empty string when there is no context. */
    private fun knowledgeBlock(relationship: String, ctx: ChatContext?): String {
        ctx ?: return ""
        val background = ctx.background(relationship)
        val history = ctx.history
        if (background.isBlank() && history.isEmpty()) return ""
        val sb = StringBuilder()
        sb.append("以下包含联系人背景、用户笔记和童锦程视角自动主题卡。联系人背景与用户笔记可作为事实；")
            .append("自动主题卡只是决策框架，不能当作对方事实。回复必须与已知事实一致，不要编造。\n")
        if (background.isNotBlank()) sb.append(background).append('\n')
        if (history.isNotEmpty()) {
            sb.append("\n更早的聊天记录（越靠下越新）：\n")
            history.takeLast(prefs.contextHistoryCount.coerceIn(0, 100)).forEach {
                sb.append(if (it.side == "me") "我：" else "对方：").append(it.text).append('\n')
            }
        }
        sb.append('\n')
        return sb.toString()
    }

    /**
     * One plain chat round trip for the settings connectivity test. Deliberately
     * NOT [summarize]: the test should exercise the ordinary path, not whatever
     * the summary prompt happens to be.
     */
    fun ping(): String =
        chat("你是连通性测试助手，只按要求回答，不要解释。", "请只回复两个字：收到", temperature = 0.0).trim()

    /** Condense a block of text (used by the D-stage contact auto-summary). */
    fun summarize(text: String): String {
        if (text.isBlank()) return ""
        val sys = "你是中文摘要助手。把给到的聊天记录压缩成不超过 120 字的第三人称要点摘要，" +
            "只保留事实、偏好、承诺和待办，不要评论，不要编造。直接输出摘要正文。"
        return chat(sys, text, temperature = 0.2).trim()
    }

    /** One chat-completions round trip; returns the assistant message content. */
    private fun chat(system: String, user: String, temperature: Double): String {
        val url = prefs.replyEndpoint()
        val messages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", system))
            .put(JSONObject().put("role", "user").put("content", user))
        val body = JSONObject()
            .put("model", prefs.replyModel)
            .put("messages", messages)
            .put("temperature", temperature)
        val resp = HttpJson.post(url, prefs.effectiveReplyKey(), body, Route.REPLY, HttpJson.headersFor(url))
        return resp.optJSONArray("choices")?.optJSONObject(0)
            ?.optJSONObject("message")?.optString("content") ?: ""
    }

}
