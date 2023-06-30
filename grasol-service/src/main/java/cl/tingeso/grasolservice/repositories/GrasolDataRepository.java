package cl.tingeso.grasolservice.repositories;

import cl.tingeso.grasolservice.entities.GrasolDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrasolDataRepository extends JpaRepository<GrasolDataEntity, Long> {

    //encontrar grasol por proveedor
    @Query("SELECT g FROM GrasolDataEntity g WHERE g.proveedor = :codigo")
    GrasolDataEntity findGrasol(@Param("codigo") String codigo);
}
