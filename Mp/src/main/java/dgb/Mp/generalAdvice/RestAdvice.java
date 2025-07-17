package dgb.Mp.generalAdvice;

import dgb.Mp.Division.Division;
import dgb.Mp.generalAdvice.customException.DepartementNotFoundException;
import dgb.Mp.generalAdvice.customException.DivisionNotFoundException;
import dgb.Mp.generalAdvice.customException.SousDirectionNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestAdvice {

    @ExceptionHandler(DepartementNotFoundException.class)
    public ResponseEntity<String> handleDepartementNotFound(DepartementNotFoundException ex){
        return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DivisionNotFoundException.class)
    public ResponseEntity<String> handleDivisionNotFound(DivisionNotFoundException ex){
        return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(SousDirectionNotFoundException.class)
    public ResponseEntity<String> handleSousDirectionNotFound(SousDirectionNotFoundException ex){
        return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "A couriel with this number already exists.";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
