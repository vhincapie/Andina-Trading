package co.edu.unbosque.foresta.model.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_code", nullable=false, length=100)
    private String eventCode;

    @Column(name="user_id", length=100)
    private String userId;

    @Column(name="username", length=150)
    private String username;

    @Column(name="ip_address", length=64)
    private String ipAddress;

    @Column(name="resource", length=200)
    private String resource;

    @Column(name="action", length=200)
    private String action;

    @Column(name="details_json", columnDefinition = "JSON")
    private String detailsJson;

    @Column(name="created_at", nullable=false, updatable=false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    public AuditLog() {}

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
    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
