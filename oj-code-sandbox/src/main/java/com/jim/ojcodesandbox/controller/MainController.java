package com.jim.ojcodesandbox.controller;

import com.jim.ojcodesandbox.model.ExecuteCodeRequest;
import com.jim.ojcodesandbox.model.ExecuteCodeResponse;
import com.jim.ojcodesandbox.service.JavaDockerCodeSandbox;
import com.jim.ojcodesandbox.service.JavaNativeCodeSandbox;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class MainController {
    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @Resource
    private JavaDockerCodeSandbox javaDockerCodeSandbox;
    @RequestMapping("/hello")
    public String hello() {
        return "ok";
    }

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                    HttpServletResponse response) {
        // 基本的认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        return javaDockerCodeSandbox.executeCode(executeCodeRequest);
        // return javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }
}