package org.sebi;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        ticket.persist();
        return ticket;
    }

    @Transactional
    public void deleteTicket(Long id) {
        Ticket ticket = Ticket.findById(id);
        if (ticket != null) {
            ticket.delete();
        } else {
            // Or log a warning, depending on desired behavior for non-existent IDs
            throw new WebApplicationException("Ticket with id of " + id + " does not exist.", 404);
        }
    }

    public Ticket getTicket(Long id) {
        Ticket entity = Ticket.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Ticket with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    public List<Ticket> getTickets(TicketType type) {
        LOG.debug("TicketService.getTickets called with type: {}", type);
        List<Ticket> allTickets = Ticket.listAll();
        LOG.debug("Total tickets in database: {}", allTickets.size());
        
        if (type == null) {
            LOG.debug("No filter applied, returning all tickets");
            return allTickets;
        }
        
        List<Ticket> filteredTickets = allTickets.stream()
                .filter(ticket -> {
                    TicketType computedType = ticket.getComputedType();
                    boolean matches = type.equals(computedType);
                    if (matches) {
                        LOG.trace("Ticket {} ({}) matches filter: {}", ticket.id, ticket.title, computedType);
                    }
                    return matches;
                })
                .collect(Collectors.toList());
                
        LOG.debug("Filtered tickets count: {}", filteredTickets.size());
        return filteredTickets;
    }
}
