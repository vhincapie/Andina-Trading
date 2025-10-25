package co.edu.unbosque.foresta.jobs;

import co.edu.unbosque.foresta.service.implementations.OrdenServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrdenesSyncJob {

    private static final Logger log = LoggerFactory.getLogger(OrdenesSyncJob.class);
    private final OrdenServiceImpl ordenes;

    public OrdenesSyncJob(OrdenServiceImpl ordenes) {
        this.ordenes = ordenes;
    }

    @Scheduled(fixedDelay = 60_000L, initialDelay = 20_000L)
    public void syncEstados() {
        try {
            int n = ordenes.sincronizarEstadosPendientes();
            if (n > 0) log.info("Sincronizadas {} órdenes con Alpaca", n);
        } catch (Exception e) {
            log.warn("Fallo sincronizando órdenes: {}", e.getMessage());
        }
    }
}
