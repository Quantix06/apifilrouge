package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.fasterxml.jackson.annotation.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.review.Review;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// optimisation of the queries, it's like tell "go take these while you fetch the events"
@NamedEntityGraph(
        name = "Event.withDetails",
        attributeNodes = {
                @NamedAttributeNode("organizer"),
                @NamedAttributeNode("place"),
                @NamedAttributeNode("categories"),
                @NamedAttributeNode("participants"),
                @NamedAttributeNode("city")
        }
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "event_date")
    private LocalDateTime date;
    private String description;
    private String name;
    private String address;
    @Column(name = "max_customers")
    private Integer maxCustomers;
    @Column(name = "is_trending")
    private Boolean isTrending;
    @Column(name = "is_invitation_only")
    private Boolean isInvitationOnly = false; // par défaut, l'événement n'est pas sur invitation
    @Column(name = "is_first_edition")
    private Boolean isFirstEdition;
    private Double price;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    @Column(columnDefinition = "TEXT", name = "content_html")
    private String contentHtml;
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id",nullable = false)
    @JsonBackReference(value = "place-events")
    @JsonIgnore
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id",nullable=false)
    @JsonBackReference(value = "city-events")
    @JsonIgnore
    private City city;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "invitation-events")
    @JsonIgnore
    private List<Invitation> invitations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-events")
    @JsonIgnore
    private User organizer;

    // On utilise un Set pour les catégories pour éviter l'erreur "MultipleBagFetchException"
    // Hibernate peut charger une List et plusieurs Set en une seule fois, mais plusieurs List non
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_category",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonIgnoreProperties("events")
    private Set<Category> categories = new HashSet<>(); // on replace List par Set car l'ordre des catégories n'a que peu d'importance

    @ManyToMany
    @JoinTable(name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties("participatedEvents")
    private List<User> participants = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "event-orders")
    @JsonIgnore
    private List<Order> orders;

    @OneToMany
    @JoinColumn(name = "review_event_id")
    @JsonManagedReference(value = "event-reviews")
    @JsonIgnore
    private List<Review> reviews;
}
