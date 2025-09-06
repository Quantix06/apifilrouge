package com.projetfilrougeapi.apifilrouge.endpoint_api.review;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false, updatable = false, unique = true)
    private long review_id;


    private String content;
    private Double rating;
    Date createdAt = new Date();

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonBackReference("user-reports-sent")
    @JsonIgnore
    private User senderReviewUser;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonBackReference("user-reports-received")
    @JsonIgnore
    private User reviewedUser;


    public Review(String content, Double rating, User senderReviewUser, User reviewedUser) {
        this.content = content;
        this.rating = rating;
        this.senderReviewUser = senderReviewUser;
        this.reviewedUser = reviewedUser;
    }
}
