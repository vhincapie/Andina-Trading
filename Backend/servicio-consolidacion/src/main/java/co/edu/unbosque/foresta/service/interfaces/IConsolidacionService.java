package co.edu.unbosque.foresta.service.interfaces;

import java.io.OutputStream;

public interface IConsolidacionService {

    void csvRegional(OutputStream out);

    void csvSegmentacion(OutputStream out, String criterio, Long paisId, Double minMonto, Double maxMonto);

    void csvComisiones(OutputStream out);
}
