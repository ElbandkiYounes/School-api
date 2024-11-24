package com.school_1.api.User;

import com.school_1.api.Commons.Exceptions.AccessDeniedException;
import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
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

@Path("users")
@Stateless
public class UserRouter {
    @Inject
    private UserService userService;

    @POST
    @Path("signup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signUp(@Valid SignupPayload userPayload) throws DuplicationException {
        User user = userService.signUp(userPayload);
        return Response.ok(user).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginPayload loginPayload) throws UnauthorizedException, NotFoundException {
        User user = userService.login(loginPayload);
        return Response.ok(user).build();
    }

    @GET
    @Path("verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyEmail(@QueryParam("token") String token) throws UnauthorizedException {
        userService.verifyEmail(token);
        return Response.ok("Email verified successfully").build();

    }

    @PUT
    @Path("/{id}")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id,@Valid UpdateUserPayload updateUserPayload, @HeaderParam("Authorization") String token) throws NotFoundException, AccessDeniedException {
        User updatedUser = userService.updateUser(id, updateUserPayload, token);
        return Response.ok(updatedUser).build();

    }
}
