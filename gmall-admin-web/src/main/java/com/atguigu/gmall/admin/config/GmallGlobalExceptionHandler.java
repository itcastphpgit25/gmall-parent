package com.atguigu.gmall.admin.config;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * 处理系统所有的抛出异常
 */
@Slf4j
@RestControllerAdvice  //两个注解
//@ControllerAdvice //全局异常处理器
public class GmallGlobalExceptionHandler {
    //数学计算异常
    @ExceptionHandler(ArithmeticException.class)
    public Object handlerNullpointException(Exception e){
        log.error("全局异常处理类感知到异常...");

        return new CommonResult().failed().validateFailed(e.getMessage());
    }

    //这个放在最后比较好
    @ExceptionHandler(Exception.class)
    public Object handle(Exception e){
        log.error("全局异常处理类感知到异常...");

        return new CommonResult().failed().validateFailed(e.getMessage());
    }
}
