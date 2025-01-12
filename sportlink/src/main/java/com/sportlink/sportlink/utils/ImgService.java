package com.sportlink.sportlink.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ImgService {

    public static final String PATH_LOCATION = "location";
    public static final String PATH_ACCOUNT = "account";
    public static final String PATH_VOUCHER = "voucher";

    public static boolean saveImage(String dir, String fileName, MultipartFile file) {

        Path filePath = Paths.get(dir + '/' + fileName);
        // Save the file
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean deleteImage(String dir, String fileName) {
        // Construct the file path
        File file = new File(dir + File.separator + fileName);

        try {
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    public static Optional<Resource> getImage(String dir, String imageName) {
        File imageFile = new File(dir + File.separator + imageName);

        if (!imageFile.exists()) {
            return Optional.empty(); // Image not found
        }

        try {
            // Wrap the image file in an InputStreamResource
            return Optional.of( new InputStreamResource(new FileInputStream(imageFile)) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Optional.empty(); // Return null if file cannot be read
        }
    }

}
