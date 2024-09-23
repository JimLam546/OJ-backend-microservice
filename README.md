# OJ在线判题平台

前端地址：https://gitee.com/jim-lam/online-judge-frontend <br>
后端地址：https://gitee.com/jim-lam/online-judge-backend-microservice <br>
#### 项目技术栈
Spring Boot + Mybatis-Plus + Spring Cloud 微服务 + RabbitMQ + Docker

#### 项目介绍
本项目是编程题目评测系统，能够根据用户提交的代码、出题人预先设置的题目输入和输出用例，
进行编译代码、运行代码、判断代码运行结果是否正确。
目前只支持java语言一种。
> 评测的限制有
- 超时限制
- 内存限制

#### 项目使用
- 代码的内容要符合以下的模版要求
```java
class Main{
    public static void main(String[] args) {
        
    }
}
```
- 题目要求的参数信息从 main 方法中的 args 字符串数组中获取，并将结果通过`System.out`的方式打印说明
> 例如：题目有两个参数，值是 1 和 2，答案是 3
```java
class Main{
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        System.out.println((a+b));
    }
}
```
