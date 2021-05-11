package pl.dietadvisor.common.shared.util.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class KafkaConsumerLogAspect {
    @Before("@annotation(KafkaConsumerLog)")
    public void logConsumedMessage(JoinPoint joinPoint) {
        Object message = joinPoint.getArgs()[0];
        log.info("Consumed message: {}.", message);
    }
}
