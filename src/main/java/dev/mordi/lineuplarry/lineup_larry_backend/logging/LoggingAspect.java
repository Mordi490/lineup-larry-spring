package dev.mordi.lineuplarry.lineup_larry_backend.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // SpEL to target the HTTP mapping annotations, @GetMapping, @PutMapping etc.
    @Around("(@annotation(org.springframework.web.bind.annotation.GetMapping) ||"
            + " @annotation(org.springframework.web.bind.annotation.PostMapping) ||"
            + " @annotation(org.springframework.web.bind.annotation.PatchMapping) ||"
            + " @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        // Before method execution
        log.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
        long startTime = System.currentTimeMillis();

        // Proceed with method execution
        Object result;
        try {
            result = joinPoint.proceed(); // This is where the actual method execution happens
        } catch (Throwable throwable) {
            // Optionally log the exception if needed
            log.error("Exception in method: {}", joinPoint.getSignature().toShortString(),
                    throwable);
            throw throwable; // Re-throw the exception so the calling code can handle it
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // After method execution
        log.info("Exiting method: {} with result: {}. Executed in {} milliseconds",
                joinPoint.getSignature().toShortString(), result, totalTime);
        return result;
    }

    // TODO: add logging to the service layer

    // TODO: add logging to the repository layer
}
