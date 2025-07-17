package dgb.Mp.generalAdvice.customException;

public class FileDoesNotExistWithThatPath extends RuntimeException{

    public FileDoesNotExistWithThatPath(String message){super(message);}
}
