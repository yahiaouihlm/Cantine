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
     * <Strong> Please   Don't Add     / at The End Of Path   </Strong>
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
     * <Strong> Please   Don't Add     / at The End Of Path   </Strong>
     *
     * @param ImageName image name
     * @param path      path the spot where the image was  saved (the directory)
     * @return InputStream the image
     * @throws ImagePathException    if the ( Directory ) path is invalid (null or empty)
     * @throws InvalidImageException if the image name is invalid (null or empty)
     */
    InputStream downloadImage(String ImageName, String path) throws ImagePathException, InvalidImageException, FileNotFoundException;


    /**
     * this method is used to delete an image from the server (the directory) specified in the path
     * <Strong> Please   Don't Add     / at The End Of Path   </Strong>
     *
     * @param ImageName image name to delete
     * @param path      path the spot where the image was  saved (the directory)
     * @throws ImagePathException if the ( Directory ) path is invalid (null or empty) or the image name is invalid (null or empty)
     *                            or the image doesn't exist in the directory The Exception will be thrown And HTTP Status Internal Server Error
     *                            will be sent to the client Because the ( ImaheName , path ) Must be valid Because  its came from the database
     *                            and  the  ImageName  File  must be existed in the directory specified in the path
     */
    void deleteImage(String ImageName, String path) throws ImagePathException;

    /**
     * this method is used to update an image in the server (the directory) specified in the path and the old image will be deleted from the directory
     * its remove the old image and upload the new image and return the new image name
     * <Strong> Please   Don't Add     / at The End Of Path   </Strong>
     *
     * @param oldImageName the old image name to delete
     * @param image        the new image as MultipartFile  to upload and save in the directory specified in the path
     * @param path         the path of the directory where the image will be saved
     * @return String the new image name
     * @throws ImagePathException        if the ( Directory ) path is invalid (null or empty) or the image name is invalid (null or empty)
     * @throws InvalidTypeImageException if the image type is not jpg or png or jpeg
     * @throws InvalidImageException     if the image file  is invalid (null or empty)
     * @throws IOException               if JVM can't write the image in the directory
     */

    String updateImage(String oldImageName, MultipartFile image, String path) throws ImagePathException, InvalidTypeImageException, InvalidImageException, IOException;
}
