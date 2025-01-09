package com.sportlink.sportlink.media.location;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationMediaService {
    
    private static final String BASE_URL = "https://maps.googleapis.com";
    private static final String PATH = "/gdfg/";
    
    private final I_LocationMediaRepository locationMediaRepository;

    public LocationMediaService(I_LocationMediaRepository locationMediaRepository) {
        this.locationMediaRepository = locationMediaRepository;
    }

    public LocationMedia saveLocationImage(Long locationId, String img) {
        Optional<LocationMedia> opt = locationMediaRepository.findById(locationId);
        LocationMedia locationMedia;
        locationMedia = opt.orElseGet(LocationMedia::new);
        locationMedia.setId(locationId);
        locationMedia.getImgNames().add(img);
        return locationMediaRepository.save(locationMedia);
    }
    public List<String> findByIdImages(Long locationId) {
        Optional<LocationMedia> opt = locationMediaRepository.findById(locationId);
        if(opt.isEmpty()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        opt.get().getImgNames().stream().forEach(image -> {
            String url = BASE_URL + PATH+ image;
            result.add(url);
        });
        return result;
    }
    public void deleteLocationImage(Long locationId, String imageUUID) {
        Optional<LocationMedia> opt = locationMediaRepository.findById(locationId);
        if(opt.isEmpty()) {
            return;
        }
        opt.get().getImgNames().remove(imageUUID);
        locationMediaRepository.save(opt.get());
    }

}
