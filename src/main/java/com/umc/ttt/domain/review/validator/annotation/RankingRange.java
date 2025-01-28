package com.umc.ttt.domain.review.validator.annotation;

import com.umc.ttt.domain.review.validator.validator.RankingRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RankingRangeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RankingRange {
    String message() default "별점은 최소 1점, 최대 5점이며 0.5점 간격이 필수입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
