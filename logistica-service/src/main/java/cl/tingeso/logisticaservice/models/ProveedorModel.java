package cl.tingeso.logisticaservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProveedorModel {
    private String codigo;
    private String nombre;
    private String categoria;
    private String retencion;
}
