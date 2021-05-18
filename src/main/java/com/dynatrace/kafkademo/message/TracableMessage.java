package com.dynatrace.kafkademo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.opentelemetry.api.trace.Span;

import java.io.Serializable;


public class TracableMessage implements Serializable {
    @JsonProperty("span")
    Span span;
    @JsonProperty("message")
    String message;

    public TracableMessage(Span span, String message) {
        this.span = span;
        this.message = message;
    }

    public Span getSpan() {
        return span;
    }

    public void setSpan(Span span) {
        this.span = span;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
