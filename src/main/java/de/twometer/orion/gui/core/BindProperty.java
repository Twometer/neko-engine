package de.twometer.orion.gui.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindProperty {

    String value() default "";

    boolean required() default false;

    Class<? extends IPropertyDecoder> decoder() default DefaultPropertyDecoder.class;

}
