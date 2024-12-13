package com.school_1.api.Liberation;

import com.school_1.api.Commons.Exceptions.*;
import com.school_1.api.Commons.Exceptions.BadRequestException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Security.Secured;
import com.school_1.api.Liberation.models.CreateLiberationPayload;
import com.school_1.api.Liberation.models.Liberation;
import com.school_1.api.Reservation.models.Week;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/liberations")
@Stateless
public class LiberationRouter {

    @Inject
    private LiberationService liberationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Liberation> getLiberations() {
        return liberationService.getAllLiberations();
    }

    @GET
    @Path("/professeur/{professeurId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Liberation> getLiberationsByProfesseurId(@PathParam("professeurId") Long professeurId, @HeaderParam("email") String email) throws NotFoundException {
        return liberationService.getAllLiberationsByProfesseurId(email, professeurId);
    }

    @GET
    @Path("week")
    @Produces(MediaType.TEXT_PLAIN)
    @Secured
    public Response getCurrentWeek() throws BadRequestException {
        return Response.ok(liberationService.getCurrentWeek()).build();
    }

    @GET
    @Path("/week/{week}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Liberation> getLiberationsByWeek(@PathParam("week") Week week) {
        return liberationService.getAllLiberationByWeek(week);
    }

    @GET
    @Path("day")
    @Produces(MediaType.TEXT_PLAIN)
    @Secured
    public Response getCurrentDay() {
        return Response.ok(liberationService.getCurrentDay()).build();
    }

    @POST
    @Path("/emploi-du-temps/{emploiDuTempsId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response addLiberation(@Valid CreateLiberationPayload payload, @HeaderParam("email") String email, @PathParam("emploiDuTempsId") Long emploiDuTempsId) throws UnauthorizedException, BadRequestException, NotFoundException, NotFoundException {
        Liberation newLiberation = liberationService.addLiberation(payload, email, emploiDuTempsId);
        return Response.status(Response.Status.CREATED).entity(newLiberation).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteLiberation(@PathParam("id") Long id, @HeaderParam("email") String email) throws NotFoundException, UnauthorizedException {
        liberationService.deleteLiberation(id, email);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}