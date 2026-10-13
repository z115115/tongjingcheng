package com.jev.probe.core.kb

import com.jev.probe.core.ChatSnapshot
import com.jev.probe.core.Msg
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuiltinTopicRouterTest {
    private fun route(vararg texts: String) = BuiltinTopicRouter.route(
        ChatSnapshot("测试", texts.map { Msg("other", it) }), "朋友"
    )

    @Test fun ordinaryChatStillLoadsTheTongFoundation() {
        assertEquals(listOf("童锦程总纲：真诚比套路重要"), route("今天吃什么").map { it.title })
    }

    @Test fun debtQuestionLoadsMoneyGuidanceAutomatically() {
        val titles = route("对了，上次你欠我的500块什么时候还？").map { it.title }
        assertTrue("钱花在真正重要的人身上" in titles)
    }

    @Test fun explicitThreatMakesSafetyTheFirstDomainCard() {
        val titles = route("你敢分手我就跟踪你，还要伤害自己").map { it.title }
        assertEquals("童锦程总纲：真诚比套路重要", titles[0])
        assertEquals("先保安全，不谈套路", titles[1])
        assertTrue(titles.size <= 3)
    }

    @Test fun uncertainInterestLoadsAttractionInsteadOfPeoplePleasing() {
        val titles = route("她总是忽冷忽热，我一直主动，她到底喜不喜欢我").map { it.title }
        assertTrue("吸引力大于讨好" in titles)
    }

    @Test fun deliberateSilenceLoadsDoNotTestHumanNature() {
        val titles = route("我想故意不联系，看她会不会主动找我").map { it.title }
        assertTrue("人性不可考验" in titles)
    }

    @Test fun noAnalysisReceivesMoreThanThreeCards() {
        assertTrue(route("分手后他威胁我，还欠我钱并且一直跟踪我").size <= 3)
    }
}
