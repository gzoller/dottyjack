package co.blocke.dottyjack;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}

