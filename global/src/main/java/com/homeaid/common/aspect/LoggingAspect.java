package com.homeaid.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 전역 로깅을 처리하는 Aspect 클래스
 * Controller와 Service 메소드의 입출력 파라미터를 자동으로 로깅합니다.
 * 
 * @author HomeAid Team
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper objectMapper;
    
    /**
     * Controller 메소드에 대한 전역 로깅
     * HTTP 요청 정보와 함께 파라미터와 응답을 로깅합니다.
     */
    @Around("execution(* com.homeaid.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = generateRequestId();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        // HTTP 요청 정보 가져오기
        HttpServletRequest request = getHttpServletRequest();
        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestURI = request != null ? request.getRequestURI() : "UNKNOWN";
        
        // 입력 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        log.info("\r\n \t\t🚀 [{}] [REQUEST] {} {} - {}.{} | Parameters: {}",
                requestId, httpMethod, requestURI, 
                className, methodName, 
                formatParameters(args));
        
        try {
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 응답 로깅
            log.info("\r\n \t\t✅ [{}] [RESPONSE] {}ms - {}.{} | Result: {}",
                    requestId, executionTime, 
                    className, methodName, 
                    formatResult(result));
            
            return result;
        } catch (Exception e) {
            log.error("❌ [{}] [ERROR] {}.{} | Exception: {} - {}", 
                    requestId, className, methodName, 
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Service 메소드에 대한 전역 로깅
     * 비즈니스 로직 실행 과정을 상세히 로깅합니다.
     */
    @Around("execution(* com.homeaid.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = getCurrentRequestId();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        // 입력 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        log.debug("\r\n \t\t🔧 [{}] [SERVICE-IN] {}.{} | Parameters: {}",
                requestId, className, methodName, formatParameters(args));
        
        try {
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 응답 로깅
            log.debug("\r\n \t\t✅ [{}] [SERVICE-OUT] {}ms - {}.{} | Result: {}",
                    requestId, executionTime, 
                    className, methodName, 
                    formatResult(result));
            
            return result;
        } catch (Exception e) {
            log.error("❌ [{}] [SERVICE-ERROR] {}.{} | Exception: {} - {}", 
                    requestId, className, methodName, 
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * HTTP 요청 객체를 안전하게 가져오기
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 요청 ID 생성 (실제 구현에서는 ThreadLocal이나 MDC 사용 권장)
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 현재 요청 ID 가져오기
     */
    private String getCurrentRequestId() {
        // 실제 구현에서는 MDC(Mapped Diagnostic Context)를 사용하여 
        // 동일한 요청에 대해 같은 ID를 반환하도록 구현
        return generateRequestId();
    }

    /**
     * 파라미터를 로그용 문자열로 포맷
     */
    private String formatParameters(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(formatObject(args[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 반환값을 로그용 문자열로 포맷
     */
    private String formatResult(Object result) {
        return formatObject(result);
    }

    /**
     * 객체를 로그용 문자열로 변환 (민감한 정보 마스킹 포함)
     */
    private String formatObject(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        try {
            String jsonStr = objectMapper.writeValueAsString(obj);
            // 민감한 정보 마스킹
            return maskSensitiveData(jsonStr);
        } catch (Exception e) {
            // JSON 변환 실패 시 toString() 사용
            String str = obj.toString();
            return maskSensitiveData(str);
        }
    }

    /**
     * 민감한 정보를 마스킹 처리
     */
    private String maskSensitiveData(String data) {
        if (data == null) return "null";
        
        return data
            // 패스워드 필드 마스킹
            .replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
            .replaceAll("\"pwd\"\\s*:\\s*\"[^\"]*\"", "\"pwd\":\"***\"")
            // 전화번호 마스킹 (뒷 4자리만 표시)
            .replaceAll("\"(phone|phoneNumber|mobile)\"\\s*:\\s*\"(\\d{3})-?(\\d{4})-?(\\d{4})\"", 
                       "\"$1\":\"$2-****-$4\"")
            // 이메일 마스킹
            .replaceAll("\"email\"\\s*:\\s*\"([^@\"]+)@([^\"]+)\"", "\"email\":\"***@$2\"")
            // 주민등록번호 등 개인식별정보 마스킹
            .replaceAll("\"(ssn|socialSecurityNumber)\"\\s*:\\s*\"[^\"]*\"", "\"$1\":\"***\"");
    }
}
