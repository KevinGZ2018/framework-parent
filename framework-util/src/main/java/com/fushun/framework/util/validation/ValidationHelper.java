package com.fushun.framework.util.validation;

import com.fushun.framework.util.exception.base.SpringContextUtil;
import com.fushun.framework.util.util.StringUtils;
import org.springframework.context.support.AbstractMessageSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;


/**
 * validation帮助处理类
 *
 * @author zpcsa
 */

public class ValidationHelper {


    private Validator validator;

    private AbstractMessageSource messageSource;

    public static ValidationHelper getInstance() {
        return SpringContextUtil.getBean(ValidationHelper.class);
    }

    /**
     * @param object
     * @param groups
     */
    public static ValidationResult validateSingle(Object object, Class<?>... groups) {
        return ValidationHelper.getInstance().innerValidateSingle(object, groups);
    }


    /**
     * @param messageCode
     * @param arguments
     */
    public static void validationFailBySingleMessage(String messageCode, Object[] arguments) {
        ValidationResult vr = new ValidationResult();
        String message = ValidationHelper.getInstance().getMessageSource().getMessage(messageCode, arguments, null);
        vr.addGlobalMessage(message);
    }

    /**
     * @param messageCode
     */
    public static void validationFailBySingleMessage(String messageCode) {
        validationFailBySingleMessage(messageCode, null);
    }

    /**
     * @param object
     * @param vr
     * @param vr
     * @param groups
     * @return
     */
    public static ValidationResult validateMultiple(Object object, ValidationResult vr, Class<?>... groups) {
        return ValidationHelper.getInstance().innerValidateMultiple(object, vr, groups);
    }

    /**
     * 校验单个对象参数是否合法
     *
     * @param object
     * @param vr
     * @return
     */
    public static ValidationResult validateMultiple(Object object, ValidationResult vr) {
        return ValidationHelper.getInstance().innerValidateMultiple(object, vr, Default.class);
    }

    private ValidationResult innerValidateSingle(Object object, Class<?>... groups) {
        ValidationResult vr = new ValidationResult();
        validateMultiple(object, vr, groups);
        return vr;
    }

    private ValidationResult innerValidateMultiple(Object object, ValidationResult vr, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        for (ConstraintViolation<Object> constraint : constraintViolations) {
            if (!StringUtils.isEmpty(constraint.getPropertyPath().toString())) {
                vr.addMessage(constraint.getPropertyPath().toString(), constraint.getMessage());
            } else {
                vr.addGlobalMessage(constraint.getMessage());
            }
        }
        return vr;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public AbstractMessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(AbstractMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
