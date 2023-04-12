package fr.sqli.Cantine.service.images;

import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface IImageService {
    /**
     * this method is used to upload an image to the server and make it in the Directory specified in the path
     *
     * @param image MultipartFile image
     * @param path  String path the spot where the image will be saved (the directory)
     * @return String the name of the image
     * @throws ImagePathException        if the ( Directory ) path is invalid (null or empty)
     * @throws IOException               if  JVM can't write the image in the directory
     * @throws InvalidImageException     if the image is invalid (null or empty)
     * @throws InvalidTypeImageException if the image type is not jpg or png or jpeg
     * @Note <Strong> Please   Don't Add     / at The End Of Path   </Strong>
     */


    String uploadImage(MultipartFile image, String path) throws ImagePathException, IOException, InvalidImageException, InvalidTypeImageException;


    /**
     * this method is used to download an image from the server to the client
     *
     * @param ImageName image name
     * @param path      path the spot where the image was  saved (the directory)
     * @return InputStream the image
     * @throws ImagePathException    if the ( Directory ) path is invalid (null or empty)
     * @throws InvalidImageException if the image name is invalid (null or empty)
     * @Note <Strong> Please   Don't Add     / at The End Of Path   </Strong>
     */
    InputStream downloadImage(String ImageName, String path) throws ImagePathException, InvalidImageException, FileNotFoundException;


    /**
     * this method is used to delete an image from the server (the directory) specified in the path
     *
     * @param ImageName image name to delete
     * @param path      path the spot where the image was  saved (the directory)
     * @throws ImagePathException if the ( Directory ) path is invalid (null or empty) or the image name is invalid (null or empty)
     *                            or the image doesn't exist in the directory The Exception will be thrown And HTTP Status Internal Server Error
     *                            will be sent to the client Because the ( ImaheName , path ) Must be valid Because  its came from the database
     *                            and  the  ImageName  File  must be existed in the directory specified in the path
     * @Note <Strong> Please   Don't Add     / at The End Of Path   </Strong>
     */
    void deleteImage(String ImageName, String path) throws ImagePathException;
}
