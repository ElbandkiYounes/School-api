package com.school_1.api.Reservation;

import com.school_1.api.Commons.Exceptions.*;
import com.school_1.api.Commons.Exceptions.BadRequestException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Security.Secured;
import com.school_1.api.Reservation.models.CreateReservationPayload;
import com.school_1.api.Reservation.models.Reservation;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/reservations")
@Stateless
public class ReservetionRouter {

    @Inject
    private ReservationService reservationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Reservation> getReservations() {
        return reservationService.getAllReservations();
    }

    @GET
    @Path("/professeur/{professeurId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Reservation> getReservationsByProfesseurId(@PathParam("professeurId")Long  professeurId) throws UnauthorizedException, NotFoundException {
        return reservationService.getPronfesseurReservations(professeurId);
    }

    @GET
    @Path("day")
    @Produces(MediaType.TEXT_PLAIN)
    @Secured
    public Response getCurrectDay() throws BadRequestException {
        return Response.ok(reservationService.getCurrentDay()).build();
    }

    @GET
    @Path("week")
    @Produces(MediaType.TEXT_PLAIN)
    @Secured
    public Response getCurrentWeek() throws BadRequestException {
        return Response.ok(reservationService.getCurrentWeek()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response addReservation(@Valid CreateReservationPayload payload, @HeaderParam("email") String email) throws UnauthorizedException, BadRequestException, DuplicationException, NotFoundException {
        Reservation newReservation = reservationService.addReservation(payload, email);
        return Response.status(Response.Status.CREATED).entity(newReservation).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteReservation(@PathParam("id") Long id, @HeaderParam("email") String email) throws NotFoundException, UnauthorizedException, AccessDeniedException {
        reservationService.deleteReservation(id, email);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
