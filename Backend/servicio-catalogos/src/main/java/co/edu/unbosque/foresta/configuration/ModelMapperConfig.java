package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.model.DTO.CiudadDTO;
import co.edu.unbosque.foresta.model.DTO.PaisDTO;
import co.edu.unbosque.foresta.model.entity.Ciudad;
import co.edu.unbosque.foresta.model.entity.Pais;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();
        cargarCiudadMappings(mm);
        return mm;
    }

    private void cargarCiudadMappings(ModelMapper mm) {

        mm.typeMap(Ciudad.class, CiudadDTO.class)
                .addMappings(m -> m.map(src -> src.getPais(), CiudadDTO::setPaisDTO));

        mm.typeMap(Pais.class, PaisDTO.class)
                .addMappings(m -> m.map(src -> src.getSituacionEconomica(), PaisDTO::setSituacionEconomicaDTO));

    }
}
