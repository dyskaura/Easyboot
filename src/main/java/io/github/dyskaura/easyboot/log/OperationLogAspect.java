package io.github.dyskaura.easyboot.log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private final OperationLogRepository repository;

    @Around("@annotation(operation)")
    public Object record(ProceedingJoinPoint joinPoint, Operation operation) throws Throwable {
        long start = System.currentTimeMillis();
        boolean success = true;
        String error = null;
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            success = false;
            error = throwable.getMessage();
            throw throwable;
        } finally {
            save(operation.value(), success, error, System.currentTimeMillis() - start);
        }
    }

    private void save(String operation, boolean success, String error, long duration) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication == null ? "anonymous" : authentication.getName();
        repository.save(OperationLog.builder().operation(operation).username(username)
                .method(request == null ? "-" : request.getMethod())
                .path(request == null ? "-" : request.getRequestURI())
                .ip(request == null ? null : request.getRemoteAddr())
                .success(success).errorMessage(error == null ? null : error.substring(0, Math.min(error.length(), 500)))
                .durationMs(duration).createdAt(Instant.now()).build());
    }
}
