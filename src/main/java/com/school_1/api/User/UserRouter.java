package com.school_1.api.User;

import com.school_1.api.Commons.Security.Secured;
import com.school_1.api.User.models.LoginPayload;
import com.school_1.api.User.models.SignupPayload;
import com.school_1.api.User.models.UpdateUserPayload;
import com.school_1.api.User.models.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("users")
@Stateless
public class UserRouter {
    @Inject
    private UserService userService;

    @POST
    @Path("signup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signUp(@Valid SignupPayload userPayload) {
        try {
            User user = userService.SignUp(userPayload);
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginPayload loginPayload) {
        try {
            User user = userService.login(loginPayload.getEmail(), loginPayload.getPassword());
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyEmail(@QueryParam("token") String token) {
        try {
            userService.verifyEmail(token);
            return Response.ok("Email verified successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, UpdateUserPayload updateUserPayload) {
        try {
            User updatedUser = userService.updateUser(id, updateUserPayload);
            return Response.ok(updatedUser).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
