package com.homeaid.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메소드나 클래스에 적용하여 로깅을 활성화하는 어노테이션
 * 
 * @author HomeAid Team
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    
    /**
     * 입력 파라미터 로깅 여부
     */
    boolean logParameters() default true;
    
    /**
     * 반환값 로깅 여부
     */
    boolean logResult() default true;
    
    /**
     * 실행 시간 로깅 여부
     */
    boolean logExecutionTime() default true;
    
    /**
     * 로그 레벨 (INFO, DEBUG, TRACE)
     */
    String level() default "INFO";
}
