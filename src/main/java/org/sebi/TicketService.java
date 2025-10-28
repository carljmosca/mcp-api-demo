package org.sebi;

import java.util.List;

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
        
        if (type == null) {
            LOG.debug("No filter applied, returning all tickets");
            return Ticket.listAll();
        }
        
        // Use database-level filtering with JPQL for better performance
        String query = buildTypeFilterQuery(type);
        List<Ticket> filteredTickets = Ticket.list(query);
                
        LOG.debug("Filtered tickets count: {}", filteredTickets.size());
        return filteredTickets;
    }
    
    public List<Ticket> getTicketsWithLimit(TicketType type, int limit) {
        LOG.debug("TicketService.getTicketsWithLimit called with type: {} and limit: {}", type, limit);
        
        if (type == null) {
            LOG.debug("No filter applied, returning limited tickets");
            return Ticket.find("ORDER BY id DESC").page(0, limit).list();
        }
        
        // Use database-level filtering with limit for even better performance
        String query = buildTypeFilterQuery(type);
        List<Ticket> filteredTickets = Ticket.find(query + " ORDER BY id DESC").page(0, limit).list();
                
        LOG.debug("Filtered tickets count: {}", filteredTickets.size());
        return filteredTickets;
    }
    
    private String buildTypeFilterQuery(TicketType type) {
        // Build JPQL query to filter at database level based on keyword patterns
        switch (type) {
            case SECURITY:
                return "LOWER(title || ' ' || description) LIKE '%security%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%vulnerability%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%exploit%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%breach%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%authentication%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%authorization%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%encryption%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%ssl%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%tls%'";
            case PERFORMANCE:
                return "LOWER(title || ' ' || description) LIKE '%performance%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%slow%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%timeout%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%memory%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%cpu%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%optimization%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%bottleneck%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%latency%'";
            case FEATURE:
                return "LOWER(title || ' ' || description) LIKE '%feature%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%enhancement%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%improvement%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%new%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%add%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%implement%'";
            case DOCUMENTATION:
                return "LOWER(title || ' ' || description) LIKE '%documentation%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%docs%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%readme%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%guide%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%tutorial%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%manual%'";
            case MAINTENANCE:
                return "LOWER(title || ' ' || description) LIKE '%maintenance%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%cleanup%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%refactor%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%update%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%upgrade%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%dependency%'";
            case BUG:
            default:
                // For BUG type, exclude all other categories (complex but more accurate)
                return "NOT (" +
                       "LOWER(title || ' ' || description) LIKE '%security%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%vulnerability%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%performance%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%feature%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%enhancement%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%documentation%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%maintenance%' OR " +
                       "LOWER(title || ' ' || description) LIKE '%cleanup%')";
        }
    }
}
