package com.jim.ojbackendserviceclient.service;


import com.jim.ojbackendcommon.model.entity.Question;
import com.jim.ojbackendcommon.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Jim_Lam
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2024-09-05 21:30:42
 */
@FeignClient(name = "oj-backend-question-service", path = "/api/Question/inner")
public interface QuestionFeignClient {

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId);

    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

}
