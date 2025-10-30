package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.model.entity.AlpacaAccount;
import co.edu.unbosque.foresta.repository.IAlpacaAccountRepository;
import co.edu.unbosque.foresta.service.interfaces.IAlpacaAccountQueryService;
import org.springframework.stereotype.Service;

import java.util.Map;

import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Service
public class AlpacaAccountQueryServiceImpl implements IAlpacaAccountQueryService {

    private final IAlpacaAccountRepository repo;
    private final AuditSender auditSender;

    public AlpacaAccountQueryServiceImpl(IAlpacaAccountRepository repo, AuditSender auditSender) {
        this.repo = repo;
        this.auditSender = auditSender;
    }

    @Override
    public AlpacaAccountDTO getByInversionistaId(Long inversionistaId) {
        AlpacaAccount acc = repo.findByInversionista_Id(inversionistaId)
                .orElseThrow(() -> {
                    auditSender.log("", new AuditLogRequest(
                            "ALPACA_ACCOUNT_NOT_FOUND",
                            "/api/alpaca/accounts/by-inversionista",
                            "Cuenta Alpaca no asociada",
                            Map.of("inversionistaId", inversionistaId)
                    ));
                    return new RuntimeException("Inversionista sin cuenta Alpaca");
                });
        AlpacaAccountDTO dto = new AlpacaAccountDTO();
        dto.setAlpacaId(acc.getAlpacaId());
        dto.setStatus(acc.getStatus());
        dto.setCurrency(acc.getCurrency());
        auditSender.log("", new AuditLogRequest(
                "ALPACA_ACCOUNT_QUERY",
                "/api/alpaca/accounts/by-inversionista",
                "Consulta cuenta Alpaca por inversionista",
                Map.of("inversionistaId", inversionistaId, "alpacaId", acc.getAlpacaId(), "status", acc.getStatus())
        ));
        return dto;
    }
}
