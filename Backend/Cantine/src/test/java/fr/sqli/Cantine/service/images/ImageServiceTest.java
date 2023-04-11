package fr.sqli.Cantine.service.images;

import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
@DisplayName(" Class To  test  Upload And  DownLoad Image  ")
class ImageServiceTest {

    @Autowired
    private  Environment environment;
    @Autowired
    private  ImageService imageService;


    /* unit Test For  Download Images */

    /**
     *  Test the downloadImage method with valid image
     * @throws FileNotFoundException  : If the image is not found
     * @throws InvalidImageException  : If the image name is null or empty
     * @throws ImagePathException     : If the path is null or empty
     */
   @Test
   void downloadValidateImage () throws FileNotFoundException, InvalidImageException, ImagePathException {
         var userImageUrl = environment.getProperty("sqli.cantine.default.user.imagename");
         var image = imageService.downloadImage(userImageUrl, "images/users/") ;

         Assertions.assertNotNull(image);

   }

    /**
     * Test the downloadImage method with UnExisting image
     */

    @Test
    void downloadNotFoundImage (){
       var userImageUrl = environment.getProperty("sqli.cantine.default.user.imagename");
        Assertions.assertThrows(FileNotFoundException.class,
                () -> imageService.downloadImage("default", userImageUrl));
    }

    /**
     * Test the downloadImage method with null image name
     */

    @Test
    void downloadImageWithNULLImageName(){
        Assertions.assertThrows(InvalidImageException.class,
                () -> imageService.downloadImage(null,"test"));
    }

    /**
     *  Test the downloadImage method with empty image name
     */

    @Test
    void downloadImageWithEmptyImageName(){
        Assertions.assertThrows(InvalidImageException.class,
                () -> imageService.downloadImage("","test"));
    }

    /**
     * Test the downloadImage method with null path
     */
    @Test
    void downloadImageWithNullPath(){
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.downloadImage("test",null));
    }


    /**
     * Test the downloadImage method with empty path
     */

    @Test
     void downloadImageWithEmptyPath(){
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.downloadImage("test",""));
    }





    /*  Unit TEST FOR  UPLOAD IMAGE */

     /*
        * Test the uploadImage method with valid image format (JPG)
        *  use the imageService.uploadImage method to upload the image in "src/test/java/fr/sqli/Cantine/service/images/filesTests/"
        * and check if the image is uploaded successfully with  Assertions.assertTrue(newimage.exists());
        * and   delete the image after the test
      */
     @Test
     void uploadImageWithValidFile () throws IOException, InvalidTypeImageException, InvalidImageException, ImagePathException {
         File imageGIF =    new File("src/test/java/fr/sqli/Cantine/service/images/filesTests/normalimage.jpg");
         FileInputStream input = new FileInputStream(imageGIF);
         MockMultipartFile multipartFile = new MockMultipartFile("normalimage",
                 imageGIF.getName(), "image/jpg", input);
       var image =  this.imageService.uploadImage(multipartFile, "src/test/java/fr/sqli/Cantine/service/images/filesTests/");
       var newimagepath =  "src/test/java/fr/sqli/Cantine/service/images/filesTests/"+image;
       File  newimage = new File(newimagepath);
       Assertions.assertTrue(newimage.exists());
       newimage.delete();

     }

    /**
     * Test the uploadImage method with  invalid image format (GIF)
     * @throws IOException
     */

     @Test
     void uploadImageWithInvalidTypeGIF() throws IOException {
         File imageGIF =    new File("src/test/java/fr/sqli/Cantine/service/images/filesTests/Babidi_TestGifFormat.gif");
         MockMultipartFile multipartFile = new MockMultipartFile("file", imageGIF.getName(),
                 "image/gif", "test data".getBytes());
         Assertions.assertThrows(InvalidTypeImageException.class,
                 () -> imageService.uploadImage(multipartFile, "images/users/"));
     }

     /**
      * Test the uploadImage method with invalid  image Type (svg)
      *
      */
    @Test
    void uploadImageWithInvalidTypeSVG(){
       File imageSVG =    new File("src/test/java/fr/sqli/Cantine/service/images/filesTests/testSvgType.svg");
        System.out.println(imageSVG.exists());
       MockMultipartFile multipartFile = new MockMultipartFile("file", imageSVG.getName(),
               "image/svg", "test data".getBytes());
        Assertions.assertThrows(InvalidTypeImageException.class,
                () -> imageService.uploadImage(multipartFile, "images/users/"));
    }


    /**
     * Test the uploadImage method with invalid  image  (empty  file )
     *
     */
    @Test
    void uploadImageWithEmtyImage(){
        Assertions.assertThrows(InvalidImageException.class,
                () -> imageService.uploadImage(new MockMultipartFile("file", new byte[0]), "images/users/"));
    }


    /**
     * Test the uploadImage method with Null image (null  file )
     */
    @Test
    void uploadImagewithNullImage(){
        Assertions.assertThrows(InvalidImageException.class, () -> imageService.uploadImage(null, "images/users/"));
    }

    /**
     * Test the uploadImage method with Null path image
     *  A real path  is  used to create the MultipartFile
     */
    @Test
    void uploadImagewithNullPathImage() throws IOException {
        var    defaultUserImageName = environment.getProperty("sqli.cantine.default.user.imagename");
        File image   =  new File("images/users/"+defaultUserImageName);
        FileInputStream input = new FileInputStream(image);
        MultipartFile multipartFile = new MockMultipartFile("file", image.getName(), "text/plain", input);
        Assertions.assertThrows(ImagePathException.class, () -> imageService.uploadImage(multipartFile, null));
    }

    /**
     * Test the uploadImage method with Empty path image
     *  A real path  is  used to create the MultipartFile
     */
    @Test
    void uploadImagewithEmptyPathImage() throws IOException {
        var    defaultUserImageName = environment.getProperty("sqli.cantine.default.user.imagename");
        File image   =  new File("images/users/"+defaultUserImageName);
        FileInputStream input = new FileInputStream(image);
        MultipartFile multipartFile = new MockMultipartFile("file", image.getName(), "text/plain", input);
        Assertions.assertThrows(ImagePathException.class, () -> imageService.uploadImage(multipartFile, ""));
    }



}