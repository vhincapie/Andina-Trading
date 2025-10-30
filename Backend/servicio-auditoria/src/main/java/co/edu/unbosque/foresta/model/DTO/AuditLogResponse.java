package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogResponse {
    private Long id;
    private String eventCode;
    private String userId;
    private String username;
    private String ipAddress;
    private String resource;
    private String action;
    private Map<String,Object> details;
    private Instant createdAt;

    public AuditLogResponse() {}

    public AuditLogResponse(Long id, String eventCode, String userId, String username, String ipAddress, String resource, String action, Map<String, Object> details, Instant createdAt) {
        this.id = id;
        this.eventCode = eventCode;
        this.userId = userId;
        this.username = username;
        this.ipAddress = ipAddress;
        this.resource = resource;
        this.action = action;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
