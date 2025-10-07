package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.model.DTO.UsuarioDTO;
import co.edu.unbosque.foresta.model.entity.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();
        mm.typeMap(Usuario.class, UsuarioDTO.class).addMappings(m ->
                m.map(src -> src.getRol().getNombre(), UsuarioDTO::setRol)
        );
        return mm;
    }
}
