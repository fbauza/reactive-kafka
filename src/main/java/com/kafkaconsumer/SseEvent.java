package com.kafkaconsumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("/fuego")
public class SseEvent {
	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void toString(@Context SseEventSink eventSink, @Context Sse sse) {
		try(SseEventSink sink = eventSink) {
			sink.send(sse.newEvent("data"));
			sink.send(sse.newEvent("La mierda del evento", "mierda totoa"));

			OutboundSseEvent event = sse.newEventBuilder()
					.id("1")
					.name("Evento")
					.data("Data")
					.reconnectDelay(3000)
					.comment("hijo de puta")
					.build();

			sink.send(event);
		}
	}
}
