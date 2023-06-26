package cl.tingeso.logisticaservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AcopioModel {
    private String fecha;
    private String turno;
    private String proveedor;
    private String kls_leche;
}
