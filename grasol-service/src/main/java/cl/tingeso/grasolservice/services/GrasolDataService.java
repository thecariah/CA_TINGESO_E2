package cl.tingeso.grasolservice.services;

import cl.tingeso.grasolservice.entities.GrasolDataEntity;
import cl.tingeso.grasolservice.repositories.GrasolDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrasolDataService {

    @Autowired
    private GrasolDataRepository grasolDataRepository;

    public void guardarGrasol(String proveedor, String ptj_grasa, String ptj_solido_total){
        GrasolDataEntity grasolData = new GrasolDataEntity();

        grasolData.setProveedor(proveedor);
        grasolData.setPtj_grasa(ptj_grasa);
        grasolData.setPtj_solido_total(ptj_solido_total);

        grasolDataRepository.save(grasolData);
    }

    public void deleteGrasol(){ grasolDataRepository.deleteAll(); }

    public List<GrasolDataEntity> obtenerGrasoles(){
        return grasolDataRepository.findAll();
    }

    public List<GrasolDataEntity> obtenerGrasolesDeProveedor(String codigo){
        return this.grasolDataRepository.findGrasoles(codigo);
    }

}
