package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.foresta.model.DTO.TransferResponseDTO;
import co.edu.unbosque.foresta.model.entity.AccountACHRelationShip;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper mm = new ModelMapper();

        mm.typeMap(ResponseAccountACHDTO.class, AccountACHRelationShip.class)
                .addMappings(map -> {
                    map.skip(AccountACHRelationShip::setId);         
                    map.map(ResponseAccountACHDTO::getId, AccountACHRelationShip::setAchId);
                    map.map(ResponseAccountACHDTO::getAccountOwnerName, AccountACHRelationShip::setAccountOwnerName);
                    map.map(ResponseAccountACHDTO::getBankAccountType, AccountACHRelationShip::setBankAccountType);
                    map.map(ResponseAccountACHDTO::getBankAccountNumber, AccountACHRelationShip::setBankAccountNumber);
                    map.map(ResponseAccountACHDTO::getNickname, AccountACHRelationShip::setNickname);
                });

        mm.typeMap(TransferResponseDTO.class, co.edu.unbosque.foresta.model.entity.TransferLog.class)
                .addMappings(map -> {
                    map.skip(co.edu.unbosque.foresta.model.entity.TransferLog::setId);
                    map.map(TransferResponseDTO::getId, co.edu.unbosque.foresta.model.entity.TransferLog::setExternalId);
                    map.map(TransferResponseDTO::getAmount, co.edu.unbosque.foresta.model.entity.TransferLog::setAmount);
                    map.map(TransferResponseDTO::getStatus, co.edu.unbosque.foresta.model.entity.TransferLog::setStatus);
                });

        return mm;
    }




}
