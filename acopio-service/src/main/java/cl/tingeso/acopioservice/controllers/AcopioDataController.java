package cl.tingeso.acopioservice.controllers;

import cl.tingeso.acopioservice.entities.AcopioDataEntity;
import cl.tingeso.acopioservice.services.AcopioDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class AcopioDataController {
    @Autowired
    AcopioDataService acopioDataService;

    @GetMapping("/acopios/all")
    public ResponseEntity<List<AcopioDataEntity>> getAllAcopios(){
        List<AcopioDataEntity> acopios = acopioDataService.obtenerAcopios();
        if(acopios.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(acopios);
    }

    @GetMapping("/acopios/{codigo}/{fecha}/{turno}")
    public ResponseEntity<AcopioDataEntity> getAcopio(@PathVariable("codigo") String codigo,
                                                                 @PathVariable("fecha") String fecha,
                                                                 @PathVariable("turno") String turno){
        AcopioDataEntity acopio = acopioDataService.obtenerAcopioEspecifico(codigo, fecha, turno);

        if(acopio == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(acopio);
    }

    @GetMapping("/acopios/{codigo}")
    public ResponseEntity<List<AcopioDataEntity>> getAcopiosByProveedor(@PathVariable("codigo") String codigo) {
        List<AcopioDataEntity> acopios = acopioDataService.obtenerAcopiosDeProveedor(codigo);
        if(acopios.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(acopios);
    }

    @GetMapping("/acopios/{codigo}/{fecha}")
    public ResponseEntity<List<AcopioDataEntity>> getAcopiosProveedorByDate(@PathVariable("codigo") String codigo,
                                                                               @PathVariable("fecha") String fecha) {
        List<AcopioDataEntity> acopios = acopioDataService.obtenerAcopiosProveedorPorFecha(codigo, fecha);
        if(acopios.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(acopios);
    }


    @PostMapping("/acopios/new")
    public String createAcopio(@RequestParam("fecha") String fecha,
                               @RequestParam("turno") String turno,
                               @RequestParam("proveedor") String proveedor,
                               @RequestParam("kls_leche") String kls_leche){

        acopioDataService.guardarAcopio(fecha, turno, proveedor, kls_leche);
        return "redirect:/acopios/new";
    }

    @GetMapping("/acopios/delete")
    public void deleteAcopios(){
        acopioDataService.deleteAcopios();
    }

}
