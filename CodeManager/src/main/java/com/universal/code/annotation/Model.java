package com.universal.code.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Model {

    
    /**
     * Database Table Name 
     * @return
     */
    public abstract String modelNames() default "";
    
    /**
     * Database Table Comment
     * @return
     */
    public abstract String description() default "";
    
    
    /**
     * Database Table Type
     * @return
     */
    public abstract String modelTypes() default "";
    
}
