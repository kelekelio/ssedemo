package com.gnowakowski.ssedemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
@Slf4j
public class SseController {

    private final SseHandler handler;

    @GetMapping(path = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> subscribe(@PathVariable Long id) {
        log.info("subscribing to {}", id);
        UUID uuid = UUID.randomUUID();
        return Flux.merge(createEventStream(id, uuid), createPingStream());
    }

    private Flux<ServerSentEvent<Object>> createEventStream(Long id, UUID uuid) {
        return Flux.create(sink -> {
            handler.subscribe(id, uuid, sink::next);
            sink.onCancel(() -> {
               log.info("Removing sink from event stream");
               handler.remove(id, uuid);
            });
        }).map(event -> {
            var eventDto = (EventDto) event;
            return ServerSentEvent.builder()
                    .event(eventDto.type().name())
                    .data(eventDto.body())
                    .build();
        }).doFinally(signalType -> log.info("exit message: {}", signalType));
    }

    private Flux<ServerSentEvent<Object>> createPingStream() {
        return Flux.interval(Duration.ofSeconds(10))
                .map(i -> ServerSentEvent.builder()
                        .event(EventType.PING.name())
                        .comment("ping")
                        .build());
    }

}
