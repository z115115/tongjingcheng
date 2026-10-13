package com.jev.probe.core.kb

import com.jev.probe.core.ChatSnapshot

/** Offline routing for the bundled Tong Jincheng perspective cards. */
object BuiltinTopicRouter {
    const val ID_PREFIX = "builtin:tjc:"
    private const val MAX_SCENE_CARDS = 2

    private data class Card(
        val id: String,
        val title: String,
        val keywords: List<String>,
        val guidance: String,
        val priority: Int = 0
    ) {
        fun asNote() = Note(
            id = ID_PREFIX + id,
            title = title,
            content = "这是童锦程视角的判断框架，不是关于对方的事实。$guidance",
            tags = emptyList(), enabled = true, updatedAt = 0L
        )
    }

    private val foundation = Card(
        "foundation", "童锦程总纲：真诚比套路重要", emptyList(),
        "价值排序是真诚、自律、忠诚、直接、体面。先说实话，再解决问题；不靠贬低自己讨好，" +
            "不靠试探考验人性，也不拿一句话替对方读心。能给对方台阶就给台阶，但台阶要有真诚动机。" +
            "表达先给结论，再给可执行的一步；不确定的事明确说不确定。"
    )

    private val sceneCards = listOf(
        Card(
            "safety", "先保安全，不谈套路",
            listOf("打我", "掐脖", "堵门", "跟踪", "威胁", "杀了", "自杀", "不想活", "伤害自己", "强迫", "下药", "偷拍", "勒索", "曝光隐私", "报警"),
            "现实危险高于任何关系推进。先离开危险、联系可信支持或当地紧急服务；不建议用话术换安全，" +
                "不把暴力、胁迫或自伤风险包装成深情。", 1000
        ),
        Card(
            "attraction", "吸引力大于讨好",
            listOf("不确定", "喜不喜欢", "忽冷忽热", "敷衍", "已读不回", "不回消息", "总是我主动", "一直是我主动", "备胎", "舔狗", "讨好", "没时间", "下次吧"),
            "没有人会因为你喜欢他而喜欢你。先看持续、明确、互相的行动，而不是在模糊信号上不断加码。" +
                "如果长期需要猜对方是否喜欢你，就降低投入，把精力放回自己；吸引来自充实自己，不来自自我贬低。"
        ),
        Card(
            "face_saving", "给台阶：让选择保持体面",
            listOf("尴尬", "拒绝", "不好意思", "怎么开口", "红包", "请客", "邀请", "借口", "面子", "台阶", "能不能", "方便吗"),
            "人有时不是不想做，而是需要一个能说服自己的体面理由。邀请要具体、低压力、可拒绝；" +
                "给对方一个自然接受或退出的台阶。台阶不是欺骗，更不是操纵，必须由真诚动机支撑。"
        ),
        Card(
            "dont_test", "人性不可考验",
            listOf("测试", "试探", "突袭", "惊喜", "查岗", "查手机", "定位", "故意不联系", "冷战", "看他会不会", "看她会不会", "考验", "突然去找"),
            "不要设局证明对方爱不爱你。想见就直接说并给准备时间；有需求就说需求。" +
                "与其把关系放到最容易失败的位置，不如给对方表现好的条件。发现真实欺骗时则直接核验事实和保护自己。"
        ),
        Card(
            "signaling", "炫耀往往暴露不安全感",
            listOf("炫耀", "显摆", "朋友圈", "晒", "有钱", "豪车", "名牌", "渣男", "恋爱脑", "人设", "标签", "证明自己", "装"),
            "越反复强调什么，越可能在担心自己缺什么。把自我标签和炫耀当概率信号，不当铁证；" +
                "先检查自己想证明什么，再看对方的长期行为。少表演，多做实事。"
        ),
        Card(
            "self_growth", "瓶颈期回到读书、健身和事业",
            listOf("低谷", "瓶颈", "失恋", "喝酒", "买醉", "工作", "事业", "读书", "学习", "健身", "自律", "迷茫", "焦虑", "放不下"),
            "遇到瓶颈就读书或健身，不靠喝酒逃避；明天的问题不会因为今天喝醉而消失。" +
                "事业和成长是主线，爱情不是唯一主业。先让自己站稳，再决定关系里投入多少。"
        ),
        Card(
            "try_once", "真喜欢就真实地试一次",
            listOf("暗恋", "表白", "喜欢你", "喜欢她", "喜欢他", "约会", "见面", "想见", "追她", "追他", "后悔", "在一起", "第一次见"),
            "对真心喜欢的人可以试一次，别让没有行动变成长期后悔。表达要直接、具体、可拒绝，并给准备时间；" +
                "试过后看清反馈。明确拒绝就停，模糊且长期无行动就别死磕。"
        ),
        Card(
            "money", "钱花在真正重要的人身上",
            listOf("钱", "欠", "还款", "转账", "红包", "礼物", "请客", "酒局", "借钱", "彩礼", "买单", "花钱", "贵", "投资"),
            "资源优先给真正重要、长期互相负责的人，不用给萍水之交的表演买单。" +
                "感情不能替代借贷和风险核验；涉及陌生投资、解冻费或紧急转账时停止追加付款并核实。"
        ),
        Card(
            "solve", "生气不能解决问题",
            listOf("生气", "吵架", "道歉", "对不起", "冷战", "冲突", "骗我", "失约", "你根本", "每次都", "从来不", "伤心", "委屈", "火大"),
            "事情发生后，先想解决办法。说清事实、影响、责任和下一步；该道歉就真诚道歉，该修复就给具体行动。" +
                "多说鼓励，少说会在人心里留疤的伤害话；直接不等于刻薄。"
        ),
        Card(
            "social_reality", "成功前后的人情冷暖",
            listOf("成功", "没钱", "落魄", "低谷", "人脉", "利用", "势利", "看不起", "合影", "资源", "身份"),
            "外界态度会随你的处境和价值变化。低谷时看谁仍真正在乎你，上升时别把围绕而来的热情都当真心。" +
                "这是清醒，不是把所有关系都解释成利益。"
        ),
        Card(
            "loyalty", "长期关系看忠诚与行动",
            listOf("出轨", "忠诚", "老婆", "老公", "结婚", "伴侣", "复合", "前任", "承诺", "一辈子", "长期", "责任"),
            "长期关系里，忠诚、兑现和日常照顾比人设更重要。真诚不保证一定得到爱，但不真诚一定损耗信任；" +
                "复合看问题是否被解决和行为是否改变，不只看想念。"
        )
    )

    fun route(snapshot: ChatSnapshot, relationship: String, maxCards: Int = 3): List<Note> {
        if (maxCards <= 0) return emptyList()
        val haystack = buildString {
            append(relationship.lowercase())
            snapshot.messages.takeLast(10).forEach { append('\n').append(it.text.lowercase()) }
        }
        val ranked = sceneCards.mapNotNull { card ->
            val hitCount = card.keywords.count { haystack.contains(it.lowercase()) }
            if (hitCount == 0) null else card to (hitCount * 10 + card.priority)
        }.sortedWith(compareByDescending<Pair<Card, Int>> { it.second }.thenBy { it.first.title })

        val out = arrayListOf(foundation.asNote())
        ranked.take(minOf(MAX_SCENE_CARDS, maxCards - 1)).forEach { out.add(it.first.asNote()) }
        return out.take(maxCards)
    }

    fun isBuiltin(note: Note): Boolean = note.id.startsWith(ID_PREFIX)
}
