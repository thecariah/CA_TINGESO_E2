package cl.tingeso.logisticaservice.services;

import cl.tingeso.logisticaservice.entities.LogisticaEntity;
import cl.tingeso.logisticaservice.models.AcopioModel;
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

    public void calculoPlanilla(String codigo) throws ParseException {
        ProveedorModel proveedor = obtenerProveedorPorCodigo(codigo);
        LogisticaEntity reporte = new LogisticaEntity();
        LocalDate quincena = determinarQuincena(proveedor.getCodigo());

        reporte.setCodigo(proveedor.getCodigo());
        reporte.setNombre(proveedor.getNombre());
        reporte.setQuincena(quincena.getYear() + "/" + quincena.getMonthValue() + "/" + quincena.getDayOfMonth());

        reporte.setTotal_leche(obtenerTotalLeche(proveedor.getCodigo()));
        reporte.setDias_envio_leche(obtenerTotalDiasDeEnvioLeche(proveedor.getCodigo()));
        reporte.setProm_diario_leche();



    }

    public LocalDate determinarQuincena(String codigo){
        List<AcopioModel> acopios = obtenerAcopiosDeProveedor(codigo);
        AcopioModel acopioEjemplo = acopios.get(0);

        LocalDate fecha = convertirEnFecha(acopioEjemplo.getFecha());

        if (fecha.getDayOfMonth() > 15){
            // entonces: segunda quincena
            LocalDate quincena = fecha.withDayOfMonth(2);
            return quincena
        }
        else{
            // entonces: primera quincena
            LocalDate quincena = fecha.withDayOfMonth(1);
            return quincena
        }
    }

    public LocalDate convertirEnFecha(String fecha_string){
        LocalDate fecha = LocalDate.parse(fecha_string);
        return fecha;
    }

    public Integer obtenerTotalLeche(String codigo) throws ParseException{
        List<AcopioModel> acopios = obtenerAcopiosDeProveedor(codigo);
        Integer total_leche = 0;

        for (AcopioModel acopio : acopios) {
            total_leche += Integer.parseInt(acopio.getKls_leche());
        }

        return total_leche;
    }

    public Integer obtenerTotalDiasDeEnvioLeche(String codigo){
        List<AcopioModel> acopios = obtenerAcopiosDeProveedor(codigo);
        Set<String> fechas = new HashSet<>(); // hashset elimina duplicados

        for (AcopioModel acopio : acopios) {
            fechas.add(acopio.getFecha());
        }

        Integer dias_envio_leche = fechas.size();
        return dias_envio_leche;
    }

    public double calcularPromedioDiarioLeche(String codigo, LocalDate quincena){
        List<Integer> diasLeche = new ArrayList<>();
        LocalDate fecha_inicio = quincena;

        if (quincena.getDayOfMonth() == 2){
            //si es la segunda quincena del mes, entonces se parte del dia 16
            fecha_inicio = quincena.withDayOfMonth(16);
        }

        while(fecha_inicio.getMonth().equals(quincena.getMonth())){
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


    }




}
