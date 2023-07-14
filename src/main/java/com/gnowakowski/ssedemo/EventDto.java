package com.gnowakowski.ssedemo;

public record EventDto(
        EventType type,
        Object body
) {
}
