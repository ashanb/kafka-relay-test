package com.relay.test;

import com.relay.test.client.operations.ClientOperationsUtil;
import com.relay.test.client.operations.OperationType;
import org.eclipse.microprofile.jwt.Claim;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("/sensors")
public class ClientResource {
    @Inject
    // tag::claim[]
    @Claim("groups")
    // end::claim[]
    // tag::rolesArray[]
    private JsonArray roles;
    // end::rolesArray[]

    @GET
    // tag::rolesEndpoint[]
    @Path("/jwtroles")
    // end::rolesEndpoint[]
    @Produces(MediaType.APPLICATION_JSON)
    // tag::rolesAllowedAdminUser2[]
    @RolesAllowed({ "admin", "user" })
    // end::rolesAllowedAdminUser2[]
    public String getRoles() {
        return roles.toString();
    }

    @GET
    @Path("/min")
    @Produces(MediaType.APPLICATION_JSON)
    // tag::rolesAllowedAdminUser1[]
    @RolesAllowed({ "admin", "user" })
    // end::rolesAllowedAdminUser1[]
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
    // tag::rolesAllowedAdminUser1[]
    @RolesAllowed({ "admin", "user" })
    // end::rolesAllowedAdminUser1[]
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
    // tag::rolesAllowedAdminUser1[]
    @RolesAllowed({ "admin", "user" })
    // end::rolesAllowedAdminUser1[]
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
    // tag::rolesAllowedAdminUser1[]
    @RolesAllowed({ "admin", "user" })
    // end::rolesAllowedAdminUser1[]
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
