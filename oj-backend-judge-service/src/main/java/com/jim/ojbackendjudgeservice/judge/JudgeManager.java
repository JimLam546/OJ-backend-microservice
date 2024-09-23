package com.jim.ojbackendjudgeservice.judge;


import com.jim.ojbackendcommon.model.codesandbox.JudgeInfo;
import com.jim.ojbackendcommon.model.entity.QuestionSubmit;
import com.jim.ojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.jim.ojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.jim.ojbackendjudgeservice.judge.strategy.JudgeContext;
import com.jim.ojbackendjudgeservice.judge.strategy.JudgeStrategy;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 * 暂时用来调配沙箱用哪个，策略用哪个
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        // 设置不同语言执行的策略
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
