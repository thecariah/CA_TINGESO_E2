package cl.tingeso.acopioservice.entities;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "acopio")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AcopioDataEntity {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fecha;
    private String turno;
    private String proveedor;   // codigo del proveedor
    private String kls_leche;
}
