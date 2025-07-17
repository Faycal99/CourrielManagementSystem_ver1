package dgb.Mp.Utils;

import dgb.Mp.generalAdvice.customException.FileDoesNotExistWithThatPath;
import dgb.Mp.generalAdvice.customException.ImageNotStoredException;
import dgb.Mp.generalAdvice.customException.PictureNotFoundException;
import lombok.Value;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtils {

    private static final String uploadDir = "C:\\images";
    // Define the folder where images will be stored on the server

    // Static method to encode an image to Base64
    public static void saveImageFromBase64(String uniqueFileName, String base64ImageData) throws IOException {
        byte[] imageData = Base64.decodeBase64(base64ImageData);// Decode Base64 to byte array

        // Save the image data to the file system
        Path path = Paths.get(uploadDir, uniqueFileName);
        Files.createDirectories(path.getParent());  // Ensure the directory exists

        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path.toFile()))) {
            stream.write(imageData);  // Write the byte array to the file system
        } catch (IOException e) {
            throw new ImageNotStoredException("Image " + uniqueFileName + " not stored: " + e.getMessage());
        }
    }

    // Static method to retrieve an image from the file system and encode it as Base64
    public static String retrieveImageAsBase64(String imagePath) throws IOException {
        File file = new File(imagePath);
        if (!file.exists()) {
            throw new FileDoesNotExistWithThatPath("File does not exist at the specified path: " + imagePath);
        }

        byte[] fileContent = Files.readAllBytes(file.toPath());  // Read the file content as byte array
        return Base64.encodeBase64String(fileContent);// Encode the byte array to Base64 string
    }


    public static String getFileExtension(String filename) {
        // Check if the filename is valid and has an extension
        if (filename == null || filename.isEmpty()) {
            return "";
        }


        int dotIndex = filename.lastIndexOf(".");


        if (dotIndex == -1) {
            return "";
        }


        return filename.substring(dotIndex + 1);
    }


    public static void deleteImage(String uniqueFileName) throws IOException {
        // Create the file path using the unique file name
        Path path = Paths.get(uploadDir, uniqueFileName);


        if (Files.exists(path)) {
            try {
                Files.delete(path); // Delete the file
                System.out.println("Image deleted: " + uniqueFileName);
            } catch (IOException e) {
                throw new IOException("Failed to delete image " + uniqueFileName + ": " + e.getMessage());
            }
        } else {
            throw new PictureNotFoundException("Image file does not exist: " + uniqueFileName);
        }


    }
    public static boolean isAPictureExtension(String pictureName ){
       switch (getFileExtension(pictureName)){
           case "jpg": return true;
           case "jpeg": return true;
           case "png": return true;
       }
       return false;

    }

}

