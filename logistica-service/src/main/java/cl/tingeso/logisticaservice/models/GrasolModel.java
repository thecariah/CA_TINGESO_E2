package cl.tingeso.logisticaservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GrasolModel {
    private String proveedor;
    private String ptj_grasa;
    private String ptj_solido_total;
}
