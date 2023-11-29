package fr.sqli.cantine.controller.images;

import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.FileNotFoundException;

public interface IImageController {

       String ENDPOINT_GET_IMAGE= "cantine/download/images/{spot}/{image}";
       String ENDPOINT_GET_IMAGE_USERS= "cantine/download/images/persons/{spot}/{image}";




}
