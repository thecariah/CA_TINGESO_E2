package cl.tingeso.grasolservice.controllers;

import cl.tingeso.grasolservice.entities.GrasolDataEntity;
import cl.tingeso.grasolservice.services.GrasolDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grasol")
public class GrasolDataController {
    @Autowired
    GrasolDataService grasolDataService;

    @GetMapping("/all")
    public ResponseEntity<List<GrasolDataEntity>> getAllGrasol(){
        List<GrasolDataEntity> grasoles = grasolDataService.obtenerGrasoles();
        if(grasoles.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(grasoles);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<List<GrasolDataEntity>> getGrasolByProveedor(@PathVariable("codigo") String codigo) {
        List<GrasolDataEntity> grasoles = grasolDataService.obtenerGrasolesDeProveedor(codigo);

        if(grasoles.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(grasoles);
    }

    @PostMapping("/new")
    public String createGrasol(@RequestParam("proveedor") String proveedor,
                               @RequestParam("ptj_grasa") String ptj_grasa,
                               @RequestParam("ptj_solido_total") String ptj_solido_total){

        grasolDataService.guardarGrasol(proveedor, ptj_grasa, ptj_solido_total);
        return "redirect:/grasol/new";
    }

    @GetMapping("/delete")
    public void deleteGrasol(){
        grasolDataService.deleteGrasol();
    }

}
