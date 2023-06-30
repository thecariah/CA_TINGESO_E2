package cl.tingeso.grasolservice.entities;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "grasol")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GrasolDataEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String proveedor;
    private String ptj_grasa;
    private String ptj_solido_total;
}
