package com.jim.ojbackendjudgeservice;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class OjBackendJudgeServiceApplicationTests {

    @Test
    void contextLoads() {

    }

    public static void main(String[] args) {
        String url = "http://192.168.233.131:8090/executeCode";
        String json = JSONUtil.toJsonStr("{}");
        String responseStr = HttpUtil.createPost(url)
                .header("auth", "secretKey")
                .body(json)
                .execute()
                .body();
        System.out.println(responseStr);
    }

}
