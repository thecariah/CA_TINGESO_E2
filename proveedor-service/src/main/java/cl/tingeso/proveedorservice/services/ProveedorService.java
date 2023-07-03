package cl.tingeso.proveedorservice.services;

import cl.tingeso.proveedorservice.entities.ProveedorEntity;
import cl.tingeso.proveedorservice.repositories.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProveedorService {
    @Autowired
    ProveedorRepository proveedorRepository;

    @Autowired
    RestTemplate restTemplate;

    public void guardarProveedor(String codigo, String nombre, String categoria, String retencion){
        ProveedorEntity proveedor = new ProveedorEntity();
        proveedor.setCodigo(codigo);
        proveedor.setNombre(nombre);
        proveedor.setCategoria(categoria);
        proveedor.setRetencion(retencion);
        proveedorRepository.save(proveedor);
    }

    public void eliminarProveedores(){
        proveedorRepository.deleteAll();
    }

    public List<ProveedorEntity> obtenerProveedores(){
        return proveedorRepository.findAll();
    }

    public ProveedorEntity obtenerPorCodigo(String codigo){
        return proveedorRepository.findByCodigo(codigo);
    }

    public ProveedorEntity obtenerPorNombre(String nombre){
        return proveedorRepository.findByNombre(nombre);
    }

    public String obtenerNombre(String codigo){
        return proveedorRepository.findNombre(codigo);
    }

    public String obtenerCategoria(String codigo){
        return proveedorRepository.findCategoria(codigo);
    }

    public String obtenerRetencion(String codigo){
        return proveedorRepository.findRetencion(codigo);
    }

}
