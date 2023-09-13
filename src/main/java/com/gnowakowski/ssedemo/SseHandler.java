package com.gnowakowski.ssedemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
@Slf4j
public class SseHandler {

    private final ConcurrentHashMap<Long, ConcurrentHashMap<UUID, Consumer<EventDto>>> sinks = new ConcurrentHashMap<>();

    public void subscribe(Long id, UUID uuid, Consumer<EventDto> listener) {
        log.info("creating sink for  {}", uuid);
        sinks.computeIfAbsent(id, k -> new ConcurrentHashMap<>()).put(uuid, listener);
        log.info("client added to {}", id);
        listener.accept(new EventDto(EventType.WELCOME, null));
    }

    public void publish(Long id, EventDto eventDto) {
        log.info("Processing event: {}", eventDto.type());
        Optional.ofNullable(sinks.get(id))
                .ifPresent(clients -> clients.values()
                        .forEach(i -> i.accept(eventDto)));
    }

    public void remove(Long id, UUID uuid) {
        log.info("Removing sink {}", uuid);
        Optional.ofNullable(sinks.get(id))
                .ifPresent(clients -> {
                    clients.remove(uuid);
                    if (clients.isEmpty()) {
                        log.info("channel is empty {}", id);
                    }
                });
        publish(id, new EventDto(EventType.MESSAGE, uuid + " was removed from the list of clients"));
        log.info("number of channels: {} | number of clients: {}", sinks.size(), sinks.get(id).values().size());
    }

}
