package fr.sqli.Cantine.controller.images;

import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.InputStream;

@RestController
public class ImageController implements IImageContriller {
    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(ENDPOINT_GET_IMAGE)
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("spot") String spot, @PathVariable("image") String image) throws FileNotFoundException, InvalidImageException, ImagePathException {
        var path = "images/" + spot;
        var imageExtension = this.imageService.getImageExtension(image);
        MediaType mediaType = null;
        switch (imageExtension) {
            case ".png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case ".jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case ".jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
        }

        InputStream inputStream = this.imageService.downloadImage(image, path);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new InputStreamResource(inputStream));


    }


    /*TODO
    * 1-  ajouter  une autorisation pour les images utilisateurs
     */

}
