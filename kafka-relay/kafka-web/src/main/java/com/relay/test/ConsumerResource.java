package com.relay.test;

import com.relay.test.external.kafka.KafkaConsumerController;

import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.relay.test.external.kafka.KafkaConsumerController.*;

@Path("consumer")
public class ConsumerResource {
    @GET
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startConsumer() {
        // todo: application context
        try {
            setupConsumer();
            final Thread newThread = new Thread(KafkaConsumerController::startConsume);
            newThread.start();
            return Response.ok().entity(Json.createObjectBuilder()
                    .add("Status", "Signal Sent!").build().toString()).build();
        } catch (Exception exception) {
            return Response.serverError().entity(Json.createObjectBuilder()
                    .add("Status", "Error").add("Details", exception.getMessage()).toString()).build();
        }
    }

    @GET
    @Path("/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopConsumer() {
        try {
            return Response.ok().entity(Json.createObjectBuilder()
                    .add("Status", KafkaConsumerController.stop()).build().toString()).build();
        } catch (Exception exception) {
            return Response.serverError().entity(Json.createObjectBuilder()
                    .add("Status", "Error").add("Details", exception.getMessage()).toString()).build();
        }
    }
}

