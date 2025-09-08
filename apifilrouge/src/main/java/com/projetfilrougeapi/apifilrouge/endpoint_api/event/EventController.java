package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Retourne une liste d'évènements avec des filtres optionnels. Si aucun paramètre n'est fourni, renvoie tous les événements sans filtre.
    @GetMapping
    public PagedModel<EntityModel<EventSummaryResponse>> getAllEvents(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @RequestParam(defaultValue = "true") boolean onlyAvailable,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String[] categories,
            @RequestParam(required = false) String[] cities,
            @RequestParam(required = false) String[] places


    ) {
        return eventService.getAllEvents(pageable, minPrice, maxPrice, startDate, endDate, categories, cities, places, onlyAvailable);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping("/{id}")
    public EntityModel<EventResponse> getEventById(@PathVariable("id") Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/{id}/place")
    public EntityModel<PlaceResponse> getPlaceForEvent(@PathVariable("id") Long id) {
        return eventService.getPlaceForEvent(id);
    }

    @GetMapping("/{id}/city")
    public EntityModel<CityResponse> getCityForEvent(@PathVariable("id") Long id) {
        return eventService.getCityForEvent(id);
    }

    @GetMapping("/{id}/invitations")
    public CollectionModel<EntityModel<Invitation>> getInvitationsForEvent(@PathVariable Long id) {
        return eventService.getInvitationsForEvent(id);
    }

    @GetMapping("/{id}/organizer")
    public EntityModel<UserResponse> getOrganizerForEvent(@PathVariable Long id) {
        return eventService.getOrganizerForEvent(id);
    }

    @GetMapping("/{eventId}/categories")
    public CollectionModel<Category> getCategoriesForEvent(@PathVariable Long eventId) {
        return eventService.getCategoriesForEvent(eventId);
    }

    @GetMapping("/{eventId}/participants")
    public CollectionModel<EntityModel<UserSummary>> getParticipantsForEvent(@PathVariable Long eventId) {
        return eventService.getParticipantsForEvent(eventId);
    }


    /**
     * It accepts a 'limit' parameter to define the number of results.
     * @param city The name of the city to filter the results.
     * @param place the name of the place to filter the results.
     * @param limit The number of events to display (default is 10).
     * @return A collection of events.
     */
    @GetMapping("/first-editions")
    public CollectionModel<EntityModel<EventSummaryResponse>> getFirstEditionEvents(
            @RequestParam(defaultValue = "true") boolean onlyAvailable,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String place,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return eventService.getFirstEditionEvents(city,place, limit, onlyAvailable);
    }
    /**
     * GET endpoint to retrieve a limited list of trending events.
     * <p>
     * Send back a collection of event summary DTOs with HATEOAS navigation links.
     * </p>
     * @param eventId The ID of the event to retrieve reviews for.
     * @return A CollectionModel containing EntityModels of EventSummaryResponse.
     */
    @GetMapping("/{eventId}/reviews")
    public CollectionModel<EntityModel<EventSummaryResponse>> getreviews(@PathVariable Long eventId) {
        return eventService.getReviews(eventId);
    }

    /**
     * GET endpoint to retrieve a limited list of trending events.
     * <p>
     * Accepts a 'limit' query parameter to specify the maximum number of events to return.
     * Returns a collection of event summary DTOs with HATEOAS navigation links.
     * </p>
     *
     * @param limit The maximum number of trending events to return (default: 5).
     * @return A CollectionModel containing EntityModels of EventSummaryResponse.
     */
    @GetMapping("/trending")
    public CollectionModel<EntityModel<EventSummaryResponse>> getTrendingEvents(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "true") boolean onlyAvailable
    ) {
        return eventService.getTrendingEvents(limit, onlyAvailable);
    }


    @PatchMapping("/{id}")
    public EntityModel<EventResponse> patchEvent(@PathVariable("id") Long id, @Valid @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request);
    }
    // Adds multiple participants to an event.
    @PostMapping("/{id}/participants")
    public EntityModel<EventSummaryResponse> addParticipants(@PathVariable("id") Long eventId, @Valid @RequestBody ParticipantListRequest request) {
        return eventService.addParticipantsToEvent(eventId, request.getUserIds());
    }

    @DeleteMapping("/{id}/participants")
    public EntityModel<EventSummaryResponse> removeParticipants(@PathVariable("id") Long eventId, @Valid @RequestBody ParticipantListRequest request) {
        return eventService.removeParticipantsFromEvent(eventId, request.getUserIds());
    }

    // add 1 participant to an event.
    @PostMapping("/{id}/participant")
    public EntityModel<EventSummaryResponse> addParticipant(@PathVariable("id") Long eventId, @Valid @RequestBody ParticipantRequest request) {
        return eventService.addParticipantToEvent(eventId, request.getUserId());
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<EventResponse> cancelEvent(@PathVariable("id") Long id) {
        return eventService.cancelEvent(id);
    }
}