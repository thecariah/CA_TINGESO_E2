package cl.tingeso.proveedorservice.controllers;

import cl.tingeso.proveedorservice.entities.ProveedorEntity;
import cl.tingeso.proveedorservice.services.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ProveedorController {
    @Autowired
    ProveedorService proveedorService;

    @GetMapping("/proveedores")
    public String menuProveedores(){
        return "proveedores-index";
    }

    @GetMapping ("/proveedores/new")
    public String nuevoProveedor(){
        return "proveedores-new";
    }

    @GetMapping("/proveedores/all")
    public ResponseEntity<List<ProveedorEntity>> getAllProveedores(){
        List<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        if(proveedores.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/proveedores/{codigo}")
    public ResponseEntity<ProveedorEntity> getProveedorByCodigo(@PathVariable("codigo") String codigo){
        ProveedorEntity proveedor = proveedorService.obtenerPorCodigo(codigo);

        if(proveedor == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proveedor);
    }

    @PostMapping("/proveedores/new")
    public String createProveedor(@RequestParam("codigo") String codigo,
                                  @RequestParam("nombre") String nombre,
                                  @RequestParam("categoria") String categoria,
                                  @RequestParam("retencion") String retencion){

        proveedorService.guardarProveedor(codigo, nombre, categoria, retencion);
        return "redirect:/proveedores/new";
    }

    @GetMapping("/proveedores/delete")
    public void deleteProveedores(){
        proveedorService.eliminarProveedores();
    }

    // ... mapping de modelos ...

}
