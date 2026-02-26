package az.edu.ada.wm2.lab5.service;

import az.edu.ada.wm2.lab5.model.Event;
import az.edu.ada.wm2.lab5.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event createEvent(Event event) {
        if (event.getId() == null) {
            event.setId(UUID.randomUUID());
        }
        return eventRepository.save(event);
    }

    @Override
    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event updateEvent(UUID id, Event event) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        event.setId(id);
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public Event partialUpdateEvent(UUID id, Event partialEvent) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        // Update only non-null fields
        if (partialEvent.getEventName() != null) {
            existingEvent.setEventName(partialEvent.getEventName());
        }
        if (partialEvent.getTags() != null && !partialEvent.getTags().isEmpty()) {
            existingEvent.setTags(partialEvent.getTags());
        }
        if (partialEvent.getTicketPrice() != null) {
            existingEvent.setTicketPrice(partialEvent.getTicketPrice());
        }
        if (partialEvent.getEventDateTime() != null) {
            existingEvent.setEventDateTime(partialEvent.getEventDateTime());
        }
        if (partialEvent.getDurationMinutes() > 0) {
            existingEvent.setDurationMinutes(partialEvent.getDurationMinutes());
        }

        return eventRepository.save(existingEvent);
    }

    // Custom methods
   @Override
    public List<Event> getEventsByTag(String tag) {
    if (tag == null || tag.trim().isEmpty()) {
        throw new IllegalArgumentException("Tag must not be null or empty");
    }

    return eventRepository.findAll()
            .stream()
            .filter(event -> event.getTags() != null &&
                    event.getTags().stream()
                            .anyMatch(t -> t.equalsIgnoreCase(tag)))
            .collect(Collectors.toList());
}

   @Override
    public List<Event> getUpcomingEvents() {
    LocalDateTime now = LocalDateTime.now();

    return eventRepository.findAll()
            .stream()
            .filter(event -> event.getEventDateTime() != null &&
                    event.getEventDateTime().isAfter(now))
            .sorted((e1, e2) -> e1.getEventDateTime().compareTo(e2.getEventDateTime()))
            .collect(Collectors.toList());
}


    @Override
    public List<Event> getEventsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {

    if (minPrice == null || maxPrice == null) {
        throw new IllegalArgumentException("Price range cannot be null");
    }

    if (minPrice.compareTo(maxPrice) > 0) {
        throw new IllegalArgumentException("Min price cannot be greater than max price");
    }

    return eventRepository.findAll()
            .stream()
            .filter(event -> event.getTicketPrice() != null &&
                    event.getTicketPrice().compareTo(minPrice) >= 0 &&
                    event.getTicketPrice().compareTo(maxPrice) <= 0)
            .collect(Collectors.toList());
}

    @Override
    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {

    if (start == null || end == null) {
        throw new IllegalArgumentException("Date range cannot be null");
    }

    if (start.isAfter(end)) {
        throw new IllegalArgumentException("Start date cannot be after end date");
    }

    return eventRepository.findAll()
            .stream()
            .filter(event -> event.getEventDateTime() != null &&
                    !event.getEventDateTime().isBefore(start) &&
                    !event.getEventDateTime().isAfter(end))
            .collect(Collectors.toList());
}


    @Override
    public Event updateEventPrice(UUID id, BigDecimal newPrice) {
        return null;
    }

}