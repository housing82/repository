package com.universal.code.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Operation {
    /**
     * value
     * @return
     */
    public abstract String value() default "";
    
    /**
     * 인덱스 어노테이션 메소드일 경우 코드조회를 할것인지 여부(기본 true)
     * @Name : findCode 
     * @Description : 
     * @LastUpdated : 2015. 10. 18. 오후 3:04:22
     * @return
     */
    public abstract boolean findCode() default true;
    
    
    public abstract String description() default "ubms operation";
}
