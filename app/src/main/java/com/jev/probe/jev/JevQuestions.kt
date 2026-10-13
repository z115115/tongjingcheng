package com.jev.probe.jev

import com.jev.probe.core.ChatSnapshot
import com.jev.probe.core.kb.LogEntry
import org.json.JSONArray
import org.json.JSONObject

/** Jev questions distilled from the bundled Tong Jincheng perspective skill. */
object JevQuestions {
    const val BACKGROUND_NOTE =
        " Facts in background are context. Automatic Tong Jincheng cards are decision guidance, not facts. " +
        "Treat the framework as practical street wisdom, not scientific certainty."

    private fun noul(instructions: String, t: String, f: String) = JSONObject().apply {
        put("type", "noul")
        put("instructions", instructions + BACKGROUND_NOTE)
        put("criteria", JSONObject().put("true", t).put("false", f))
    }

    private fun choice(instructions: String, criteria: Map<String, String>) = JSONObject().apply {
        put("type", "choice")
        put("instructions", instructions + BACKGROUND_NOTE)
        put("criteria", JSONObject().also { c -> criteria.forEach { (k, v) -> c.put(k, v) } })
    }

    private fun score(instructions: String, levels: List<String>) = JSONObject().apply {
        put("type", "score")
        put("instructions", instructions + BACKGROUND_NOTE)
        put("criteria", JSONArray().also { a -> levels.forEach { a.put(it) } })
    }

    fun judge(): JSONObject = JSONObject().apply {
        put("literal_question", noul(
            "Is the latest message direct and literal rather than a test, face-saving hint, performance, or hidden complaint? " +
                "Use the whole visible thread and do not invent motives.",
            "The message plainly states the feeling, request, boundary, plan, acceptance, or rejection.",
            "The surface wording hides a test of interest, a need for a face-saving reason, sarcasm, insecurity signaling, or an implied complaint."
        ))
        put("true_intent", choice(
            "What is the best-supported social signal in the latest message? Prefer repeated behavior over flattering words. " +
                "Do not label ambiguity as attraction. A clear boundary or rejection remains rejection even if phrased politely.",
            linkedMapOf(
                "clear_interest" to "They clearly invest, initiate, accept a concrete plan, offer an alternative, or otherwise make mutual interest easy to see.",
                "ambiguous_interest" to "There may be warmth, teasing, or attention, but no reliable reciprocal action yet. The user would still have to guess.",
                "polite_or_logistic" to "The message is ordinary politeness, casual talk, or practical logistics without evidence of romantic interest.",
                "asks_for_action" to "They directly want a fact, action, time, explanation, apology, or commitment.",
                "seeks_care_or_face" to "They want care, respect, reassurance, or a face-saving way to accept, decline, or recover from awkwardness.",
                "boundary_or_rejection" to "They set a boundary, ask for space, decline, end the relationship, or show a sustained lack of reciprocal investment."
            )
        ))
        put("danger_level", score(
            "How risky would it be to push the relationship in the next message? Score the present scene, including consent, fraud, threats, and clear boundaries.",
            listOf(
                "Warm mutual chat or a confirmed plan; no pressure, complaint, or safety issue.",
                "Neutral chat or logistics; a normal short reply is low risk.",
                "Interest is uncertain, but one respectful low-pressure message would be acceptable.",
                "One-sided effort, mild disappointment, or awkwardness; more chasing may reduce dignity or attraction.",
                "Cold, inconsistent, or sarcastic signals; a pushy reply could escalate tension.",
                "A direct complaint, broken promise, or visible loss of trust requires honest repair before any flirting.",
                "A clear request for space, repeated cancellation without alternatives, or a serious conflict makes continued pursuit harmful.",
                "A firm rejection, breakup, financial pressure, controlling behavior, or repeated boundary violation is present.",
                "An ultimatum, coercion, stalking, fraud pattern, self-harm threat, or credible threat is active; ordinary relationship tactics are unsafe.",
                "Immediate danger, violence, forced sexual conduct, extortion, or an active self-harm or harm-to-others crisis is present."
            )
        ))
        put("should_reply_now", noul(
            "Is there a truthful, useful next message the user should send now? True requires a direct answer, concrete plan, sincere repair, or one low-pressure attempt supported by the thread. " +
                "False when the user would be guessing, testing, chasing a clear rejection, performing, or adding words after closure.",
            "A short truthful reply can answer the request, solve a real problem, make one respectful attempt, or give a needed face-saving option.",
            "More words would be guessing, testing human nature, over-investing in ambiguity, ignoring a boundary, or reopening a closed exchange."
        ))
        put("best_action", choice(
            "Choose the next action using Tong Jincheng's core heuristics: attraction over pleasing, give a face-saving step, do not test human nature, be direct, solve instead of getting angry, and step back from sustained ambiguity.",
            linkedMapOf(
                "be_direct" to "Say the honest feeling, answer, boundary, or apology plainly. Do not hide behind games or a fake persona.",
                "give_step" to "Offer a concrete, low-pressure, face-saving option with an easy way to decline or prepare.",
                "solve_problem" to "Address the actual issue with facts, responsibility, and a specific next step instead of venting anger.",
                "try_once" to "Make one sincere, specific attempt because genuine interest is worth testing through action, not endless analysis.",
                "step_back" to "Stop adding effort where attraction is unclear, reciprocal action is absent, the topic is closed, or a boundary was stated.",
                "invest_self" to "Put attention back into work, study, fitness, or stable routines rather than chasing or drinking to escape.",
                "protect_safety" to "Stop ordinary relationship tactics, protect money or physical safety, preserve evidence, and seek trusted or emergency support as appropriate."
            )
        ))
        put("she_needs", choice(
            "What does the other person most need from the user right now? Choose only what the visible thread supports. " +
                "A clear rejection means space, not persuasion. A face-saving option must never disguise pressure.",
            linkedMapOf(
                "sincerity" to "A direct, truthful acknowledgment, answer, apology, or statement of intent.",
                "face" to "A dignified option or explanation that lets them accept, decline, or recover without embarrassment.",
                "preparation" to "Advance notice, time to prepare, or a concrete plan rather than a surprise test.",
                "solution" to "A practical action that resolves the named problem or fulfills a clear commitment.",
                "space" to "Less pursuit, no further persuasion, or respect for a boundary, rejection, or closed topic.",
                "nothing" to "No special response is needed; the exchange is casual, complete, or already mutually understood."
            )
        ))
        put("tension_resolved", noul(
            "Is the social tension or uncertainty resolved enough that no repair or clarification remains? " +
                "Do not call it resolved merely because one person stopped replying.",
            "They clearly accepted, confirmed, declined, closed the topic, or returned to warm mutual interaction.",
            "A complaint, ambiguity, broken commitment, boundary, safety issue, or unresolved one-sided pursuit remains active."
        ))
    }

    fun buildState(
        snapshot: ChatSnapshot,
        relationship: String,
        background: String = "",
        history: List<LogEntry> = emptyList()
    ): JSONObject {
        val msgs = JSONArray()
        val last10 = snapshot.messages.takeLast(10)
        for (m in last10) msgs.put(JSONObject().put("from", m.side).put("text", m.text))
        val chat = JSONObject()
            .put("relationship", relationship)
            .put("messages", msgs)
            .put("latest_from", last10.lastOrNull()?.side ?: "other")
        val state = JSONObject().put("chat", chat)
        if (background.isNotBlank()) state.put("background", background)
        if (history.isNotEmpty()) {
            val h = JSONArray()
            history.forEach { h.put(JSONObject().put("from", it.side).put("text", it.text)) }
            state.put("history", h)
        }
        return state
    }

    fun rankQuestion(candidates: List<String>): JSONObject {
        require(candidates.size == 3) { "rankQuestion expects exactly 3 candidates" }
        val keys = listOf("reply_a", "reply_b", "reply_c")
        val criteria = JSONObject()
        keys.forEachIndexed { i, k -> criteria.put(k, candidates[i]) }
        val q = JSONObject().apply {
            put("type", "choice")
            put("instructions", "Which reply best applies the supported next action? Prefer sincerity, directness, dignity, a face-saving and pressure-free option, concrete problem solving, and respect for rejection. " +
                "Penalize people-pleasing, manipulation, jealousy, surprise tests, false certainty, invented facts, over-investment in ambiguity, or imitating a public persona instead of sounding like the user." + BACKGROUND_NOTE)
            put("criteria", criteria)
        }
        return JSONObject().put("best_reply", q)
    }
}
