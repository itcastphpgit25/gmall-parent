package com.atguigu.gmall.admin.config;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
@Slf4j
@Aspect //说明这是一个切面
@Component
public class GmallValidatorAspect {
    @Around("execution(* com.atguigu.gmall.admin..controller..*.*(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("校验切面切入进行");
        Object[] args = proceedingJoinPoint.getArgs();

        Object proceed=null;
      // try {
           //前置通知
            for (Object obj:args) {
                //获取当前所有参数
                if(obj instanceof BindingResult){
                    //只获取感兴趣的BindingResult
                    //判断校验有误错误
                    int count = ((BindingResult) obj).getErrorCount();
                    if(count>0){
                        log.error("校验发生错误...");
                        //有错误
                        CommonResult commonResult = new CommonResult().validateFailed((BindingResult) obj);
                        return commonResult;
                    }
                }
            }
            //方法执行完成相当于method.invoke()
            proceed = proceedingJoinPoint.proceed(args);
        //}catch (Exception e){
            //e.printStackTrace();
            //异常通知
        //}
      return proceed;
    }
}
