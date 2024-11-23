package com.school_1.api.Commons.Security;

import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
@Secured
public class TokenFilter implements ContainerRequestFilter {

    @Inject
    private UserEJB userEJB;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        if (method != null && method.isAnnotationPresent(Secured.class)) {
            String token = requestContext.getHeaderString("Authorization");
            String email = requestContext.getHeaderString("Email");

            if (token == null || email == null || !isValidToken(email, token)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Authorization is needed for this operation ")
                        .build());
            }
        }
    }

    private boolean isValidToken(String email, String token) {
        User user = userEJB.findUserByEmail(email);
        return user != null && token.equals(user.getVerificationToken());
    }
}