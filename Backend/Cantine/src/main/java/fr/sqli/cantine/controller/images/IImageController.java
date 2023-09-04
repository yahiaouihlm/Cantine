package fr.sqli.cantine.controller.images;

public interface IImageController {

       String ENDPOINT_GET_IMAGE= "cantine/download/images/{spot}/{image}";
       String ENDPOINT_GET_IMAGE_USERS= "cantine/download/images/persons/{spot}/{image}";

}
