package com.umc.ttt.domain.review.validator.validator;

import com.umc.ttt.domain.review.validator.annotation.RankingRange;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingRangeValidator implements ConstraintValidator<RankingRange, Double> {
    @Override
    public void initialize(RankingRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Double ranking, ConstraintValidatorContext context) {
        boolean isValid = true;

        if(ranking%0.5!=0 && ranking!=null){
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.RIVIEW_NOT_RANKING_RANGE.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
