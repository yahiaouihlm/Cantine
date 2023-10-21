package fr.sqli.cantine.service.images;

import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService implements IImageService {

    private static final Logger LOG = LogManager.getLogger();
    Environment env;

    @Autowired
    public ImageService(Environment env) {
        this.env = env;
    }

    @Override
    public String uploadImage(MultipartFile image, String path) throws ImagePathException, IOException, InvalidImageException, InvalidFormatImageException {

        if (path == null || path.isEmpty()) {
            LOG.fatal("CAN'T UPLOAD IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE uploadImage METHOD ");
            throw new ImagePathException("INVALID PATH CAN'T UPLOAD IMAGE");
        }
        if  (!Files.exists(Path.of(path))){
            LOG.fatal("CAN'T UPLOAD IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE uploadImage METHOD ");
            throw new ImagePathException("INVALID PATH CAN'T UPLOAD IMAGE");
        }

        if (image == null || image.isEmpty()) {
            LOG.error("CAN'T UPLOAD IMAGE BECAUSE THE IMAGE IS INVALID ITS EMPTY OR NULL IN THE uploadImage METHOD ");
            throw new InvalidImageException("INVALID IMAGE IT CANNOT BE NULL OR EMPTY ");
        }

        if (image.getContentType() == null || image.getContentType().isEmpty() ||
                (!image.getContentType().equals("image/png")
                        && !image.getContentType().equals("image/jpg")
                        && !image.getContentType().equals("image/jpeg")
                )) {
            LOG.error("CAN'T UPLOAD IMAGE BECAUSE THE IMAGE TYPE IS NOT VALID in the uploadImage METHOD");
            throw new InvalidFormatImageException("INVALID IMAGE TYPE ONLY PNG , JPG , JPEG   ARE ACCEPTED");
        }

        var name = image.getOriginalFilename();
        name = UUID.randomUUID() + name;
        var spot = path + "/" + name;
        File file = new File(spot);
        image.transferTo(Path.of(file.getPath()));
        return name;
    }


    @Override
    public InputStream downloadImage(String ImageName, String path) throws ImagePathException, InvalidImageException, FileNotFoundException {
        if (path == null || path.isEmpty()) {
            LOG.fatal("CAN'T DOWNLOAD IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE downloadImage METHOD ");
            throw new ImagePathException("INVALID PATH CAN'T DOWNLOAD IMAGE");
        }
        if (ImageName == null || ImageName.isEmpty() || ImageName.isBlank()) {
            LOG.error("CAN'T DOWNLOAD IMAGE BECAUSE THE IMAGE NAME IS INVALID ITS EMPTY OR NULL IN THE downloadImage METHOD ");
            throw new InvalidImageException("INVALID IMAGE NAME IT CANNOT BE NULL OR EMPTY ");
        }

        var spot = path + "/" + ImageName;
        Path of = Path.of(path);

        if (!Files.exists(of)) {
            ImageService.LOG.error("CAN'T DOWNLOAD IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE downloadImage METHOD ");
            throw new FileNotFoundException("IMAGE NOT FOUND");
        }

        return new FileInputStream(spot);
    }


    @Override
    public void deleteImage(String ImageName, String path) throws ImagePathException {
        if (path == null || path.isEmpty()) {
            LOG.fatal("CAN'T DELETE IMAGE BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE deleteImage METHOD ");
            throw new ImagePathException("INVALID PATH CAN'T DELETE IMAGE");
        }
        if (ImageName == null || ImageName.isEmpty()) {
            LOG.error("CAN'T DELETE IMAGE BECAUSE THE IMAGE NAME IS INVALID ITS EMPTY OR NULL IN THE deleteImage METHOD ");
            throw new ImagePathException("INVALID IMAGE NAME IT CANNOT BE NULL OR EMPTY ");
        }
        var spot = path + "/" + ImageName;

        File file = new File(spot);
        if (file.exists()) {
            file.delete();
            LOG.info("FILE DELETED SUCCESSFULLY");
        } else {
            LOG.error("CAN'T DELETE THE FILE");
            throw new ImagePathException("CAN'T DELETE THE FILE NOT FOUND");
        }

    }

    @Override
    public String updateImage(String oldImageName, MultipartFile image, String path) throws ImagePathException, InvalidFormatImageException, InvalidImageException, IOException {
        var imageName = this.uploadImage(image, path);

        this.deleteImage(oldImageName, path);

        return imageName;
    }

    @Override
    public String getImageExtension(String ImageName) throws InvalidImageException {
        if (ImageName == null || ImageName.isEmpty()) {
            ImageService.LOG.error("CAN'T GET IMAGE EXTENSION BECAUSE THE IMAGE NAME IS INVALID ITS EMPTY OR NULL IN THE getImageExtension METHOD ");
            throw new InvalidImageException("INVALID IMAGE NAME ");
        }
        var extension = "";
        try {
            extension = ImageName.substring(ImageName.lastIndexOf('.'));
        } catch (Exception e) {
            ImageService.LOG.error("CAN'T GET IMAGE EXTENSION BECAUSE THE IMAGE NAME IS INVALID ITS EMPTY OR NULL IN THE getImageExtension METHOD ");
            throw new InvalidImageException("INVALID IMAGE NAME");
        }
        if (extension.isEmpty() || !extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")
        ) {
            ImageService.LOG.error("CAN'T GET IMAGE EXTENSION BECAUSE THE IMAGE NAME IS INVALID ITS EMPTY OR NULL IN THE getImageExtension METHOD ");
            throw new InvalidImageException("INVALID IMAGE NAME");

        }

        return extension;
    }

    @Override
    public boolean isImageExist(String ImageName, String pathDirectory) throws ImagePathException, InvalidImageException {
        if (pathDirectory == null || pathDirectory.isEmpty()) {
            LOG.fatal("CAN'T CHECK IF IMAGE EXIST BECAUSE THE PATH IS INVALID ITS EMPTY OR NULL IN THE isImageExist METHOD ");
            throw new ImagePathException("INVALID PATH CAN'T CHECK IF IMAGE EXIST");
        }
        if (ImageName == null || ImageName.isEmpty()) {
            LOG.error("CAN'T CHECK IF IMAGE EXIST BECAUSE THE IMAGE NAME IS INVALID ITS EMPTY OR NULL IN THE isImageExist METHOD ");
            throw new InvalidImageException("INVALID IMAGE NAME IT CANNOT BE NULL OR EMPTY ");
        }
        File directory = new File(pathDirectory);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(ImageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
