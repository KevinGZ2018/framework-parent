package com.fushun.framework.jpa.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityListenerObjcet {

    Class<? extends EntityOptionObject<?>> entityOptionObject();
}
