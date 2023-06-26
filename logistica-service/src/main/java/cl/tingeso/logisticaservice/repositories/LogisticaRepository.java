package cl.tingeso.logisticaservice.repositories;

import cl.tingeso.logisticaservice.entities.LogisticaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LogisticaRepository extends JpaRepository<LogisticaEntity, String> {

    //encontrar planilla de logistica por proveedor
    @Query("SELECT p FROM LogisticaEntity p WHERE p.codigo = :codigo")
    LogisticaEntity findPlanilla(@Param("codigo") String codigo);

    //insertar codigo proveedor en planilla de logistica
    @Query(value = "insert into logistica_planilla(codigo) values(?)", nativeQuery = true)
    void insertarDatos(@Param("codigo") String codigo);
}
