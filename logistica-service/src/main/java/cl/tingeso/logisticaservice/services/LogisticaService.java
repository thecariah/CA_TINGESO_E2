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
import java.time.temporal.TemporalAdjusters;
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

    public List<GrasolModel> obtenerGrasolesDeProveedor(String codigo){
        List<GrasolModel> grasoles = restTemplate.getForObject("http://grasol-service/grasol/" + codigo, List.class);
        System.out.println(grasoles);
        return grasoles;
    }

    public List<String> obtenerCodigoProveedores(){
        List<ProveedorModel> proveedores = restTemplate.getForObject("http://proveedor-service/proveedores/all", List.class);
        List<String> codigos_proveedores = new ArrayList<>();
        for (ProveedorModel proveedor_actual : proveedores) {
            codigos_proveedores.add(proveedor_actual.getCodigo());
        }

        return codigos_proveedores;
    }

    public List<LogisticaEntity> obtenerPlanillas(){
        return logisticaRepository.findAll();
    }

    public List<LogisticaEntity> obtenerPlanillasDeProveedor(String codigo){
        return this.logisticaRepository.findPlanillasByProveedor(codigo);
    }

    public LogisticaEntity obtenerPlanillaProveedorSegunQuincena(String codigo, String quincena){
        return this.logisticaRepository.findPlanillaProveedorByQuincena(codigo, quincena);
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
        List<GrasolModel> grasolesProveedor = obtenerGrasolesDeProveedor(codigo);
        LogisticaEntity reporte = new LogisticaEntity();

        reporte.setCodigo(proveedor.getCodigo());
        reporte.setNombre(proveedor.getNombre());
        reporte.setQuincena(quincena.getYear() + "/" + quincena.getMonthValue() + "/" + quincena.getDayOfMonth());

        // [Total kls leche, Nro. dias que envio leche, Promedio diario KLS leche, Frecuencia leche]
        List<Double> resultadosLeche = realizarCalculosLeche(proveedor.getCodigo(), quincena);

        reporte.setTotal_leche(resultadosLeche.get(0));
        reporte.setDias_envio_leche(resultadosLeche.get(1).intValue());
        reporte.setProm_diario_leche(resultadosLeche.get(2));
        reporte.setPtj_grasa(Double.valueOf(grasolesProveedor.get(grasolesProveedor.size() - 1).getPtj_grasa()));
        reporte.setPtj_solidos(Double.valueOf(grasolesProveedor.get(grasolesProveedor.size() - 1).getPtj_solido_total()));

        LocalDate quincena_anterior = determinarQuincenaAnterior(quincena);
        String quincena_string = quincena_anterior.getYear() + "/" + quincena_anterior.getMonthValue() + "/" + quincena_anterior.getDayOfMonth();

        // [%variacion grasa, %variacion solidos totales, %variacion leche]
        List<Double> porcentajes = determinarPorcentajes(grasolesProveedor, reporte.getTotal_leche(), codigo, quincena_string);

        reporte.setPtj_var_grasa(porcentajes.get(0));
        reporte.setPtj_var_solidos(porcentajes.get(1));
        reporte.setPtj_var_leche(porcentajes.get(2));

        reporte.setPago_leche(calcularPagoLeche(proveedor.getCategoria(), reporte.getTotal_leche()));
        reporte.setPago_grasa(calcularPagoGrasa(reporte.getPtj_grasa()));
        reporte.setPago_solidos(calcularPagoSolidosTotales(reporte.getPtj_solidos()));

        reporte.setBonificacion(calcularBonificacion(resultadosLeche.get(3), reporte.getPago_leche()));

        double pago_acopio_leche = reporte.getPago_leche() + reporte.getPago_grasa() + reporte.getPago_solidos() + reporte.getBonificacion();

        reporte.setDcto_var_leche(calcularDctoLeche(pago_acopio_leche, reporte.getPtj_var_leche()));
        reporte.setDcto_var_grasa(calcularDctoGrasa(pago_acopio_leche, reporte.getPtj_var_grasa()));
        reporte.setDcto_var_solidos(calcularDctoSolidos(pago_acopio_leche, reporte.getPtj_var_solidos()));

        double descuentos = reporte.getDcto_var_leche() + reporte.getDcto_var_grasa() + reporte.getDcto_var_solidos();

        reporte.setPago_total(pago_acopio_leche - descuentos);

        // valor retencion:
        if (proveedor.getRetencion().equals("Si") && reporte.getPago_total() > 950000){
            reporte.setMonto_retencion((13 / 100) * reporte.getPago_total());
        }
        else{
            reporte.setMonto_retencion(0);
        }

        reporte.setMonto_final(reporte.getPago_total() - reporte.getMonto_retencion());

        logisticaRepository.save(reporte);
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

    public LocalDate determinarQuincenaAnterior(LocalDate quincena_actual){
        if (quincena_actual.getDayOfMonth() == 2){
            LocalDate quincena_anterior = quincena_actual.withDayOfMonth(1);
            return quincena_anterior;
        }
        else{
            LocalDate mes_anterior = quincena_actual.minusMonths(1);
            LocalDate quincena_anterior = mes_anterior.withDayOfMonth(2);
            return quincena_anterior;
        }
    }

    public double  calcularVariacionNegativa(double primerValor, double segundoValor){
        return ((((segundoValor - primerValor)/ primerValor)*100)* (-1));
    }

    public List<Double> determinarPorcentajes(List<GrasolModel> grasoles, double total_leche_nuevo, String codigo, String quincena){
        LogisticaEntity planillaAnt = obtenerPlanillaProveedorSegunQuincena(codigo, quincena);
        List<Double> porcentajes = new ArrayList<>();

        if (grasoles.size() >= 2){
            // si hay datos de quincenas anteriores del proveedor:
            GrasolModel grasolActual = grasoles.get(grasoles.size() - 1);
            GrasolModel grasolAnterior = grasoles.get(grasoles.size() - 2);

            // [%variacion grasa, %variacion solidos totales, %variacion leche]
            porcentajes.add(calcularVariacionNegativa(Double.valueOf(grasolAnterior.getPtj_grasa()), Double.valueOf(grasolActual.getPtj_grasa())));
            porcentajes.add(calcularVariacionNegativa(Double.valueOf(grasolAnterior.getPtj_solido_total()), Double.valueOf(grasolActual.getPtj_solido_total())));
            porcentajes.add(calcularVariacionNegativa(planillaAnt.getTotal_leche(), total_leche_nuevo));
        }
        else{
            // no hay datos de quincenas anteriores del proveedor:
            porcentajes.add(0.0);
            porcentajes.add(0.0);
            porcentajes.add(0.0);
        }

        return porcentajes;
    }

    public List<Double> realizarCalculosLeche(String codigo, LocalDate quincena){
        List<Double> diasLeche = new ArrayList<>();
        LocalDate fecha_inicio = quincena;

        Integer contMyT = 0;
        Integer contM = 0;
        Integer contT = 0;

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
                diasLeche.add(0.0);
            }
            else{
                // ese dia si hubo acopios

                // guardamos kls de leche del dia
                double leche_del_dia = 0.0;

                for (AcopioModel acopio : acopios) {
                    leche_del_dia += Double.valueOf(acopio.getKls_leche());
                }

                diasLeche.add(leche_del_dia);


                // ademas calculamos frecuencia de kls de leche
                if (acopios.size() == 2){
                    // si hay dos acopios en un dia significa que hubo envio mañana y tarde
                    contMyT += 1;
                }
                else{
                    String turno = acopios.get(0).getTurno();
                    if (turno.equals("M")){
                        contM += 1;
                    }
                    else{
                        contT += 1;
                    }
                }
            }

            fecha_inicio = fecha_inicio.plusDays(1);
        }

        // [Total kls leche, Nro. dias que envio leche, Promedio diario KLS leche, Frecuencia leche]
        List<Double> resultados = new ArrayList<>();
        resultados.add(sumarElementosLista(diasLeche));
        resultados.add(contarDiasConLeche(diasLeche));
        resultados.add(calcularPromedioLista(diasLeche));
        resultados.add((double) determinarFrecuencia(contMyT,contM,contT));

        return resultados;
    }

    public double calcularDctoLeche(double pago_acopio_leche, double ptj_var_leche){
        double descuento = 0.0;

        if (ptj_var_leche >= 0 && ptj_var_leche < 9){
            // 0 – 8%
            descuento = 0.0;
        }
        else if (ptj_var_leche >= 9 && ptj_var_leche < 26){
            // 9% - 25%
            descuento = (7 / 100) * pago_acopio_leche;
        }
        else if (ptj_var_leche >= 26 && ptj_var_leche < 46){
            // 26% - 45%
            descuento = (15 / 100) * pago_acopio_leche;
        }
        else{
            // 46% - más
            descuento = (30 / 100) * pago_acopio_leche;
        }

        return descuento;
    }

    public double calcularDctoGrasa(double pago_acopio_leche, double ptj_var_grasa){
        double descuento = 0.0;

        if (ptj_var_grasa >= 0 && ptj_var_grasa < 16){
            // 0 – 15%
            descuento = 0.0;
        }
        else if (ptj_var_grasa >= 16 && ptj_var_grasa < 26){
            // 16% - 25%
            descuento = (12 / 100) * pago_acopio_leche;
        }
        else if (ptj_var_grasa >= 26 && ptj_var_grasa < 41){
            // 26% - 40%
            descuento = (20 / 100) * pago_acopio_leche;
        }
        else{
            // 41% - más
            descuento = (30 / 100) * pago_acopio_leche;
        }

        return descuento;
    }

    public double calcularDctoSolidos(double pago_acopio_leche, double ptj_var_solidos){
        double descuento = 0.0;

        if (ptj_var_solidos >= 0 && ptj_var_solidos < 7){
            // 0 – 6%
            descuento = 0.0;
        }
        else if (ptj_var_solidos >= 7 && ptj_var_solidos < 13){
            // 7% - 12%
            descuento = (18 / 100) * pago_acopio_leche;
        }
        else if (ptj_var_solidos >= 13 && ptj_var_solidos < 36){
            // 13% - 35%
            descuento = (27 / 100) * pago_acopio_leche;
        }
        else{
            // 36% - más
            descuento = (45 / 100) * pago_acopio_leche;
        }

        return descuento;
    }

    public double calcularPagoLeche(String categoria, double total_leche){
        double pago = 0.0;

        if (categoria.equals("A")){
            pago = total_leche * 700;
        }
        else if (categoria.equals("B")){
            pago = total_leche * 550;
        }
        else if (categoria.equals("C")){
            pago = total_leche * 400;
        }
        else{
            // si es "D":
            pago = total_leche * 250;
        }

        return pago;
    }

    public double calcularPagoGrasa(double ptj_grasa){
        double pago = 0.0;

        if (ptj_grasa >= 0 && ptj_grasa < 21){
            // 0 – 20%:
            pago = ptj_grasa * 30;
        }
        else if (ptj_grasa >= 21 && ptj_grasa < 46){
            // 21% - 45%:
            pago = ptj_grasa * 80;
        }
        else{
            // 46% - más:
            pago = ptj_grasa * 120;
        }

        return pago;
    }

    public double calcularPagoSolidosTotales(double ptj_solidos){
        double pago = 0.0;

        if (ptj_solidos >= 0 && ptj_solidos < 8){
            // 0 – 7%:
            pago = ptj_solidos * (-130);
        }
        else if (ptj_solidos >= 8 && ptj_solidos < 19){
            // 8% - 18%:
            pago = ptj_solidos * (-90);
        }
        else if (ptj_solidos >= 19 && ptj_solidos < 36){
            // 19% - 35%:
            pago = ptj_solidos * 95;
        }
        else{
            // 36% - más:
            pago = ptj_solidos * 150;
        }

        return pago;
    }

    public double calcularBonificacion(double frecuencia, double pagoLeche){
        double pagoBonificacion = (frecuencia / 100) * pagoLeche;
        return pagoBonificacion;
    }

    public Integer determinarFrecuencia(Integer contMyT, Integer contM, Integer contT){
        Integer frecuencia = 0;

        if (contMyT > 10){
            frecuencia = 20;
        }
        else if (contM > 10){
            frecuencia = 12;
        }
        else if (contT > 10){
            frecuencia = 8;
        }
        else{
            // no hay ninguno que tenga una frecuecia mayor de 10 dias:
            frecuencia = 0;
        }

        return frecuencia;
    }



    public double contarDiasConLeche(List<Double> lista_dias_leche){
        Integer contador = 0;
        for (double dia : lista_dias_leche) {
            if (dia != 0.0){
                contador += 1;
            }
        }
        return (double) contador;
    }

    public LocalDate convertirEnFecha(String fecha_string){
        LocalDate fecha = LocalDate.parse(fecha_string);
        return fecha;
    }

    public double sumarElementosLista(List<Double> lista){
        double suma = 0.0;

        for (double num : lista) {
            suma += num;
        }

        return suma;
    }

    public double calcularPromedioLista(List<Double> lista) {
        double promedio = sumarElementosLista(lista) / (double) lista.size();
        return promedio;
    }

}
