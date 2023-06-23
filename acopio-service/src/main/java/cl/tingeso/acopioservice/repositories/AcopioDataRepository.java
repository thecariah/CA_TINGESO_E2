package cl.tingeso.acopioservice.repositories;

import cl.tingeso.acopioservice.entities.AcopioDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcopioDataRepository extends JpaRepository<AcopioDataEntity, Long> {

    //encontrar acopio especifico
    @Query("SELECT a FROM AcopioDataEntity a " +
            "WHERE a.proveedor = :codigo and a.fecha = :fecha and a.turno = :turno")
    AcopioDataEntity findAcopio(@Param("codigo") String codigo,
                                @Param("fecha") String fecha,
                                @Param("turno") String turno);

    //encontrar acopios por proveedor
    @Query("SELECT a FROM AcopioDataEntity a WHERE a.proveedor = :codigo")
    List<AcopioDataEntity> findAcopiosByCodigo(@Param("codigo") String codigo);
}
