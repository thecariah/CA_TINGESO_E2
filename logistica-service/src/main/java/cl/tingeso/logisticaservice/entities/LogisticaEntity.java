package cl.tingeso.logisticaservice.entities;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "logistica_planilla")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogisticaEntity {
    @Id
    @NotNull
    private String codigo;
    private String nombre;
    private String quincena;

    private double total_leche;
    private Integer dias_envio_leche;
    private double prom_diario_leche;

    private double ptj_grasa;
    private double ptj_solidos;

    private double ptj_var_grasa;
    private double ptj_var_solidos;
    private double ptj_var_leche;

    private double pago_leche;
    private double pago_grasa;
    private double pago_solidos;

    private double bonificacion;

    private double dcto_var_leche;
    private double dcto_var_grasa;
    private double dcto_var_solidos;

    private double pago_total;
    private double monto_retencion;
    private double monto_final;
}
