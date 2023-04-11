package fr.sqli.Cantine.service.images;

import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService {

    Environment env;
    @Autowired
    public  ImageService (Environment env){
        this.env = env;
    }
    private static final Logger LOG = LogManager.getLogger();

    /**
     * this method is used to upload an image to the server and make it in the Directory specified in the path
     * @param image  MultipartFile image
     * @param path   String path the spot where the image will be saved (the directory)
     * @return  String the name of the image
     * @throws ImagePathException   if the ( Directory ) path is invalid (null or empty)
     * @throws IOException if  JVM can't write the image in the directory
     * @throws InvalidImageException if the image is invalid (null or empty)
     * @throws InvalidTypeImageException if the image type is not jpg or png or jpeg
     */


    public String   uploadImage (MultipartFile image , String  path ) throws ImagePathException, IOException, InvalidImageException, InvalidTypeImageException {

        if  ( path == null || path.isEmpty()){
            LOG.fatal("CAN'T UPLOAD IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE uploadImage METHOD ");
            throw   new ImagePathException("INVALID PATH CAN'T UPLOAD IMAGE");
        }

        if (image == null || image.isEmpty()){
            LOG.error("CAN'T UPLOAD IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE uploadImage METHOD ");
            throw  new InvalidImageException("INVALID IMAGE IT CANNOT BE NULL OR EMPTY ");
        }


        if (image.getContentType() ==null || image.getContentType().isEmpty() ||
                (!image.getContentType().equals("image/png")
                && !image.getContentType().equals("image/jpg")
                && !image.getContentType().equals("image/jpeg")) ){
            LOG.error("CAN'T UPLOAD IMAGE BECAUSE THE IMAGE TYPE IS NOT VALID in the uploadImage METHOD");
            throw new InvalidTypeImageException("INVALID IMAGE TYPE ONLY PNG , JPG , JPEG ARE ACCEPTED ");
        }

        var name =  image.getOriginalFilename();
        name  = UUID.randomUUID().toString()+name ;
        var spot = path+"/"+name;
        File file =  new File(spot);
        image.transferTo(Path.of(file.getPath()));
        return name ;
    }

    /**
     * this method is used to download an image from the server to the client
     * @param ImageName image name
     * @param path path the spot where the image was  saved (the directory)
     * @return InputStream the image
     * @throws ImagePathException if the ( Directory ) path is invalid (null or empty)
     * @throws InvalidImageException if the image name is invalid (null or empty)
     */


    public InputStream downloadImage ( String ImageName , String path ) throws ImagePathException, InvalidImageException, FileNotFoundException {
        if (path == null ||path.isEmpty()  ){
            LOG.fatal("CAN'T DOWNLOAD IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE downloadImage METHOD ");
            throw new  ImagePathException("INVALID PATH CAN'T DOWNLOAD IMAGE");
        }
        if  (ImageName == null ||  ImageName.isEmpty() ){
           LOG.error("CAN'T DOWNLOAD IMAGE BECAUSE THE IMAGE NAME IS INVALID ITS EMPTY OR NULL IN THE downloadImage METHOD ");
           throw  new InvalidImageException("INVALID IMAGE NAME IT CANNOT BE NULL OR EMPTY ");
        }

        var spot =  path + "/" + ImageName;
        return  new FileInputStream(spot);
    }


}
