package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogRequest {
    private String eventCode;
    private String resource;
    private String action;
    private Map<String,Object> details;

    public AuditLogRequest() {}

    public AuditLogRequest(String eventCode, String resource, String action, Map<String, Object> details) {
        this.eventCode = eventCode;
        this.resource = resource;
        this.action = action;
        this.details = details;
    }

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
