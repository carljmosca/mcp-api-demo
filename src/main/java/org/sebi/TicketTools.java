package org.sebi;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.quarkiverse.mcp.server.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TicketTools {

    private static final Logger LOG = LoggerFactory.getLogger(TicketTools.class);

    @Inject
    TicketService ticketService;

    @Tool(description = "Create a ticket")
    public String createTicket(String title, String description) {
        Ticket ticket = new Ticket();
        ticket.title = title;
        ticket.description = description;
        Ticket created = ticketService.createTicket(ticket);
        return String.format("Successfully created ticket ID: %d\nTitle: %s\nDescription: %s\nComputed Type: %s", 
                            created.id, created.title, created.description, created.getComputedType());
    }

    @Tool(description = "Delete a ticket")
    public String deleteTicket(Long id) {
        ticketService.deleteTicket(id);
        return "Ticket deleted";
    }

    @Tool(description = "Get a ticket by ID")
    public String getTicket(Long id) {
        try {
            Ticket ticket = ticketService.getTicket(id);
            return String.format("Ticket Details:\nID: %d\nTitle: %s\nDescription: %s\nComputed Type: %s", 
                               ticket.id, ticket.title, ticket.description, ticket.getComputedType());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "Get a list of tickets. You can optionally filter by ticket type.")
    public String getTickets(Optional<String> type) {
        try {
            LOG.debug("TicketTools.getTickets called with type: {}", type);
            TicketType ticketType = null;
            if (type.isPresent()) {
                String typeValue = type.get().toUpperCase().trim();
                LOG.debug("Converting type string to enum: {}", typeValue);
                try {
                    ticketType = TicketType.valueOf(typeValue);
                    LOG.debug("Successfully converted to TicketType: {}", ticketType);
                } catch (IllegalArgumentException e) {
                    LOG.warn("Invalid ticket type: {}, returning all tickets", typeValue);
                    return "Invalid ticket type: " + typeValue + ". Valid types are: BUG, FEATURE, SECURITY, PERFORMANCE, DOCUMENTATION, MAINTENANCE";
                }
            } else {
                LOG.debug("No type provided, returning all tickets");
            }
            
            // Use optimized method that limits results at database level
            List<Ticket> result = ticketService.getTicketsWithLimit(ticketType, 10);
            LOG.debug("Returning {} tickets (limited)", result.size());
            
            if (result.isEmpty()) {
                return "No tickets found" + (ticketType != null ? " for type " + ticketType : "") + ".";
            }
            
            StringBuilder response = new StringBuilder();
            response.append("Found ").append(result.size()).append(" ticket(s)");
            if (ticketType != null) {
                response.append(" of type ").append(ticketType);
            }
            response.append(" (showing up to 10):\n\n");
            
            // Process all returned tickets since we already limited at DB level
            int maxTickets = result.size();
            for (int i = 0; i < maxTickets; i++) {
                Ticket ticket = result.get(i);
                response.append("ID: ").append(ticket.id)
                       .append(" | Type: ").append(ticket.getComputedType())
                       .append(" | Title: ").append(ticket.title)
                       .append("\nDescription: ").append(ticket.description)
                       .append("\n\n");
            }
            
            if (result.size() > maxTickets) {
                response.append("... and ").append(result.size() - maxTickets).append(" more tickets.");
            }
            
            return response.toString();
        } catch (Exception e) {
            LOG.error("Error in getTickets", e);
            return "Error retrieving tickets: " + e.getMessage();
        }
    }
}
