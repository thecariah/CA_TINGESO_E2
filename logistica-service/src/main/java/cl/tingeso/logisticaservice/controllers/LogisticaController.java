package cl.tingeso.logisticaservice.controllers;

import cl.tingeso.logisticaservice.entities.LogisticaEntity;
import cl.tingeso.logisticaservice.services.LogisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/logistica")
public class LogisticaController {
    @Autowired
    LogisticaService logisticaService;

    @GetMapping
    public ResponseEntity<List<LogisticaEntity>> getPlanillas() throws ParseException {
        //logisticaService.reportePlanilla();
        List<LogisticaEntity> reportePlanillas = logisticaService.obtenerPlanillas();

        if (reportePlanillas.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reportePlanillas);
    }
}
