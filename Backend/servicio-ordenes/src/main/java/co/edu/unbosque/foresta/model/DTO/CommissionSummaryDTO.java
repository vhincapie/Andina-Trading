package co.edu.unbosque.foresta.model.DTO;

import java.math.BigDecimal;

public class CommissionSummaryDTO {
    private BigDecimal total;
    private Long cantidadOrdenes;

    public CommissionSummaryDTO() {

    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getCantidadOrdenes() {
        return cantidadOrdenes;
    }

    public void setCantidadOrdenes(Long cantidadOrdenes) {
        this.cantidadOrdenes = cantidadOrdenes;
    }
}
