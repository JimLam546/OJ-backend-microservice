package com.jim.ojbackendquestionservice.controller.inner;

import com.jim.ojbackendcommon.model.entity.Question;
import com.jim.ojbackendcommon.model.entity.QuestionSubmit;
import com.jim.ojbackendquestionservice.service.QuestionService;
import com.jim.ojbackendquestionservice.service.QuestionSubmitService;
import com.jim.ojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

    /**
     * 获取题目id
     *
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/question_submit/get/question")
    public long getQuestionIdByQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        return questionSubmitService.lambdaQuery()
                .select(QuestionSubmit::getQuestionId)
                .eq(QuestionSubmit::getId, questionSubmitId)
                .one()
                .getQuestionId();
    }

    /**
     * 修改题目的提交数（+1）
     *
     * @param questionId
     * @return
     */
    @GetMapping("/question/submitNum")
    public boolean updateSubmitNum(@RequestParam("questionId") long questionId) {
        return questionService.update()
                .eq("id", questionId)
                .setSql("submitNum = submitNum + 1")
                .update();
    }

    /**
     * 修改题目的通过数
     *
     * @param questionId
     * @return
     */
    @GetMapping("/question/acceptedNum")
    public boolean updateAcceptedNum(@RequestParam("questionId") long questionId) {
        return questionService.update()
                .eq("id", questionId)
                .setSql("acceptedNum = acceptedNum + 1")
                .update();
    }
}