
package com.relay.test;

import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsumerEndpointIT {
    @Test
    public void testKafkaStartConsumer() {
        String port = System.getProperty("http.port");
        String context = System.getProperty("context.root");
        String url = "http://localhost:" + port + "/" + context + "/";

        Client client = ClientBuilder.newClient();

        WebTarget target = client.target(url + "kafka/consumer/start");
        Response response = target.request().post(null);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(),
                "Incorrect response code from " + url);
        String json = response.readEntity(String.class);
        assertEquals(json, "{\"Status\":\"Signal Sent!\"}",
                "Wrong Return");
        response.close();
    }

    @Test
    public void testKafkaStopConsumer() {
        String port = System.getProperty("http.port");
        String context = System.getProperty("context.root");
        String url = "http://localhost:" + port + "/" + context + "/";

        Client client = ClientBuilder.newClient();

        WebTarget target = client.target(url + "kafka/consumer/stop");
        Response response = target.request().post(null);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(),
                "Incorrect response code from " + url);
        String json = response.readEntity(String.class);
//        assertEquals(json, "{\"Status\":\"Consumer Stopped\"}",
//                "Wrong Return");
        response.close();
    }
}
