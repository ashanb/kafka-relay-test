package com.relay.test;

import com.relay.test.client.operations.ClientOperationsUtil;
import com.relay.test.client.operations.OperationType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/sensors")
public class ClientResource {
    @GET
    @Path("/min")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMin(
            @QueryParam("type") final String type,
            @QueryParam("cluster") final Long cluster,
            @QueryParam("from") final String from,
            @QueryParam("to") final String to) {
        // todo: application context

        String result = ClientOperationsUtil.getAggregate(type, cluster, from, to, OperationType.MIN).toString();
        System.out.println(result);
        return "Result: " + result;
    }

    @GET
    @Path("/max")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMax(
            @QueryParam("type") final String type,
            @QueryParam("cluster") final Long cluster,
            @QueryParam("from") final String from,
            @QueryParam("to") final String to) {
        // todo: application context
        String result = ClientOperationsUtil.getAggregate(type, cluster, from, to, OperationType.MAX).toString();
        System.out.println(result);
        return "Result: " + result;
    }


    @GET
    @Path("/avg")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAvg(
            @QueryParam("type") final String type,
            @QueryParam("cluster") final Long cluster,
            @QueryParam("from") final String from,
            @QueryParam("to") final String to) {
        // todo: application context
        String result = ClientOperationsUtil.getAggregate(type, cluster, from, to, OperationType.AVG).toString();
        System.out.println(result);
        return "Result: " + result;
    }

    @GET
    @Path("/median")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMedian(
            @QueryParam("type") final String type,
            @QueryParam("cluster") final Long cluster,
            @QueryParam("from") final String from,
            @QueryParam("to") final String to) {
        // todo: application context
        String result = ClientOperationsUtil.getAggregate(type, cluster, from, to, OperationType.MEDIAN).toString();
        System.out.println(result);
        return "Result: " + result;
    }

}
