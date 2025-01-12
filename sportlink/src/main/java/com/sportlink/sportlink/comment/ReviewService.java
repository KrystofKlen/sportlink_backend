package com.sportlink.sportlink.comment;

import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.utils.DTO_Adapter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    I_LocationRepository locationRepository;
    I_ReviewRepository reviewRepository;
    DTO_Adapter adapter;

    public DTO_Review save(UserAccount account, DTO_Review dto) {
        Location location = locationRepository.findById(dto.getLocationId()).orElseThrow();

        Review review = new Review(
                null,
                account,
                dto.getContent(),
                location
        );

        review = reviewRepository.save(review);
        return adapter.getDTO_Review(review);
    }

    public List<DTO_Review> findAllForLocation(long locationId) {
        List<Review> reviews = reviewRepository.findAllForLocation(locationId);
        return reviews.stream().map(adapter::getDTO_Review).collect(Collectors.toList());
    }
}
