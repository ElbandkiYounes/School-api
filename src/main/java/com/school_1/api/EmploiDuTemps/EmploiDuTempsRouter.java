package com.school_1.api.EmploiDuTemps;

import com.school_1.api.Commons.Exceptions.BadRequestException;
import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Commons.Security.Secured;
import com.school_1.api.EmploiDuTemps.models.CreateEmploiDuTempsPayload;
import com.school_1.api.EmploiDuTemps.models.EmploiDuTemps;
import com.school_1.api.EmploiDuTemps.models.UpdateEmploiDuTempsPayload;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/emplois_du_temps")
@Stateless
public class EmploiDuTempsRouter {

    @Inject
    private EmploiDuTempsService emploiDuTempsService;

    @GET
    @Path("/filiere/{filiereId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<EmploiDuTemps> getAllEmploisDuTemps(@PathParam("filiereId") Long filiereId) throws NotFoundException {
        return emploiDuTempsService.getEmploisDuTempsByFiliereId(filiereId);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response getEmploiDuTempsById(@PathParam("id") Long id) throws NotFoundException {
        EmploiDuTemps emploiDuTemps = emploiDuTempsService.getEmploiDuTempsById(id);
        return Response.ok(emploiDuTemps).build();
    }

    @GET
    @Path("/professeur/{professeurId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response getEmploisDuTempsByProfesseurId(@PathParam("professeurId") Long professeurId) throws NotFoundException {
        List<EmploiDuTemps> emploisDuTemps = emploiDuTempsService.getEmploisDuTempsByProfesseurId(professeurId);
        return Response.ok(emploisDuTemps).build();
    }

    @POST
    @Path("/filiere/{filiereId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response addEmploiDuTemps(@Valid CreateEmploiDuTempsPayload payload, @HeaderParam("email") String email, @PathParam("filiereId") Long filiereId) throws UnauthorizedException, NotFoundException, DuplicationException, BadRequestException {
        EmploiDuTemps emploiDuTemps = emploiDuTempsService.addEmploiDuTemps(payload, email, filiereId);
        return Response.status(Response.Status.CREATED).entity(emploiDuTemps).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response updateEmploiDuTemps(@PathParam("id") Long id, @Valid UpdateEmploiDuTempsPayload payload, @HeaderParam("email") String email) throws UnauthorizedException, NotFoundException, DuplicationException, BadRequestException {
        EmploiDuTemps emploiDuTemps = emploiDuTempsService.updateEmploiDuTemps(id, payload, email);
        return Response.ok(emploiDuTemps).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteEmploiDuTemps(@PathParam("id") Long id, @HeaderParam("email") String email) throws UnauthorizedException, NotFoundException {
        emploiDuTempsService.deleteEmploiDuTemps(id, email);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/filiere/{filiereId}")
    @Secured
    public Response deleteEmploiDuTempsByFiliereId(@PathParam("filiereId") Long filiereId, @HeaderParam("email") String email) throws UnauthorizedException, NotFoundException {
        emploiDuTempsService.deleteEmploiDuTempsByFiliereId(filiereId, email);
        return Response.noContent().build();
    }
}