package org.sebi;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TicketService {

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        ticket.persist();
        return ticket;
    }

    public void deleteTicket(Long id) {
        Ticket ticket = Ticket.findById(id);
        ticket.delete();
    }
    public Ticket getTicket(Long id) {
        return Ticket.findById(id);
    }
    public List<Ticket> getTickets() {
        return Ticket.listAll();
    }
    
}
