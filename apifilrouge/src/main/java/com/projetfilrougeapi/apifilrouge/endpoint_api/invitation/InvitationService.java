package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.DTO.EventResponse;
import com.projetfilrougeapi.apifilrouge.DTO.InvitationRequest;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.email.EmailSender;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.OrderController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.TicketController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class InvitationService {

    private final EmailSender emailSender;
    private final InvitationRepository invitationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CreateOrderForInvitationService createOrderForInvitationService;

    public InvitationService(InvitationRepository invitationRepository, EventRepository eventRepository, UserRepository userRepository, CreateOrderForInvitationService createOrderForInvitationService, EmailSender emailSender) {
        this.invitationRepository = invitationRepository;
        this.emailSender = emailSender;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.createOrderForInvitationService = createOrderForInvitationService;
    }

    public CollectionModel<EntityModel<Invitation>> getAllInvitations() {
        List<EntityModel<Invitation>> invitations = invitationRepository.findAll().stream()
                .map(invitation -> EntityModel.of(invitation,
                        linkTo(methodOn(InvitationController.class).getInvitationById(invitation.getId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getEventById(invitation.getEvent().getId())).withRel("event")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(invitations,
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents(null, true, null, null, null, null, null, null, null)).withRel("events"));
    }

    /**
     * Retrieves a specific invitation by the combination of event and user.
     *
     * @param eventId The ID of the event.
     * @param userId  The ID of the user.
     * @return An EntityModel wrapping the Invitation entity.
     * @throws ResponseStatusException if no invitation is found for the given event and user.
     */
    public EntityModel<Invitation> getInvitationByEventAndUser(Long eventId, Long userId) {
        Invitation invitation = invitationRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No invitation found for this user at this event."));

        return EntityModel.of(invitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(invitation.getId())).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(InvitationController.class).getEventForInvitation(invitation.getId())).withRel("event"),
                linkTo(methodOn(InvitationController.class).getUserForInvitation(invitation.getId())).withRel("user"));
    }

    /**
     * Retrieves a specific invitation by id.
     *
     * @param id The ID of the invitation.
     * @return An EntityModel wrapping the Invitation entity.
     * @throws ResponseStatusException if no invitation is found for the given event and user.
     */
    public EntityModel<Invitation> getInvitationById(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(invitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(id)).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(InvitationController.class).getEventForInvitation(id)).withRel("event"),
                linkTo(methodOn(InvitationController.class).getUserForInvitation(id)).withRel("user"));
    }

    /**
     * Retrieves the event associated with a specific invitation.
     *
     * @param invitationId the ID of the invitation
     * @return an EntityModel wrapping the Event entity linked to the invitation
     * @throws ResponseStatusException if the invitation is not found
     */
    public EntityModel<EventResponse> getEventForInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

        Event event = invitation.getEvent();
        EventResponse eventResponse = EventResponse.fromEntity(event);

        return EntityModel.of(eventResponse,
                linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents(null, true, null, null, null, null, null, null, null)).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(event.getId())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(event.getId())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getCityForEvent(event.getId())).withRel("city"),
                linkTo(methodOn(EventController.class).getOrganizerForEvent(event.getId())).withRel("organizer"),
                linkTo(methodOn(EventController.class).getCategoriesForEvent(event.getId())).withRel("categories"),
                linkTo(methodOn(EventController.class).getParticipantsForEvent(event.getId())).withRel("participants"));
    }

    /**
     * Retrieves the user associated with a specific invitation.
     *
     * @param invitationId the ID of the invitation
     * @return an EntityModel wrapping the User entity linked to the invitation
     * @throws ResponseStatusException if the invitation is not found
     */
    public EntityModel<UserResponse> getUserForInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

        User user = invitation.getUser();
        UserResponse userResponse = UserResponse.fromEntity(user);

        return EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).getEventsForUser(user.getId(), null)).withRel("events"),
                linkTo(methodOn(UserController.class).getCategoriesForUser(user.getId())).withRel("categories"),
                linkTo(methodOn(UserController.class).getOrderByUser(user.getId())).withRel("orders"),
                linkTo(methodOn(UserController.class).getInvitationsForUser(user.getId())).withRel("invitations"));
    }


    // création d'une invitation
    public EntityModel<Invitation> addInvitation(InvitationRequest invitation) throws Exception {
        if (invitationRepository.existsByEventIdAndUserId(invitation.getEventId(), invitation.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An invitation for this user at this event already exists.");
        }
        Event event = eventRepository.findById(invitation.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        User user = userRepository.findById(invitation.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        User receiver = userRepository.findById(event.getOrganizer().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        Invitation newInvitation = Invitation.builder()
                .event(event)
                .user(user)
                .status(Status.SENT)
                .description(invitation.getDescription())
                .organizerId(event.getOrganizer().getId())
                .build();

        Invitation savedInvitation = invitationRepository.save(newInvitation);

        emailSender.sendInvitationEmail(user, receiver, event);

        return EntityModel.of(savedInvitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(savedInvitation.getId())).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(InvitationController.class).getEventForInvitation(savedInvitation.getId())).withRel("event"),
                linkTo(methodOn(InvitationController.class).getUserForInvitation(savedInvitation.getId())).withRel("user"));
    }

    public EntityModel<Invitation> updateInvitation(Long id, InvitationRequest invitation) {
        // Récupération de l'invitation existante
        Invitation existingInvitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation non trouvée"));

        // Mise à jour de l'événement si spécifié
        Event event = existingInvitation.getEvent();
        if (invitation.getEventId() != null) {
            event = eventRepository.findById(invitation.getEventId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Événement non trouvé"));
            existingInvitation.setEvent(event);
        }

        // Mise à jour de la description si spécifiée
        if (invitation.getDescription() != null) {
            existingInvitation.setDescription(invitation.getDescription());
        }

        // Mise à jour du statut et gestion de l'ajout du participant
        if (invitation.getStatus() != null) {
            existingInvitation.setStatus(invitation.getStatus());

            // Si le statut est "ACCEPTED", on ajoute l'utilisateur comme participant
            if ("ACCEPTED".equals(invitation.getStatus().toString())) {
                User userToAdd = existingInvitation.getUser();

                // Vérification si l'événement a atteint sa capacité maximale
                if (event.getMaxCustomers() != null) {
                    long currentParticipantsCount = event.getParticipants().stream()
                            .filter(user -> !user.getId().equals(userToAdd.getId()))
                            .count();

                    if (currentParticipantsCount >= event.getMaxCustomers()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre maximum de participants atteint");
                    }
                }
                event.getParticipants().add(userToAdd);
                eventRepository.save(event);
            }
        }

        // Sauvegarde des modifications
        Invitation updatedInvitation = invitationRepository.save(existingInvitation);
        if (updatedInvitation.getStatus() == Status.REJECTED) {
            return EntityModel.of(updatedInvitation,
                    linkTo(methodOn(InvitationController.class).getInvitationById(updatedInvitation.getId())).withSelfRel(),
                    linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                    linkTo(methodOn(InvitationController.class).getEventForInvitation(updatedInvitation.getId())).withRel("event"),
                    linkTo(methodOn(InvitationController.class).getUserForInvitation(updatedInvitation.getId())).withRel("user")
            );
        } else if (updatedInvitation.getStatus() == Status.ACCEPTED) {
            Long orderId = createOrderForInvitationService.createOrderForInvitation(updatedInvitation);
            Long ticketId = createOrderForInvitationService.createTicket(orderId);
            return EntityModel.of(updatedInvitation,
                    linkTo(methodOn(InvitationController.class).getInvitationById(updatedInvitation.getId())).withSelfRel(),
                    linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                    linkTo(methodOn(InvitationController.class).getEventForInvitation(updatedInvitation.getId())).withRel("event"),
                    linkTo(methodOn(InvitationController.class).getUserForInvitation(updatedInvitation.getId())).withRel("user"),
                    linkTo(methodOn(OrderController.class).getOrderById(orderId)).withRel("order-created"),
                    linkTo(methodOn(TicketController.class).getTicketById(ticketId)).withRel("ticket-created")
            );
        } else {
            // Cas par défaut (PENDING)
            return EntityModel.of(updatedInvitation,
                    linkTo(methodOn(InvitationController.class).getInvitationById(updatedInvitation.getId())).withSelfRel(),
                    linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                    linkTo(methodOn(InvitationController.class).getEventForInvitation(updatedInvitation.getId())).withRel("event"),
                    linkTo(methodOn(InvitationController.class).getUserForInvitation(updatedInvitation.getId())).withRel("user")
            );
        }
    }

    // Suppression d'une invitation
    public void deleteInvitation(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        invitationRepository.delete(invitation);
    }
}