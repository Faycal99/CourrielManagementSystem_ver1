package dgb.Mp.validation;

import dgb.Mp.Couriel.Dtos.CourielValidatble;
import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Nature;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CourielTypeValidator implements ConstraintValidator<ValidCouriel, CourielValidatble> {
    private static final Logger logger = LoggerFactory.getLogger(CourielTypeValidator.class);

    @Override
    public boolean isValid(CourielValidatble courielDtoToAdd, ConstraintValidatorContext context) {
        logger.debug("Validating CourielDtoToAdd: {}", courielDtoToAdd);
        if (courielDtoToAdd == null) {
            logger.warn("Input is null");
            return false;
        }

        context.disableDefaultConstraintViolation();
        boolean isValid = validateCommonFields(courielDtoToAdd, context);
        logger.debug("Common fields validation result: {}", isValid);

        if (isValid && courielDtoToAdd.getType() != null && courielDtoToAdd.getNature() != null) {
            Couriel_Type type = Couriel_Type.valueOf(courielDtoToAdd.getType());
            Nature nature = Nature.valueOf(courielDtoToAdd.getNature());
            logger.debug("Type: {}, Nature: {}", type, nature);

            if (type == Couriel_Type.Arrivé && nature == Nature.Interne) {
                isValid &= validateEntrantIntern(courielDtoToAdd, context);
            } else if (type == Couriel_Type.Arrivé && nature == Nature.Externe) {
                isValid &= validateEntrantExtern(courielDtoToAdd, context);
            } else if (type == Couriel_Type.Départ && nature == Nature.Externe) {
                isValid &= validateSortantExtern(courielDtoToAdd, context);
            } else if (type == Couriel_Type.Départ && nature == Nature.Interne) {
                isValid &= validateSortantIntern(courielDtoToAdd, context);
            } else {
                logger.warn("Invalid combination: Type={}, Nature={}", type, nature);
                context.buildConstraintViolationWithTemplate(
                                "Invalid combination: Type=" + type + ", Nature=" + nature)
                        .addConstraintViolation();
                return false;
            }
        }

        logger.debug("Final validation result: {}", isValid);
        return isValid;
    }

    private boolean validateCommonFields(CourielValidatble courielDtoToAdd, ConstraintValidatorContext context) {
        boolean valid = true;

        if (courielDtoToAdd.getCourielNumber() == null) {
            logger.warn("courielNumber is null");
            addViolation(context, "courielNumber", "Couriel number is required");
            valid = false;
        }
        if (courielDtoToAdd.getSubject() == null || courielDtoToAdd.getSubject().isEmpty()) {
            logger.warn("subject is null or empty");
            addViolation(context, "subject", "Subject is required");
            valid = false;
        }
        if (courielDtoToAdd.getType() == null) {
            logger.warn("type is null");
            addViolation(context, "type", "Type is required");
            valid = false;
        }
        if (courielDtoToAdd.getNature() == null) {
            logger.warn("nature is null");
            addViolation(context, "nature", "Nature is required");
            valid = false;
        }
        if (courielDtoToAdd.getPriority() == null) {
            logger.warn("priority is null");
            addViolation(context, "priority", "Priority is required");
            valid = false;
        }

        logger.debug("Common fields validation result: {}", valid);
        return valid;
    }

    private boolean validateEntrantIntern(CourielValidatble courielDtoToAdd, ConstraintValidatorContext context) {
        boolean valid = true;

        if (courielDtoToAdd.getFromDivisionId() == null) {
            addViolation(context, "fromDivisionId", "From Division ID is required for ENTRANT INTERN");
            valid = false;
        }

        if (courielDtoToAdd.getFromDirectionId() != null && courielDtoToAdd.getFromDivisionId() == null) {
            addViolation(context, "fromDivisionId", "From Division ID is required when Direction is provided");
            valid = false;
        }

        if (courielDtoToAdd.getFromSousDirectionId() != null) {
            if (courielDtoToAdd.getFromDirectionId() == null) {
                addViolation(context, "fromDirectionId", "From Direction ID is required when Sous Direction is provided");
                valid = false;
            }
            if (courielDtoToAdd.getFromDivisionId() == null) {
                addViolation(context, "fromDivisionId", "From Division ID is required when Sous Direction is provided");
                valid = false;
            }
        }

        // Recipient (Récepteur) - Same partial hierarchy logic
        //I omit "toInter" because the system is gonna take it from the logging user
//        if (courielDtoToAdd.getToDivisionId() == null) {
//            addViolation(context, "toDivisionId", "To Division ID is required for ENTRANT INTERN");
//            valid = false;
//        }
//
//        if (courielDtoToAdd.getToDirectionId() != null && courielDtoToAdd.getToDivisionId() == null) {
//            addViolation(context, "toDivisionId", "To Division ID is required when Direction is provided");
//            valid = false;
//        }
//
//        if (courielDtoToAdd.getToSousDirectionId() != null) {
//            if (courielDtoToAdd.getToDirectionId() == null) {
//                addViolation(context, "toDirectionId", "To Direction ID is required when Sous Direction is provided");
//                valid = false;
//            }
//            if (courielDtoToAdd.getToDivisionId() == null) {
//                addViolation(context, "toDivisionId", "To Division ID is required when Sous Direction is provided");
//                valid = false;
//            }
//        }

        // External sender/receiver not allowed
        if (courielDtoToAdd.getFromExternal() != null) {
            addViolation(context, "fromExternal", "From external must not be provided for ENTRANT INTERN");
            valid = false;
        }
        if (courielDtoToAdd.getToExternal() != null) {
            addViolation(context, "toExternal", "To external must not be provided for ENTRANT INTERN");
            valid = false;
        }
//        if (courielDtoToAdd.getArrivedDate() == null) {
//            addViolation(context, "arrivedDate", "Arrived date is required");
//            valid = false;
//        }
//        if (courielDtoToAdd.getSentDate() != null) {
//            addViolation(context, "sentDate", "Sent date must be null for ENTRANT INTERN");
//            valid = false;
//        }
        return valid;
    }
    private boolean validateEntrantExtern(CourielValidatble courielDtoToAdd, ConstraintValidatorContext context) {
        boolean valid = true;

        // Required fields
        if (courielDtoToAdd.getFromExternal() == null) {
            addViolation(context, "fromExternal", "From external is required for ENTRANT EXTERN");
            valid = false;
        }
        // Required fields for division->Direction->SouDirection (Récepteur)
        //I omit "toInter" because the system is gonna take it from the logging user

//        if (courielDtoToAdd.getToDivisionId() == null) {
//            addViolation(context, "toDivisionId", " Division ID is required for ENTRANT INTERN");
//            valid = false;
//        }
//
//        if (courielDtoToAdd.getToDirectionId() != null && courielDtoToAdd.getToDivisionId() == null) {
//            addViolation(context, "toDirectionId", " Division ID is required when Direction is provided");
//            valid = false;
//        }
//        if (courielDtoToAdd.getToSousDirectionId() != null) {
//            if (courielDtoToAdd.getToDirectionId() == null) {
//                addViolation(context, "toDirectionId", "Direction ID is required when Sous Direction is provided");
//                valid = false;
//            }
//            if (courielDtoToAdd.getToDivisionId() == null) {
//                addViolation(context, "toDivisionId", "Division ID is required when Sous Direction is provided");
//                valid = false;
//            }
//        }

        // Forbidden fields
        if (courielDtoToAdd.getFromDivisionId() != null) {
            addViolation(context, "fromDivisionId", "From Division ID must not be provided for ENTRANT EXTERN");
            valid = false;
        }
        if (courielDtoToAdd.getFromDirectionId() != null) {
            addViolation(context, "fromDirectionId", "From Direction ID must not be provided for ENTRANT EXTERN");
            valid = false;
        }
        if (courielDtoToAdd.getFromSousDirectionId() != null) {
            addViolation(context, "fromSousDirectionId", "From Sous Direction ID must not be provided for ENTRANT EXTERN");
            valid = false;
        }
        if (courielDtoToAdd.getToExternal() != null) {
            addViolation(context, "toExternal", "To external must not be provided for ENTRANT EXTERN");
            valid = false;
        }
//        if (courielDtoToAdd.getArrivedDate() == null) {
//            addViolation(context, "arrivedDate", "Arrived date is required");
//            valid = false;
//        }
//        if (courielDtoToAdd.getSentDate() != null) {
//            addViolation(context, "sentDate", "Sent date must be null for ENTRANT EXTERN");
//            valid = false;
//        }

        return valid;
    }

    private boolean validateSortantExtern(CourielValidatble courielDtoToAdd, ConstraintValidatorContext context) {
        boolean valid = true;

        // Required fields
        if (courielDtoToAdd.getToExternal() == null) {
            addViolation(context, "toExternal", "To external is required for SORTANT EXTERN");
            valid = false;
        }
        // Required fields for division->Direction->SouDirection (expéditeur)
        //I omit "from" because the system is gonna take it from the logging user

//        if (courielDtoToAdd.getFromDivisionId() == null) {
//            addViolation(context, "fromDivisionId", "From Division ID is required for ENTRANT INTERN");
//            valid = false;
//        }
//
//        if (courielDtoToAdd.getFromDirectionId() != null && courielDtoToAdd.getFromDivisionId() == null) {
//            addViolation(context, "fromDivisionId", "From Division ID is required when Direction is provided");
//            valid = false;
//        }
//        if (courielDtoToAdd.getFromSousDirectionId() != null) {
//            if (courielDtoToAdd.getFromDirectionId() == null) {
//                addViolation(context, "fromDirectionId", "From Direction ID is required when Sous Direction is provided");
//                valid = false;
//            }
//            if (courielDtoToAdd.getFromDivisionId() == null) {
//                addViolation(context, "fromDivisionId", "From Division ID is required when Sous Direction is provided");
//                valid = false;
//            }
//        }

        // Forbidden fields
        if (courielDtoToAdd.getFromExternal() != null) {
            addViolation(context, "fromExternal", "From external must not be provided for SORTANT EXTERN");
            valid = false;
        }
        if (courielDtoToAdd.getToDivisionId() != null) {
            addViolation(context, "toDivisionId", "To Division ID must not be provided for SORTANT EXTERN");
            valid = false;
        }
        if (courielDtoToAdd.getToDirectionId() != null) {
            addViolation(context, "toDirectionId", "To Direction ID must not be provided for SORTANT EXTERN");
            valid = false;
        }
        if (courielDtoToAdd.getToSousDirectionId() != null) {
            addViolation(context, "toSousDirectionId", "To Sous Direction ID must not be provided for SORTANT EXTERN");
            valid = false;
        }
//        if (courielDtoToAdd.getSentDate() == null) {
//            logger.warn("sentDate is null");
//            addViolation(context, "sentDate", "Sent date is required");
//            valid = false;
//        }
//        if (courielDtoToAdd.getArrivedDate() != null) {
//            addViolation(context, "arrivedDate", "Arrived date must be null for SORTANT EXTERN");
//            valid = false;
//        }
        return valid;
    }

    private boolean validateSortantIntern(CourielValidatble courielDtoToAdd, ConstraintValidatorContext context) {
        boolean valid = true;

        // Required fields for division->Direction->SouDirection (expéditeur)
//        if (courielDtoToAdd.getFromDivisionId() == null) {
//            addViolation(context, "fromDivisionId", "From Division ID is required for ENTRANT INTERN");
//            valid = false;
//        }
//
//        if (courielDtoToAdd.getFromDirectionId() != null && courielDtoToAdd.getFromDivisionId() == null) {
//            addViolation(context, "fromDivisionId", "From Division ID is required when Direction is provided");
//            valid = false;
//        }
//        if (courielDtoToAdd.getFromSousDirectionId() != null) {
//            if (courielDtoToAdd.getFromDirectionId() == null) {
//                addViolation(context, "fromDirectionId", "From Direction ID is required when Sous Direction is provided");
//                valid = false;
//            }
//            if (courielDtoToAdd.getFromDivisionId() == null) {
//                addViolation(context, "fromDivisionId", "From Division ID is required when Sous Direction is provided");
//                valid = false;
//            }
//        }
        // Required fields for division->Direction->SouDirection (Récepteur)

        if (courielDtoToAdd.getToDivisionId() == null) {
            addViolation(context, "toDivisionId", " Division ID is required for ENTRANT INTERN");
            valid = false;
        }

        if (courielDtoToAdd.getToDirectionId() != null && courielDtoToAdd.getToDivisionId() == null) {
            addViolation(context, "toDirectionId", " Division ID is required when Direction is provided");
            valid = false;
        }
        if (courielDtoToAdd.getToSousDirectionId() != null) {
            if (courielDtoToAdd.getToDirectionId() == null) {
                addViolation(context, "toDirectionId", "Direction ID is required when Sous Direction is provided");
                valid = false;
            }
            if (courielDtoToAdd.getToDivisionId() == null) {
                addViolation(context, "toDivisionId", "Division ID is required when Sous Direction is provided");
                valid = false;
            }
        }
        // Forbidden fields
        if (courielDtoToAdd.getFromExternal() != null) {
            addViolation(context, "fromExternal", "From external must not be provided for SORTANT INTERN");
            valid = false;
        }
        if (courielDtoToAdd.getToExternal() != null) {
            addViolation(context, "toExternal", "To external must not be provided for SORTANT INTERN");
            valid = false;
        }
//        if (courielDtoToAdd.getSentDate() == null) {
//            logger.warn("sentDate is null");
//            addViolation(context, "sentDate", "Sent date is required");
//            valid = false;
//        }
//        if (courielDtoToAdd.getArrivedDate() != null) {
//            addViolation(context, "arrivedDate", "Arrived date must be null for SORTANT INTERN");
//            valid = false;
//        }
        return valid;
    }

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }


}
