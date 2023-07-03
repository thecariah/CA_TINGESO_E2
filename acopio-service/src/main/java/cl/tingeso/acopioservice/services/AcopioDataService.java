package cl.tingeso.acopioservice.services;


import cl.tingeso.acopioservice.entities.AcopioDataEntity;
import cl.tingeso.acopioservice.repositories.AcopioDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcopioDataService{
    @Autowired
    AcopioDataRepository acopioDataRepository;

    public void guardarAcopio(String fecha, String turno, String proveedor, String kls_leche){
        AcopioDataEntity acopioData = new AcopioDataEntity();

        acopioData.setFecha(fecha);
        acopioData.setTurno(turno);
        acopioData.setProveedor(proveedor);
        acopioData.setKls_leche(kls_leche);

        acopioDataRepository.save(acopioData);
    }

    public void deleteAcopios(){ acopioDataRepository.deleteAll(); }


    public List<AcopioDataEntity> obtenerAcopios(){
        return acopioDataRepository.findAll();
    }

    public AcopioDataEntity obtenerAcopioEspecifico(String codigo, String fecha, String turno){
        return this.acopioDataRepository.findAcopio(codigo, fecha, turno);
    }

    public List<AcopioDataEntity> obtenerAcopiosDeProveedor(String codigo){
        return this.acopioDataRepository.findAcopiosByCodigo(codigo);
    }

    public List<AcopioDataEntity> obtenerAcopiosProveedorPorFecha(String codigo, String fecha){
        return this.acopioDataRepository.findAcopiosDeProveedorByFecha(codigo, fecha);
    }

}
