package dgb.Mp.generalAdvice.customException;

public class DivisionNotFoundException extends RuntimeException {
    public DivisionNotFoundException(Long id) { super("Division" + id + "not found");}
}
