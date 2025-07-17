package dgb.Mp.Picrures;

import dgb.Mp.Picrures.Dtos.PictureDto;
import dgb.Mp.Picrures.Dtos.PictureToAdd;
import dgb.Mp.Utils.ImageUtils;
import dgb.Mp.Utils.Mapper;
import dgb.Mp.generalAdvice.customException.FileIsNotPicture;
import dgb.Mp.generalAdvice.customException.PictureNotFoundException;
import dgb.Mp.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class PictureServiceimpl implements PictureService {


    private final PictureRepository pictureRepository;
@Autowired
    private  Mapper mapper;

    @Override
    public Picture getPictureById(Long id) {
        return pictureRepository.findById(id).orElseThrow(()->new PictureNotFoundException("Picture not found with id: " + id));
    }

    @Override
    public PictureDto getPictureDtoById(Long id) {
        return mapper.toPictureDto(getPictureById(id));
    }


    @Override
    public PictureDto getPictureDtoByUrl(String url) {
        return mapper.toPictureDto(pictureRepository.findPictureByPicturePath(url));
    }

    @Override
    public PictureDto addPictureToUser(PictureToAdd pictureToAdd, Long userId) throws IOException {



       if(! ImageUtils.isAPictureExtension(pictureToAdd.getName())){

           throw new FileIsNotPicture("Please upload only File With png , jpg , jpeg extensions: " + pictureToAdd.getName());
       };
        Picture picture = new Picture();
        picture.setPicturePath(UUID.randomUUID().toString() + "_" +pictureToAdd.getName());
        picture.setExtension(ImageUtils.getFileExtension(pictureToAdd.getName()));


        ImageUtils.saveImageFromBase64(picture.getPicturePath() ,pictureToAdd.getBase64Content());

        return mapper.toPictureDto(pictureRepository.save(picture)) ;
    }

    @Override
    public void deletePictureById(Long id) throws IOException {

        Picture picture = getPictureById(id);

        ImageUtils.deleteImage(picture.getPicturePath());

        pictureRepository.delete(picture);


    }
}
