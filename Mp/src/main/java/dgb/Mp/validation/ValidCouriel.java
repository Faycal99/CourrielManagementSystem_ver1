package dgb.Mp.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@Constraint(validatedBy = CourielTypeValidator.class)
@Documented
public @interface ValidCouriel {

    String message() default "Invalid fields for the given Couriel_Type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
