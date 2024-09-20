package com.jim.ojbackendcommon.model.dto.Question;


import lombok.Data;

/**
 * 题目用例
 */
@Data
public class JudgeConfig {
    /**
     * 时间限制
     */
    private Long timeLimit;

    /**
     *  内存限制（KB）
     */
    private Long memoryLimit;

    /**
     * 堆栈限制（KB）
     */
    private Long stackLimit;
}