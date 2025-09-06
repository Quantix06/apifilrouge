package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.email.EmailSender;
import org.springframework.stereotype.Service;

@Service
public class EventEmailUpdateManager {
    private final EmailSender emailSender;
    private final EventRepository eventRepository;

    public EventEmailUpdateManager( EventRepository eventRepository, EmailSender emailSender) {
        this.eventRepository = eventRepository;
        this.emailSender = emailSender;
    }

    public void sendMultipleMailToParticipants(Event event) {
        event.getParticipants().forEach(participant -> {
            try {
                emailSender.sendIUpdateEventEmail(participant, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
