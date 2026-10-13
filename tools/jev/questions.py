"""Jev question set aligned with the bundled Tong Jincheng perspective."""

from __future__ import annotations

BACKGROUND_NOTE = (
    " Facts in background are context. Automatic Tong Jincheng cards are decision guidance, not facts. "
    "Treat the framework as practical street wisdom, not scientific certainty."
)


def _noul(instructions: str, true: str, false: str) -> dict:
    return {"type": "noul", "instructions": instructions + BACKGROUND_NOTE,
            "criteria": {"true": true, "false": false}}


def _choice(instructions: str, criteria: dict[str, str]) -> dict:
    return {"type": "choice", "instructions": instructions + BACKGROUND_NOTE,
            "criteria": criteria}


JUDGE_QUESTIONS: dict = {
    "literal_question": _noul(
        "Is the latest message direct and literal rather than a test, face-saving hint, performance, or hidden complaint? Use the whole thread and do not invent motives.",
        "It plainly states a feeling, request, boundary, plan, acceptance, or rejection.",
        "It hides a test of interest, a need for a face-saving reason, sarcasm, insecurity signaling, or an implied complaint.",
    ),
    "true_intent": _choice(
        "What is the best-supported social signal in the latest message? Prefer repeated behavior over flattering words. Do not label ambiguity as attraction.",
        {
            "clear_interest": "They clearly invest, initiate, accept a concrete plan, or offer an alternative.",
            "ambiguous_interest": "There may be warmth or attention, but no reliable reciprocal action yet.",
            "polite_or_logistic": "Ordinary politeness, casual talk, or practical logistics without evidence of romantic interest.",
            "asks_for_action": "They directly want a fact, action, time, explanation, apology, or commitment.",
            "seeks_care_or_face": "They want care, respect, reassurance, or a face-saving way to respond.",
            "boundary_or_rejection": "They set a boundary, ask for space, decline, end the relationship, or show sustained non-reciprocity.",
        },
    ),
    "danger_level": {
        "type": "score",
        "instructions": "How risky would it be to push the relationship in the next message? Score the present scene, including consent, fraud, threats, and clear boundaries." + BACKGROUND_NOTE,
        "criteria": [
            "Warm mutual chat or a confirmed plan; no pressure, complaint, or safety issue.",
            "Neutral chat or logistics; a normal short reply is low risk.",
            "Interest is uncertain, but one respectful low-pressure message is acceptable.",
            "One-sided effort, mild disappointment, or awkwardness; more chasing may reduce dignity or attraction.",
            "Cold, inconsistent, or sarcastic signals; a pushy reply could escalate tension.",
            "A direct complaint, broken promise, or visible loss of trust requires honest repair first.",
            "A request for space, repeated cancellation, or serious conflict makes continued pursuit harmful.",
            "A firm rejection, breakup, financial pressure, controlling behavior, or repeated boundary violation is present.",
            "An ultimatum, coercion, stalking, fraud pattern, self-harm threat, or credible threat is active.",
            "Immediate danger, violence, forced sexual conduct, extortion, or an active harm crisis is present.",
        ],
    },
    "should_reply_now": _noul(
        "Is there a truthful, useful next message to send now? True requires a direct answer, concrete plan, sincere repair, or one low-pressure attempt supported by the thread.",
        "A short truthful reply can answer, solve, make one respectful attempt, or give a needed face-saving option.",
        "More words would be guessing, testing human nature, over-investing in ambiguity, ignoring a boundary, or reopening closure.",
    ),
    "best_action": _choice(
        "Choose the next action using attraction over pleasing, a face-saving step, no tests of human nature, directness, problem solving, and stepping back from sustained ambiguity.",
        {
            "be_direct": "Say the honest feeling, answer, boundary, or apology plainly.",
            "give_step": "Offer a concrete, low-pressure, face-saving option with an easy way to decline or prepare.",
            "solve_problem": "Address the actual issue with facts, responsibility, and a specific next step.",
            "try_once": "Make one sincere, specific attempt because genuine interest is worth testing through action.",
            "step_back": "Stop adding effort where attraction is unclear, action is not reciprocal, the topic is closed, or a boundary was stated.",
            "invest_self": "Put attention back into work, study, fitness, or stable routines rather than chasing or drinking.",
            "protect_safety": "Protect money or physical safety and seek trusted or emergency support as appropriate.",
        },
    ),
    "she_needs": _choice(
        "What does the other person most need right now? Choose only what the thread supports. Rejection means space, not persuasion.",
        {
            "sincerity": "A direct truthful acknowledgment, answer, apology, or statement of intent.",
            "face": "A dignified option that lets them accept, decline, or recover without embarrassment.",
            "preparation": "Advance notice, time to prepare, or a concrete plan rather than a surprise test.",
            "solution": "A practical action that resolves the named problem or fulfills a commitment.",
            "space": "Less pursuit, no persuasion, or respect for a boundary, rejection, or closed topic.",
            "nothing": "No special response is needed; the exchange is casual, complete, or mutually understood.",
        },
    ),
    "tension_resolved": _noul(
        "Is the social tension or uncertainty resolved enough that no repair or clarification remains? Silence alone is not resolution.",
        "They clearly accepted, confirmed, declined, closed the topic, or returned to warm mutual interaction.",
        "A complaint, ambiguity, broken commitment, boundary, safety issue, or one-sided pursuit remains active.",
    ),
}


def build_state(messages: list, relationship: str) -> dict:
    """Build state from the last ten (her|me, text) messages."""
    cleaned = []
    for item in messages:
        who, text = (item.get("from"), item.get("text")) if isinstance(item, dict) else item[:2]
        if who not in ("her", "me"):
            raise ValueError(f"message from must be 'her' or 'me', got {who!r}")
        cleaned.append({"from": who, "text": str(text)})
    cleaned = cleaned[-10:]
    return {"chat": {"relationship": relationship, "messages": cleaned,
                     "latest_from": cleaned[-1]["from"] if cleaned else "her"}}


def build_rank_question(candidates: list[str]) -> dict:
    if len(candidates) != 3:
        raise ValueError("build_rank_question expects exactly 3 candidate replies")
    return {"best_reply": {
        "type": "choice",
        "instructions": (
            "Which reply best applies the supported next action? Prefer sincerity, directness, dignity, a face-saving and pressure-free option, concrete problem solving, and respect for rejection. "
            "Penalize people-pleasing, manipulation, jealousy, surprise tests, false certainty, invented facts, and over-investment in ambiguity."
            + BACKGROUND_NOTE
        ),
        "criteria": dict(zip(("reply_a", "reply_b", "reply_c"), candidates)),
    }}
