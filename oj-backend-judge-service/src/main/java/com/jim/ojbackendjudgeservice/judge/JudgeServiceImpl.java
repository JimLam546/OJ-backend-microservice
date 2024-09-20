package com.jim.ojbackendjudgeservice.judge;


import cn.hutool.json.JSONUtil;
import com.jim.ojbackendcommon.common.ErrorCode;
import com.jim.ojbackendcommon.exception.BusinessException;
import com.jim.ojbackendcommon.model.codesandbox.ExecuteCodeRequest;
import com.jim.ojbackendcommon.model.codesandbox.ExecuteCodeResponse;
import com.jim.ojbackendcommon.model.codesandbox.JudgeInfo;
import com.jim.ojbackendcommon.model.dto.Question.JudgeCase;
import com.jim.ojbackendcommon.model.entity.Question;
import com.jim.ojbackendcommon.model.entity.QuestionSubmit;
import com.jim.ojbackendcommon.model.enums.QuestionSubmitStatusEnum;
import com.jim.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.jim.ojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.jim.ojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.jim.ojbackendjudgeservice.judge.strategy.JudgeContext;
import com.jim.ojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.jim.ojbackendcommon.model.enums.QuestionSubmitStatusEnum.FAILED;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        System.out.println("异步判题....");
        // 1. 传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2. 如果题目提交状态不为等待中，就不重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3. 更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4. 调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取题目的输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 如果编译错误就直接将错误信息写入数据库
        if (executeCodeResponse.getStatus().equals(FAILED.getValue())) {
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(executeCodeResponse.getJudgeInfo()));
            questionSubmitUpdate.setStatus(FAILED.getValue());
            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            return questionFeignClient.getQuestionSubmitById(questionId);
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5. 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6. 修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }
}
