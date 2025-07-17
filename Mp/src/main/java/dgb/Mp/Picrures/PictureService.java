package dgb.Mp.Picrures;

import dgb.Mp.Picrures.Dtos.PictureDto;
import dgb.Mp.Picrures.Dtos.PictureToAdd;

import java.io.IOException;

public interface PictureService {


    public Picture getPictureById(Long id);

    public PictureDto getPictureDtoById(Long id);


    public PictureDto getPictureDtoByUrl(String url);

    public PictureDto addPictureToUser(PictureToAdd pictureToAdd,Long userId) throws IOException;

    public void deletePictureById(Long id) throws IOException;
}
