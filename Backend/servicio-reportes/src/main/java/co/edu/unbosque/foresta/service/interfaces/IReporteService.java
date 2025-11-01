package co.edu.unbosque.foresta.service.interfaces;

import java.io.OutputStream;

public interface IReporteService {
    void csvInversionistas(OutputStream out);
    void csvComisionistas(OutputStream out);
    void csvOrdenes(OutputStream out);
}
