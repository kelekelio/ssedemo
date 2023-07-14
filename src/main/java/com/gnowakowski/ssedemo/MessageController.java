package com.gnowakowski.ssedemo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Slf4j
public class MessageController {

    private final SseHandler handler;

    @GetMapping("/")
    public String index() {
        return "Greetings from Azure Spring Apps!";
    }

    @PostMapping("/{id}")
    public EventDto sendMessage(@PathVariable Long id, @RequestBody EventDto eventDto) {
        log.info("Sending message to {}", id);
        handler.publish(id, eventDto);
        return eventDto;
    }
}
