package dgb.Mp.generalAdvice.customException;

public class DirectionNotFoundException extends RuntimeException {
    public DirectionNotFoundException(Long id ) {
        super(" Direction " + id + " not found");
    }
}
