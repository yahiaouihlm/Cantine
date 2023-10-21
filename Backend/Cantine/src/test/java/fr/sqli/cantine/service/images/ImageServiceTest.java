package fr.sqli.cantine.service.images;

import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.junit.jupiter.api.*;
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
    private Environment environment;
    @Autowired
    private ImageService imageService;

    private String IMAGE_DIRECTORY;
    private String IMAGE_TEST_URL;

    @BeforeEach
    void  initImageTest () {
        IMAGE_DIRECTORY = environment.getProperty("sqli.cantine.default.student.images.directory");
        var imageNAME  = environment.getProperty("sqli.cantine.default.student.images.file");
        IMAGE_TEST_URL =IMAGE_DIRECTORY +"/"+imageNAME;
    }

    /* unit Test For  Delete Images */

    /**
     * Test the deleteImage method with valid image name and path
     *
     * @throws IOException : file.createNewFile() throws IOException we use it  to create the file after the delete method
     *                     to  save  the aytomatic test from  failing
     */
    @Test
    void deleteImageWithValidImage() throws IOException {
        String imageName = "testRemove.gif";
        String path = "src/test/java/fr/sqli/Cantine/service/images/filesTests";
        File file = new File(path + '/' + imageName);
        Assertions.assertDoesNotThrow(() -> imageService.deleteImage(imageName, path));
        file.createNewFile();

    }

    /**
     * Test the deleteImage method with valid image
     *
     * @throws InvalidImageException
     * @throws ImagePathException
     */
    @Test
    void deleteImageWithInvalidImage() {
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.deleteImage("test", "meal"));
    }

    @Test
    @DisplayName("Test the deleteImage method with empty image name")
    void deleteImageWithEmptyImgaeName() {
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.deleteImage("", "meal"));
    }


    @Test
    @DisplayName("Test the deleteImage method with null image name")
    void deleteImageWithNullImgaeName() {
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.deleteImage(null, "meal"));
    }


    @Test
    @DisplayName("Test the deleteImage method with null path")
    void deleteImageWithNullPath() {
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.deleteImage("meal", null));
    }


    @Test
    @DisplayName("Test the deleteImage method with empty path")
    void deleteImageWithEmptyPath() {
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.deleteImage("meal", ""));
    }


    /* unit Test For  Download Images */

    /**
     * Test the downloadImage method with valid image
     *
     * @throws FileNotFoundException : If the image is not found
     * @throws InvalidImageException : If the image name is null or empty
     * @throws ImagePathException    : If the path is null or empty
     */
    @Test
    void downloadValidateImage() throws FileNotFoundException, InvalidImageException, ImagePathException {
        var userImageUrl = environment.getProperty("sqli.cantine.default.user.imagename");
        var image = imageService.downloadImage(userImageUrl, "images/users/");

        Assertions.assertNotNull(image);

    }


    @Test
    @DisplayName("Test the downloadImage method with UnExisting image")
    void downloadNotFoundImage() {
        var userImageUrl = environment.getProperty("sqli.cantine.default.user.imagename");
        Assertions.assertThrows(FileNotFoundException.class,
                () -> imageService.downloadImage("default", userImageUrl));
    }


    @Test
    @DisplayName("Test the downloadImage method with null image name")
    void downloadImageWithNULLImageName() {
        Assertions.assertThrows(InvalidImageException.class,
                () -> imageService.downloadImage(null, "test"));
    }


    @Test
    @DisplayName("Test the downloadImage method with empty image name")
    void downloadImageWithEmptyImageName() {
        Assertions.assertThrows(InvalidImageException.class,
                () -> imageService.downloadImage("", "test"));
    }


    @Test
    @DisplayName("Test the downloadImage method with null path")
    void downloadImageWithNullPath() {
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.downloadImage("test", null));
    }


    @Test
    @DisplayName("Test the downloadImage method with empty path")
    void downloadImageWithEmptyPath() {
        Assertions.assertThrows(ImagePathException.class,
                () -> imageService.downloadImage("test", ""));
    }





  /********************************************************** UPLOAD IMAGE  *******************************************************/



    /*  Unit TEST FOR  UPLOAD IMAGE */

    /**
     * Test the uploadImage method with valid image format (JPG)
     * use the imageService.uploadImage method to upload the image in "src/test/java/fr/sqli/Cantine/service/images/filesTests/"
     * and check if the image is uploaded successfully with  Assertions.assertTrue(newimage.exists());
     * and   delete the image after the test
     */

    /////// HELLO WORLD
    @Test
    void uploadImageWithValidFile() throws IOException, InvalidFormatImageException, InvalidImageException, ImagePathException {

       //  Image to  upload
        File imageGIF = new File(IMAGE_TEST_URL);
        FileInputStream input = new FileInputStream(imageGIF);
        MockMultipartFile multipartFile = new MockMultipartFile("Images", imageGIF.getName(), "image/jpg", input);

        // use  the  uploadImage method  in ImageService
        var imageName = this.imageService.uploadImage(multipartFile,IMAGE_DIRECTORY );



         var newimagepath = IMAGE_DIRECTORY + "/" + imageName;
         File newimage = new File(newimagepath);
        Assertions.assertTrue(newimage.exists());
        Assertions.assertTrue (newimage.delete());

    }




    @Test
    @DisplayName("Test the uploadImage method with invalid  image Type (gif)")
    void uploadImageWithInvalidTypeGIF() throws IOException {
        File imageGIF = new File("src/test/java/fr/sqli/Cantine/service/images/filesTests/Babidi_TestGifFormat.gif");
        MockMultipartFile multipartFile = new MockMultipartFile("file", imageGIF.getName(),
                "image/gif", "test data".getBytes());
        Assertions.assertThrows(InvalidFormatImageException.class,
                () -> imageService.uploadImage(multipartFile, IMAGE_TEST_URL));
    }




    @Test
    @DisplayName("Test the uploadImage method with invalid  image Type (svg)")
    void uploadImageWithInvalidTypeSVG() {
        File imageSVG = new File("src/test/java/fr/sqli/Cantine/service/images/filesTests/testSvgType.svg");
        System.out.println(imageSVG.exists());
        MockMultipartFile multipartFile = new MockMultipartFile("file", imageSVG.getName(),
                "image/svg", "test data".getBytes());
        Assertions.assertThrows(InvalidFormatImageException.class,
                () -> imageService.uploadImage(multipartFile, IMAGE_TEST_URL));
    }




    @Test
    @DisplayName("Test the uploadImage method with empty image (empty  file )")
    void uploadImageWithEmptyImage() {
        Assertions.assertThrows(InvalidImageException.class,
                () -> imageService.uploadImage(new MockMultipartFile("file", new byte[0]), IMAGE_TEST_URL));
    }


    @Test
    @DisplayName("Test the uploadImage method with Null image (null  file )")
    void uploadImageWithNullImage() {
        Assertions.assertThrows(InvalidImageException.class, () -> imageService.uploadImage(null, IMAGE_TEST_URL));
    }



    @Test
    void uploadImageWithInvalidPath() throws IOException {
        File image = new File(this.IMAGE_TEST_URL);
        FileInputStream input = new FileInputStream(image);
        MultipartFile multipartFile = new MockMultipartFile("file", image.getName(), "text/plain", input);
        Assertions.assertThrows(ImagePathException.class, () -> imageService.uploadImage(multipartFile, "wrong/path"));
    }




    @Test
    @DisplayName("Test the uploadImage method with Empty path image A real path  is  used to create the MultipartFile")
    void uploadImageWithEmptyPathImage() throws IOException {
        File image = new File(this.IMAGE_TEST_URL);
        FileInputStream input = new FileInputStream(image);
        MultipartFile multipartFile = new MockMultipartFile("file", image.getName(), "text/plain", input);
        Assertions.assertThrows(ImagePathException.class, () -> imageService.uploadImage(multipartFile, ""));
    }



    @Test
    void uploadImageWithNullPath () throws IOException {
        File image = new File(this.IMAGE_TEST_URL);
        FileInputStream input = new FileInputStream(image);
        MultipartFile multipartFile = new MockMultipartFile("file", image.getName(), "text/plain", input);
        Assertions.assertThrows(ImagePathException.class, () -> imageService.uploadImage(multipartFile , null));
    }





}