package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportManagerService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ReportManagerService(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public Boolean hasBeenBanned(User user) {
        if (user.getRole()==Role.Banned) {
            user.setBanned(true);
            userRepository.save(user);
            List<Event> eventList = eventRepository.findByOrganizerId(user.getId())
                    .orElseThrow();

            if (!eventList.isEmpty()) {
                EventStatus eventStatus = EventStatus.BLOCKED;
                eventList.forEach(event -> event.setStatus(eventStatus));
            }

            return true;
        }else {
            return false;
        }
    }
}