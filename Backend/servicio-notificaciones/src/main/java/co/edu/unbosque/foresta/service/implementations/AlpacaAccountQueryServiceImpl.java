package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.model.entity.AlpacaAccount;
import co.edu.unbosque.foresta.repository.IAlpacaAccountRepository;
import co.edu.unbosque.foresta.service.interfaces.IAlpacaAccountQueryService;
import org.springframework.stereotype.Service;

@Service
public class AlpacaAccountQueryServiceImpl implements IAlpacaAccountQueryService {

    private final IAlpacaAccountRepository repo;

    public AlpacaAccountQueryServiceImpl(IAlpacaAccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public AlpacaAccountDTO getByInversionistaId(Long inversionistaId) {
        AlpacaAccount acc = repo.findByInversionista_Id(inversionistaId)
                .orElseThrow(() -> new RuntimeException("Inversionista sin cuenta Alpaca"));
        AlpacaAccountDTO dto = new AlpacaAccountDTO();
        dto.setAlpacaId(acc.getAlpacaId());
        dto.setStatus(acc.getStatus());
        dto.setCurrency(acc.getCurrency());
        return dto;
    }
}