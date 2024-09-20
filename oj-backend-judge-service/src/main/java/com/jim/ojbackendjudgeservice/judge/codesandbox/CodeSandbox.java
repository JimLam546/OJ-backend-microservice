package com.jim.ojbackendjudgeservice.judge.codesandbox;


import com.jim.ojbackendcommon.model.codesandbox.ExecuteCodeRequest;
import com.jim.ojbackendcommon.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
