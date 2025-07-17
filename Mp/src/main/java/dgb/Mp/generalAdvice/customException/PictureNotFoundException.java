package dgb.Mp.generalAdvice.customException;

public class PictureNotFoundException extends  RuntimeException{

    public PictureNotFoundException(String message){
        super(message);
    }
}
