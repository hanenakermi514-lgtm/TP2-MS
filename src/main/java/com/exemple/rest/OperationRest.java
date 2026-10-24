package com.exemple.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/operations")
public class OperationRest {
    private static final Map<Integer, Operation> OPERATIONS = new ConcurrentSkipListMap<>();
    private static final AtomicInteger COMPTEUR = new AtomicInteger(0);
    private static final String ERREUR = "{\"erreur\":\"Calcul impossible\"}";

    // Retourne le résultat, ou null si le calcul est impossible
    private Double calculer(Operation op) {
        if (op == null || op.getOperateur() == null) {
            return null;
        }
        switch (op.getOperateur()) {
            case "somme":        return (double) (op.getA() + op.getB());
            case "soustraction": return (double) (op.getA() - op.getB());
            case "produit":      return (double) (op.getA() * op.getB());
            case "division":
                if (op.getB() == 0) return null;
                return (double) op.getA() / op.getB();
            default:             return null;   // "racine" et opérateurs inconnus
        }
    }

    private Response erreur() {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ERREUR)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response creer(Operation demande, @Context UriInfo uriInfo) {
        Double resultat = calculer(demande);
        if (resultat == null) {
            return erreur();
        }
        demande.setId(COMPTEUR.incrementAndGet());
        demande.setResultat(resultat);
        OPERATIONS.put(demande.getId(), demande);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(demande.getId())).build();
        return Response.created(location).entity(demande).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response lister() {
        return Response.ok(new ArrayList<>(OPERATIONS.values())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lire(@PathParam("id") int id) {
        Operation op = OPERATIONS.get(id);
        if (op == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(op).build();
    }

    // F.4
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifier(@PathParam("id") int id, Operation op) {
        if (!OPERATIONS.containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Double resultat = calculer(op);
        if (resultat == null) {
            return erreur();
        }
        op.setId(id);
        op.setResultat(resultat);
        OPERATIONS.put(id, op);
        return Response.ok(op).build();
    }

    // F.5
    @DELETE
    @Path("/{id}")
    public Response supprimer(@PathParam("id") int id) {
        if (OPERATIONS.remove(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();   // 204
    }
}