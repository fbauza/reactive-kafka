package com.kafkaconsumer;


import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.concurrent.CompletionStage;




@Path("/consume")
public class KafkaConsumer {

    private OutboundSseEvent.Builder eventBuilder;
    private Sse sse;
    private SseEventSink sseEventSink = null;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void setSse(Sse sse) {
        this.sse = sse;
        this.eventBuilder = sse.newEventBuilder();
    }
    /*
    @GET
    @Path("/hola")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        logger.info("Pasando por la pavada");
        return "Hello from RESTEasy Reactive ";
    }


    @GET
    @Path("/events")
    @Produces(MediaType.TEXT_PLAIN)
    public String eventos() {

        logger.info("Pasando por eventos");
        return "event";
    }


    @Incoming("events")
    public String  consume(String event) {
        System.out.println("Receive: " + event);
        return event;
    }
    */
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void consume(@Context Sse sse, SseEventSink sseEventSink) {
        this.sseEventSink = sseEventSink;
        logger.info("sseEventSink: " + sseEventSink);
    }

    @Incoming("events")
    public CompletionStage onMessage(Message<String> message) throws IOException {
        if (sseEventSink != null) {
            logger.info("Message content: " + message.getPayload());
            String display = LocalTime.now().toString() + " | "  + getHost() + " | " + message.getPayload();

            final OutboundSseEvent sseEvent = this.eventBuilder
                    .id("1")
                    .name("message")
                    .mediaType(MediaType.TEXT_PLAIN_TYPE)
                    .data(Message.class, message)
                    .reconnectDelay(3000)
                    .comment("comment")
                    .build();

            sseEventSink.send(sseEvent);
        }

        return message.ack();
    }
    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.error("Problem while trying to get the hostname: " + ex);
            return "Unknown";
        }
    }


}

