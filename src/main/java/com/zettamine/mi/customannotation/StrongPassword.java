package com.zettamine.mi.customannotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zettamine.mi.validators.PasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongPassword {

	String message() default "Must be 8 characters long and combination of uppercase letters, lowercase letters, numbers, special characters.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
