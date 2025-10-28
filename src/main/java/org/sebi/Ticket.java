package org.sebi;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class Ticket extends PanacheEntity {
    public String title;
    public String description;
    
    // Cache the computed type to avoid recalculating on every access
    @Transient
    private TicketType cachedComputedType;
    
    /**
     * Dynamically computes ticket type based on keywords in title and description.
     * Uses caching to avoid expensive recalculation.
     */
    public TicketType getComputedType() {
        // Return cached value if available
        if (cachedComputedType != null) {
            return cachedComputedType;
        }
        
        // Compute and cache the type
        cachedComputedType = computeTypeFromContent();
        return cachedComputedType;
    }
    
    /**
     * Forces recomputation of the ticket type (useful after title/description changes)
     */
    public void invalidateComputedTypeCache() {
        cachedComputedType = null;
    }
    
    private TicketType computeTypeFromContent() {
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
