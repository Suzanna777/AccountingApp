package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @Pointcut("execution(* com.cydeo..*(..))")
    public void allControllerMethods() {
    }


    @AfterThrowing(pointcut = "allControllerMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, RuntimeException exception) {
        String methodName = joinPoint.getSignature().getName();
        String exceptionClassName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();

        log.error("Exception thrown in method: {}. Exception class: {}. Message: {}",
                methodName, exceptionClassName, exceptionMessage);
    }
}
