package dgb.Mp.generalAdvice.customException;

public class SousDirectionNotFoundException extends RuntimeException
{
    public SousDirectionNotFoundException(Long id) {
        super("Sous Direction " + id + " not found");
    }
}
