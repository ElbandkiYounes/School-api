package com.school_1.api.Matiere;

import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Commons.Security.Secured;
import com.school_1.api.Matiere.models.CreateMatierePayload;
import com.school_1.api.Matiere.models.Matiere;
import com.school_1.api.Matiere.models.UpdateMatierePayload;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/matieres")
@Stateless
public class MatiereRouter {

    @Inject
    private MatiereService matiereService;

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public List<Matiere> getAllMatieres() {
        return matiereService.getAllMatieres();
    }

    @GET
    @Path("/{id}")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatiereById(@PathParam("id") Long id) throws NotFoundException {
        Matiere matiere = matiereService.getMatiereById(id);
        return Response.ok(matiere).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response addMatiere(@Valid CreateMatierePayload createMatierePayload, @HeaderParam("Email") String email) throws UnauthorizedException, DuplicationException {
        Matiere matiere = matiereService.addMatiere(createMatierePayload, email);
        return Response.status(Response.Status.CREATED).entity(matiere).build();
    }

    @PUT
    @Path("/{id}")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMatiere(@PathParam("id") Long id, @Valid UpdateMatierePayload updateMatierePayload, @HeaderParam("Email") String email) throws UnauthorizedException, NotFoundException, DuplicationException {
        Matiere matiere = matiereService.updateMatiere(id, updateMatierePayload, email);
        return Response.ok(matiere).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteMatiere(@PathParam("id") Long id, @HeaderParam("Email") String email) throws UnauthorizedException, NotFoundException {
        matiereService.deleteMatiere(id, email);
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public List<Matiere> getMatieresByKey(@QueryParam("key") String key) {
        return matiereService.getMatieresByKey(key);
    }
}
