package cl.tingeso.logisticaservice.repositories;

import cl.tingeso.logisticaservice.entities.LogisticaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogisticaRepository extends JpaRepository<LogisticaEntity, String> {

    //encontrar planillas de logistica por proveedor
    @Query("SELECT p FROM LogisticaEntity p WHERE p.codigo = :codigo")
    List<LogisticaEntity> findPlanillasByProveedor(@Param("codigo") String codigo);

    //encontrar planilla de logistica de un proveedor por la quincena
    @Query("SELECT p FROM LogisticaEntity p WHERE p.codigo = :codigo AND p.quincena = :quincena")
    LogisticaEntity findPlanillaProveedorByQuincena(@Param("codigo") String codigo, @Param("quincena") String quincena);

    //insertar codigo proveedor en planilla de logistica
    @Query(value = "insert into logistica_planilla(codigo) values(?)", nativeQuery = true)
    void insertarDatos(@Param("codigo") String codigo);
}
