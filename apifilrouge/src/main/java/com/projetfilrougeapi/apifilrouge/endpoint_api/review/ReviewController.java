package com.projetfilrougeapi.apifilrouge.endpoint_api.review;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.ReviewRequest;
import com.projetfilrougeapi.apifilrouge.DTO.UserSummary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public EntityModel<Review> createReview(@RequestBody ReviewRequest review) {
        return reviewService.createReview(review);
    }

    @GetMapping
    public CollectionModel<EntityModel<Review>> getAllReview() {
        return reviewService.getAllReview();
    }

    @GetMapping("/{id}")
    public EntityModel<Review> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/{id}/senderUser")
    public EntityModel<UserSummary> getSenderUserByReviewId(@PathVariable Long id) {
        return reviewService.getSenderUserByReviewId(id);
    }

    @GetMapping("/{id}/reviewedEvent")
    public EntityModel<EventSummaryResponse> getReviewedEvent(@PathVariable Long id) {
        return reviewService.getReviewedEvent(id);
    }

    @GetMapping("/{id}/reviewedUser")
    public EntityModel<UserSummary> getReviewedUserByReviewId(@PathVariable Long id) {
        return reviewService.getReviewedUserByReviewId(id);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

}
