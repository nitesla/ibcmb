package longbridge.utils;

import longbridge.models.UserType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface Verifiable
{

	String operation() default "";
	String description() default "";
	UserType type() default UserType.ADMIN ;
}


