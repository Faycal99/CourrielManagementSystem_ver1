package dgb.Mp.generalAdvice.customException;

public class DepartementNotFoundException extends RuntimeException {
    public DepartementNotFoundException(Long id) {
        super("Departement " + id + " not found");
    }
}
