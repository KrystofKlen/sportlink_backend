package com.sportlink.sportlink.comment;

import com.sportlink.sportlink.account.user.UserAccount;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
public class ReviewRepository {

    private final ReviewService reviewService;

    @GetMapping
    public List<DTO_Review> getReviewsForLocation(@PathVariable long locationId) {
        return reviewService.findAllForLocation(locationId);
    }

    @PostMapping
    public ResponseEntity<DTO_Review> postReview(@RequestBody DTO_Review review) {
        UserAccount account = new UserAccount();
        DTO_Review dto = reviewService.save(account, review);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}
