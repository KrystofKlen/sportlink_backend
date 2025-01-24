package com.sportlink.sportlink.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ImgService {


    public final String PATH_LOCATION;
    public final String PATH_ACCOUNT;
    public final String PATH_VOUCHER;

    public ImgService( @Value("${dir.images.location}") String pathLocation,@Value("${dir.images.account}") String pathAccount, @Value("${dir.images.voucher}") String pathVoucher) {
        PATH_LOCATION = pathLocation;
        PATH_ACCOUNT = pathAccount;
        PATH_VOUCHER = pathVoucher;
    }

    public boolean saveImage(String dir, String fileName, MultipartFile file) {

        Path filePath = Paths.get(dir + '/' + fileName);
        // Save the file
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean deleteImage(String dir, String fileName) {
        // Construct the file path
        File file = new File(dir + File.separator + fileName);

        try {
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Resource> getImage(String dir, String imageName) {
        Path filePath = Paths.get(dir + File.separator + imageName);
        try {
            // Wrap the image file in an InputStreamResource
            Resource resource = new UrlResource(filePath.toUri());
            return Optional.of(resource);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
