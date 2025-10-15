package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDTO {

    @JsonProperty("document_type")
    private String documentType;

    @JsonProperty("content")
    private String content; // base64 si aplica

    public DocumentDTO() {

    }

    public DocumentDTO(String documentType, String content) {
        this.documentType = documentType;
        this.content = content;
    }

    //Getters/Setters
    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
