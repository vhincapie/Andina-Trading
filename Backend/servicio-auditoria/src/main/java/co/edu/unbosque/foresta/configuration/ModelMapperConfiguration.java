package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.model.DTO.AuditLogResponse;
import co.edu.unbosque.foresta.model.entity.AuditLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return om;
    }

    @Bean
    public ModelMapper modelMapper(ObjectMapper om) {
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        Converter<Timestamp, Instant> tsToInstant = ctx ->
                ctx.getSource() == null ? null : ctx.getSource().toInstant();

        Converter<String, Map<String,Object>> jsonToMap = ctx -> {
            String s = ctx.getSource();
            if (s == null || s.isBlank()) return Map.of();
            try {
                return om.readValue(s, new TypeReference<Map<String,Object>>(){});
            } catch (Exception e) {
                return Map.of("raw", s);
            }
        };

        TypeMap<AuditLog, AuditLogResponse> tm =
                mm.createTypeMap(AuditLog.class, AuditLogResponse.class);

        tm.addMappings(m -> {
            m.using(tsToInstant).map(AuditLog::getCreatedAt, AuditLogResponse::setCreatedAt);
            m.using(jsonToMap).map(AuditLog::getDetailsJson, AuditLogResponse::setDetails);
        });

        return mm;
    }
}
