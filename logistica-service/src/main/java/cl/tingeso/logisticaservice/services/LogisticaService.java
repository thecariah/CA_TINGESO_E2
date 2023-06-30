package cl.tingeso.logisticaservice.services;

import cl.tingeso.logisticaservice.entities.LogisticaEntity;
import cl.tingeso.logisticaservice.models.AcopioModel;
import cl.tingeso.logisticaservice.models.GrasolModel;
import cl.tingeso.logisticaservice.models.ProveedorModel;
import cl.tingeso.logisticaservice.repositories.LogisticaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

@Service
public class LogisticaService {
    @Autowired
    LogisticaRepository logisticaRepository;

    @Autowired
    RestTemplate restTemplate;

    public ProveedorModel obtenerProveedorPorCodigo(String codigo){
        ProveedorModel proveedor = restTemplate.getForObject("http://proveedor-service/proveedores/" + codigo, ProveedorModel.class);
        System.out.println(proveedor);
        return proveedor;
    }

    public List<AcopioModel> obtenerTodosLosAcopios(){
        List<AcopioModel> acopios = restTemplate.getForObject("http://acopio-service/acopios/all", List.class);
        System.out.println(acopios);
        return acopios;
    }

    public List<AcopioModel> obtenerAcopiosDeProveedor(String codigo){
        List<AcopioModel> acopios = restTemplate.getForObject("http://acopio-service/acopios/" + codigo, List.class);
        System.out.println(acopios);
        return acopios;
    }

    public List<AcopioModel> obtenerAcopiosProveedorPorFecha(String codigo, String fecha){
        try{
            List<AcopioModel> acopios = restTemplate.getForObject("http://acopio-service/acopios/" + codigo + "/" + fecha, List.class);
            System.out.println(acopios);
            return acopios;
        }
        catch (HttpClientErrorException ex){
            List<AcopioModel> acopios = null;
            System.out.println(acopios);
            return acopios;
        }
    }

    public GrasolModel obtenerGrasolDeProveedor(String codigo){
        GrasolModel grasol = restTemplate.getForObject("http://grasol-service/grasol/" + codigo, GrasolModel.class);
        System.out.println(grasol);
        return grasol;
    }

    public List<String> obtenerCodigoProveedores(){
        List<ProveedorModel> proveedores = restTemplate.getForObject("http://proveedor-service/proveedores/all", List.class);
        List<String> codigos_proveedores = new ArrayList<>();
        for (ProveedorModel proveedor_actual : proveedores) {
            codigos_proveedores.add(proveedor_actual.getCodigo());
        }

        return codigos_proveedores;
    }

    public void reportePlanilla() throws ParseException{
        List<String> proveedores = obtenerCodigoProveedores();
        LocalDate quincena = determinarQuincena();

        for (String codigo : proveedores) {
            calculoPlanilla(codigo, quincena);
        }
    }

    public void calculoPlanilla(String codigo, LocalDate quincena) throws ParseException {
        ProveedorModel proveedor = obtenerProveedorPorCodigo(codigo);
        GrasolModel grasol = obtenerGrasolDeProveedor(codigo);
        LogisticaEntity reporte = new LogisticaEntity();

        reporte.setCodigo(proveedor.getCodigo());
        reporte.setNombre(proveedor.getNombre());
        reporte.setQuincena(quincena.getYear() + "/" + quincena.getMonthValue() + "/" + quincena.getDayOfMonth());

        // [Total kls leche, Nro. dias que envio leche, Promedio diario KLS leche]
        List<Double> resultados = realizarCalculos(proveedor.getCodigo(), quincena);

        reporte.setTotal_leche(resultados.get(0).intValue());
        reporte.setDias_envio_leche(resultados.get(1).intValue());
        reporte.setProm_diario_leche(resultados.get(2));

        reporte.setPtj_grasa(Integer.valueOf(grasol.getPtj_grasa()));
        reporte.setPtj_solidos(Integer.valueOf(grasol.getPtj_solido_total()));

        //reporte.setPtj_var_grasa();

        logisticaRepository.save(reporte);
    }

    public LocalDate convertirEnFecha(String fecha_string){
        LocalDate fecha = LocalDate.parse(fecha_string);
        return fecha;
    }

    public LocalDate determinarQuincena(){
        List<AcopioModel> acopios = obtenerTodosLosAcopios();

        // ultimo acopio de la lista representa los acopios alamcenados en BD del archivo recien subido
        AcopioModel acopioEjemplo = acopios.get(acopios.size() - 1);

        LocalDate fecha = convertirEnFecha(acopioEjemplo.getFecha());

        if (fecha.getDayOfMonth() > 15){
            // entonces: segunda quincena
            LocalDate quincena = fecha.withDayOfMonth(2);
            return quincena;
        }
        else{
            // entonces: primera quincena
            LocalDate quincena = fecha.withDayOfMonth(1);
            return quincena;
        }
    }

    public List<Double> realizarCalculos(String codigo, LocalDate quincena){
        List<Integer> diasLeche = new ArrayList<>();
        LocalDate fecha_inicio = quincena;

        if (quincena.getDayOfMonth() == 2){
            //si es la segunda quincena del mes, entonces se inicia del dia 16
            fecha_inicio = quincena.withDayOfMonth(16);
        }

        while(fecha_inicio.getMonth().equals(quincena.getMonth())){
            if (quincena.getDayOfMonth() == 1 && fecha_inicio.getDayOfMonth() > 15){
                // si estamos en la primera quincena, entonces el loop debe terminar en la segunda quincena = dia 16 del mes
                break;
            }

            List<AcopioModel> acopios = obtenerAcopiosProveedorPorFecha(codigo, fecha_inicio.toString());

            if (acopios.isEmpty()){
                // durante ese dia no hubo acopio
                diasLeche.add(0);
            }
            else{
                // ese dia si hubo acopios
                Integer leche_del_dia = 0;
                for (AcopioModel acopio : acopios) {
                    leche_del_dia += Integer.valueOf(acopio.getKls_leche());
                }
                diasLeche.add(leche_del_dia);
            }

            fecha_inicio = fecha_inicio.plusDays(1);
        }

        // [Total kls leche, Nro. dias que envio leche, Promedio diario KLS leche]
        List<Double> resultados = new ArrayList<>();
        resultados.add(sumarElementosLista(diasLeche));
        resultados.add(contarDiasConLeche(diasLeche));
        resultados.add(calcularPromedioLista(diasLeche));

        return resultados;
    }

    public double sumarElementosLista(List<Integer> lista){
        Integer suma = 0;

        for (Integer num : lista) {
            suma += num;
        }

        return (double) suma;
    }

    public double contarDiasConLeche(List<Integer> lista_dias_leche){
        Integer contador = 0;
        for (Integer dia : lista_dias_leche) {
            if (dia != 0){
                contador += 1;
            }
        }
        return (double) contador;
    }

    public double calcularPromedioLista(List<Integer> lista) {
        double promedio = sumarElementosLista(lista) / lista.size();
        return promedio;
    }




}
