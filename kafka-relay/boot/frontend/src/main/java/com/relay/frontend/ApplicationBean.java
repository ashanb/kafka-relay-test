package com.relay.frontend;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.relay.frontend.client.RelayClient;
import com.relay.frontend.util.SessionUtils;


@ApplicationScoped
@Named
public class ApplicationBean {
    @Inject
    @RestClient
    private RelayClient defaultRestClient;

    public String getJwt() {
        String jwtTokenString = SessionUtils.getJwtToken();
        String authHeader = "Bearer " + jwtTokenString;
        return authHeader;
    }

    public String getJwtRoles() {
        String authHeader = getJwt();
        return defaultRestClient.getJwtRoles(authHeader);
    }

}
