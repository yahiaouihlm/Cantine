package fr.sqli.cantine.controller.images;

import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;

@RestController
public class ImageController{
    final String ENDPOINT_GET_IMAGE= "cantine/download/images/{spot}/{image}";
    final String ENDPOINT_GET_IMAGE_USERS= "cantine/download/images/persons/{spot}/{image}";


    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(ENDPOINT_GET_IMAGE)
    public ResponseEntity<InputStreamResource> getFoodImage(@PathVariable("spot") String spot, @PathVariable("image") String image) throws FileNotFoundException, InvalidImageException, ImagePathException {

        var path = "images/" + spot;
        return this.getImage(spot, image, path);
    }


  @GetMapping(ENDPOINT_GET_IMAGE_USERS)
    public ResponseEntity<InputStreamResource> getUsersImages(@PathVariable("spot") String spot, @PathVariable("image") String image) throws FileNotFoundException, InvalidImageException, ImagePathException {

        var path = "images/persons/" + spot;
        return this.getImage(spot, image, path);


    }



    private   ResponseEntity<InputStreamResource> getImage( String spot ,  String  image ,  String  path ) throws InvalidImageException, ImagePathException, FileNotFoundException {
        var imageExtension = this.imageService.getImageExtension(image);
        MediaType mediaType = switch (imageExtension) {
            case ".png" -> MediaType.IMAGE_PNG;
            case ".jpg" -> MediaType.IMAGE_JPEG;
            case ".jpeg" -> MediaType.IMAGE_JPEG;
            default -> null;
        };
        InputStream inputStream = this.imageService.downloadImage(image, path);
        assert mediaType != null;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new InputStreamResource(inputStream));

    }



    /*TODO
    * 1-  ajouter  une autorisation pour les images utilisateurs
     */

}
