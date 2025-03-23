package org.sebi;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/tickets")
public class TicketResource {

    @Inject
    TicketService ticketService;

    @GET
    @Path("{id}")
    public Ticket getTicket(long id) {
        return ticketService.getTicket(id);
    }

    @GET
    public List<Ticket> getTickets() {
        return ticketService.getTickets();
    }

    @POST
    public Ticket createTicket(Ticket ticket) {
        return ticketService.createTicket(ticket);
    }
}
