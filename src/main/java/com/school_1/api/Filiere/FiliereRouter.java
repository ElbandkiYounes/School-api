package com.school_1.api.Filiere;

import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Commons.Security.Secured;
import com.school_1.api.Filiere.models.CreateFilierePayload;
import com.school_1.api.Filiere.models.UpdateFilierePayload;
import com.school_1.api.Filiere.models.Filiere;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/filieres")
@Stateless
public class FiliereRouter {

    @Inject
    private FiliereService filiereService;

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public List<Filiere> getAllFilieres() {
        return filiereService.getAllFilieres();
    }

    @GET
    @Path("/{id}")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFiliereById(@PathParam("id") Long id) throws NotFoundException {
        Filiere filiere = filiereService.getFiliereById(id);
        return Response.ok(filiere).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response addFiliere(@Valid CreateFilierePayload payload, @HeaderParam("Email") String email) throws UnauthorizedException {
        Filiere filiere = filiereService.addFiliere(payload, email);
        return Response.status(Response.Status.CREATED).entity(filiere).build();
    }

    @PUT
    @Path("/{id}")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFiliere(@PathParam("id") Long id, @Valid UpdateFilierePayload payload, @HeaderParam("Email") String email) throws UnauthorizedException, NotFoundException {
        Filiere filiere = filiereService.updateFiliere(id, payload, email);
        return Response.ok(filiere).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteFiliere(@PathParam("id") Long id, @HeaderParam("Email") String email) throws UnauthorizedException, NotFoundException {
        filiereService.deleteFiliere(id, email);
        return Response.noContent().build();
    }
}