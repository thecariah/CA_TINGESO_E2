package cl.tingeso.proveedorservice.repositories;

import cl.tingeso.proveedorservice.entities.ProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, String> {

    //encontrar por codigo
    @Query("SELECT p FROM ProveedorEntity p WHERE p.codigo = :codigo")
    ProveedorEntity findByCodigo(@Param("codigo") String codigo);

    //encontrar por nombre
    @Query("SELECT p FROM ProveedorEntity p WHERE p.nombre = :nombre")
    ProveedorEntity findByNombre(@Param("nombre") String nombre);

    //obtener nombre
    @Query("SELECT p.nombre FROM ProveedorEntity p WHERE p.codigo = :codigo")
    String findNombre(@Param("codigo") String codigo);

    //obtener categoria
    @Query("SELECT p.categoria FROM ProveedorEntity p WHERE p.codigo = :codigo")
    String findCategoria(@Param("codigo") String codigo);

    //obtener retencion
    @Query("SELECT p.retencion FROM ProveedorEntity p WHERE p.codigo = :codigo")
    String findRetencion(@Param("codigo") String codigo);
}
