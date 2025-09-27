package org.sebi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/tickets")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    @Inject
    TicketService ticketService;

    @GET
    public List<Ticket> getTickets(@QueryParam("type") TicketType type) {
        return ticketService.getTickets(type);
    }

    @GET
    @Path("/{id}")
    public Ticket getTicket(@PathParam("id") Long id) {
        return ticketService.getTicket(id);
    }

    @POST
    @Transactional
    public Response createTicket(Ticket ticket) {
        if (ticket.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        ticketService.createTicket(ticket);
        return Response.ok(ticket).status(201).build();
    }
}