package com.school_1.api.Salle;

import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Commons.Security.Secured;
import com.school_1.api.Reservation.models.Week;
import com.school_1.api.Salle.models.CreateSallePayload;
import com.school_1.api.Salle.models.GetPayload;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.Salle.models.UpdateSallePayload;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/salles")
@Stateless
public class SalleRouter {

    @Inject
    private SalleService salleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Salle> getAllSalles() {
        return salleService.getAllSalles();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response getSalleById(@PathParam("id") Long id) throws NotFoundException {
        Salle salle = salleService.getSalleById(id);
        return Response.ok(salle).build();
    }

    @GET
    @Path("/free-day-week-seance")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Salle> getFreeSallePerDayAndWeekAndSceance(@Valid GetPayload payload) {
        return salleService.getFreeSallePerDayAndWeekAndSceance(payload.getDay(), payload.getWeek(), payload.getSeance());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response addSalle(@Valid CreateSallePayload payload, @HeaderParam("email") String email) throws DuplicationException, UnauthorizedException {
        Salle salle = salleService.addSalle(payload, email);
        return Response.status(Response.Status.CREATED).entity(salle).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response updateSalle(@PathParam("id") Long id, @Valid UpdateSallePayload payload, @HeaderParam("email") String email) throws DuplicationException, UnauthorizedException, NotFoundException {
        Salle salle = salleService.updateSalle(id, payload, email);
        return Response.ok(salle).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteSalle(@PathParam("id") Long id, @HeaderParam("email") String email) throws UnauthorizedException, NotFoundException {
        salleService.deleteSalle(id, email);
        return Response.noContent().build();

    }


}
