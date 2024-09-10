package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Pointcut("@annotation(com.cydeo.annotation.ExecutionTime)")
    public void executionTimePointcut() {
    }


    @Around("executionTimePointcut()")
    public Object aroundAnyExecutionTimeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        log.info("Method {} execution started", joinPoint.getSignature().getName());
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long executionTimeInMillis = System.currentTimeMillis() - start;
        log.info("Method {} executed in {} ms", joinPoint.getSignature().getName(), executionTimeInMillis);
        return result;
    }
}
