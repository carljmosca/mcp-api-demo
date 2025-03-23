package org.sebi;

import java.util.List;

import io.quarkiverse.mcp.server.Tool;
import jakarta.inject.Inject;

public class TicketTools {

    @Inject
    TicketService ticketService;

    @Tool(description = "Create a ticket")
    public Ticket createTicket(Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    @Tool(description = "Delete a ticket")
    public String deleteTicket(Long id) {
        ticketService.deleteTicket(id);
        return "Ticket deleted";
    }

    @Tool(description = "Get a ticket")
    public Ticket getTicket(Long id) {
        return ticketService.getTicket(id);
    }

    @Tool(description = "Get all tickets")
    public List<Ticket> getTickets() {
        return ticketService.getTickets();
    }
    
}
