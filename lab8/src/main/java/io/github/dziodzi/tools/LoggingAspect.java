package io.github.dziodzi.tools;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("@annotation(io.github.dziodzi.tools.LogExecutionTime)")
    public void methodsWithLogExecutionTime() {}

    @Pointcut("@within(io.github.dziodzi.tools.LogExecutionTime)")
    public void classesWithLogExecutionTime() {}

    @Around("methodsWithLogExecutionTime() || classesWithLogExecutionTime()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("Class: {}, Method: {} executed in {} ms", className, methodName, executionTime);

        return proceed;
    }
}
