//tag::copyright[]
/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
// end::copyright[]
package com.relay.test;

import com.relay.test.util.JwtBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientEndpointIT {

    static String authHeaderAdmin;
    static String authHeaderUser;
    static String urlOS;
    static String urlUsername;
    static String urlRoles;

    @BeforeAll
    private static void setup() throws Exception{
        String urlBase = "http://" + "localhost"
                 + ":" + System.getProperty("http.port")
                 + "/relay/client";
        urlRoles = urlBase + "/jwtroles";

        authHeaderAdmin = "Bearer " + new JwtBuilder().createAdminJwt("testUser");
        authHeaderUser = "Bearer " + new JwtBuilder().createUserJwt("testUser");
    }

    @Test
    // tag::roles[]
    public void testRolesEndpoint() {
        // tag::adminRequest3[]
        Response response = makeRequest(urlRoles, authHeaderAdmin);
        // end::adminRequest3[]
        assertEquals(200, response.getStatus(),
                "Incorrect response code from " + urlRoles);
        assertEquals("[\"admin\",\"user\"]", response.readEntity(String.class),
                "Incorrect groups claim in token " + urlRoles);

        // tag::userRequest3[]
        response = makeRequest(urlRoles, authHeaderUser);
        // end::userRequest3[]
        assertEquals(200, response.getStatus(), 
                "Incorrect response code from " + urlRoles);
        assertEquals("[\"user\"]", response.readEntity(String.class),
                "Incorrect groups claim in token " + urlRoles);

        // tag::nojwtRequest3[]
        response = makeRequest(urlRoles, null);
        // end::nojwtRequest3[]
        assertEquals(401, response.getStatus(),
                "Incorrect response code from " + urlRoles);

        response.close();
    }
    // end::roles[]

    private Response makeRequest(String url, String authHeader) {
        Client client = ClientBuilder.newClient();
        Builder builder = client.target(url).request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        if (authHeader != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authHeader);
        }
        Response response = builder.get();
        return response;
    }

}
