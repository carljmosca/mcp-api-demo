package org.sebi;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Ticket extends PanacheEntity {
    public String title;
    public String description;
    
    /**
     * Dynamically computes ticket type based on keywords in title and description
     */
    public TicketType getComputedType() {
        String content = (title + " " + description).toLowerCase();
        
        // Security-related keywords
        if (content.contains("security") || content.contains("vulnerability") || 
            content.contains("exploit") || content.contains("breach") || 
            content.contains("authentication") || content.contains("authorization") ||
            content.contains("encryption") || content.contains("ssl") || content.contains("tls")) {
            return TicketType.SECURITY;
        }
        
        // Performance-related keywords
        if (content.contains("performance") || content.contains("slow") || 
            content.contains("timeout") || content.contains("memory") || 
            content.contains("cpu") || content.contains("optimization") ||
            content.contains("bottleneck") || content.contains("latency")) {
            return TicketType.PERFORMANCE;
        }
        
        // Feature-related keywords
        if (content.contains("feature") || content.contains("enhancement") || 
            content.contains("improvement") || content.contains("new") ||
            content.contains("add") || content.contains("implement")) {
            return TicketType.FEATURE;
        }
        
        // Documentation-related keywords
        if (content.contains("documentation") || content.contains("docs") || 
            content.contains("readme") || content.contains("guide") ||
            content.contains("tutorial") || content.contains("manual")) {
            return TicketType.DOCUMENTATION;
        }
        
        // Maintenance-related keywords
        if (content.contains("maintenance") || content.contains("cleanup") || 
            content.contains("refactor") || content.contains("update") ||
            content.contains("upgrade") || content.contains("dependency")) {
            return TicketType.MAINTENANCE;
        }
        
        // Default to BUG if no other category matches
        return TicketType.BUG;
    }
}
