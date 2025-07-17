package dgb.Mp.generalAdvice.customException;

public class DirectionGeneraleNotFoundException extends RuntimeException {
    public DirectionGeneraleNotFoundException(long id) { super("DirectionGenerale " + id + " not found");
    }
}
