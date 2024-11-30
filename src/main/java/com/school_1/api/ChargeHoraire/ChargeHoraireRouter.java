package com.school_1.api.ChargeHoraire;

import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import com.school_1.api.ChargeHoraire.models.CreateChargeHorairePayload;
import com.school_1.api.ChargeHoraire.models.UpdateChargeHorairePayload;
import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Commons.Security.Secured;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/charges-horaires")
@Stateless
public class ChargeHoraireRouter {

    @Inject
    private ChargeHoraireService chargeHoraireService;

    @GET
    @Path("/filiere/{filiereId}")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChargeHoraire> getAllChargesHoraires(@PathParam("filiereId") Long filiereId) throws NotFoundException {
        return chargeHoraireService.getChargesHorairesByFiliere(filiereId);
    }

    @GET
    @Path("/{id}")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChargeHoraireById(@PathParam("id") Long id) throws NotFoundException {
        ChargeHoraire chargeHoraire = chargeHoraireService.getChargeHoraireById(id);
        return Response.ok(chargeHoraire).build();
    }

    @POST
    @Secured
    @Path("/filiere/{filiereId}/matiere/{matiereId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addChargeHoraire(@Valid CreateChargeHorairePayload payload, @HeaderParam("Email") String email, @PathParam("filiereId") Long filiereId, @PathParam("matiereId") Long matiereId) throws UnauthorizedException, NotFoundException, DuplicationException {
        ChargeHoraire chargeHoraire = chargeHoraireService.addChargeHoraire(payload, email, filiereId, matiereId);
        return Response.status(Response.Status.CREATED).entity(chargeHoraire).build();
    }

    @PUT
    @Path("/{id}")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateChargeHoraire(@PathParam("id") Long id, @Valid UpdateChargeHorairePayload payload, @HeaderParam("Email") String email) throws UnauthorizedException, NotFoundException {
        ChargeHoraire chargeHoraire = chargeHoraireService.updateChargeHoraire(id, payload, email);
        return Response.ok(chargeHoraire).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteChargeHoraire(@PathParam("id") Long id, @HeaderParam("Email") String email) throws UnauthorizedException, NotFoundException {
        chargeHoraireService.deleteChargeHoraire(id, email);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/filiere/{filiereId}")
    @Secured
    public Response deleteChargeHoraireByFiliere(@PathParam("filiereId") Long filiereId, @HeaderParam("Email") String email) throws UnauthorizedException, NotFoundException {
        chargeHoraireService.deleteChargeHoraireByFiliere(filiereId, email);
        return Response.noContent().build();
    }
}
