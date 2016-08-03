/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PagoDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.dao.SecuenciaDao;
import com.fpuna.py.travelware.dao.FacturaDao;
import com.fpuna.py.travelware.model.ComPagos;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.ComFacturas;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author damia_000
 */
@Named(value = "pagoBean")
@SessionScoped
public class PagoBean implements Serializable{
    private List<ComPagos> pagos;
    private List<ComFacturas> facturas;
    private List<PgeMonedas> monedas;
    
    private ComPagos pagoSelected;
    
    @EJB
    private PagoDao pagoEJB;
    @EJB
    private FacturaDao facturaEJB;
    @EJB
    private MonedaDao monedaEJB;
    @EJB
    private SecuenciaDao secuenciaEJB;
    
    private LoginBean loginBean;
    private String clave;
    
    private PgeMonedas moneda;
    private ComFacturas factura;
    
    //arreglos utilizados en la funcion convertir()
    private final String[] UNIDADES = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private final String[] DECENAS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
        "diecisiete ", "dieciocho ", "diecinueve", "veinte ", "treinta ", "cuarenta ",
        "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "};
    private final String[] CENTENAS = {"", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
        "setecientos ", "ochocientos ", "novecientos "};
    
    private boolean habilitado;

    //crea una nueva instancia de Pago
    public PagoBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clave = "pagos"; //nombre para secuencia
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.pagoSelected.setMonId(this.moneda);
        this.pagoSelected.setFacId(this.factura);
        this.pagos = pagoEJB.getAll();
        this.monedas = monedaEJB.getAll();
        this.facturas = facturaEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.pagoSelected = new ComPagos();
        Integer nro = secuenciaEJB.getSec(clave)+1;
        this.pagoSelected.setPgoNro(nro.toString());
        this.factura = new ComFacturas();
        this.moneda = new PgeMonedas();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.pagoSelected = new ComPagos();
        Integer nro = secuenciaEJB.getSec(clave)+1;
        this.pagoSelected.setPgoNro(nro.toString());
        habilitado = true;
    }
    
    public void buttonAction2(ComFacturas factura){
        this.pagoSelected = new ComPagos();
        Integer nro = secuenciaEJB.getSec(clave)+1;
        this.pagoSelected.setPgoNro(nro.toString());
        this.pagoSelected.setFacId(factura);
    }

    public void addPago(){
        FacesContext context = FacesContext.getCurrentInstance();

        //Validaciones de pagos de facturas
        if (this.pagoSelected.getPgoCambio().compareTo(BigDecimal.ZERO) < 0 || this.pagoSelected.getPgoMonto().compareTo(BigDecimal.ZERO) < 0 ) {
            context.addMessage(null, new FacesMessage("Advertencia. Monto de pago o tipo de cambio no pueden ser negativos. Verifique."));
            return;
        }
        if ( !this.pagoSelected.getMonId().equals(this.pagoSelected.getFacId().getMonId()) ) {
            context.addMessage(null, new FacesMessage("Advertencia. Moneda de pago debe ser igual a moneda de factura ("
                                                            +this.pagoSelected.getFacId().getMonId().getMonDesc()+"). Verifique."));
            return;
        }
        if ( this.pagoSelected.getPgoMonto().compareTo(this.pagoSelected.getFacId().getFacSaldo()) > 0 ) {
            context.addMessage(null, new FacesMessage("Advertencia. Monto de pago no puede ser mayor a saldo de la factura ("
                                                            +this.pagoSelected.getFacId().getFacSaldo()+"). Verifique."));
            return;
        }
        
        ComPagos pago = new ComPagos();
        if (this.pagoSelected.getPgoId()!= null){ //modificacion
            pago.setPgoId(this.pagoSelected.getPgoId());
            pago.setPgoUsuIns(this.pagoSelected.getPgoUsuIns());
            pago.setPgoFecIns(this.pagoSelected.getPgoFecIns());
            pago.setPgoUsuMod(loginBean.getUsername());
            pago.setPgoFecMod(new Date());
        }
        else
        {
            pago.setPgoUsuIns(loginBean.getUsername());
            pago.setPgoFecIns(new Date());
            //Actualizamos la secuencia de pago
            secuenciaEJB.actSec(this.pagoSelected.getPgoNro());
        }
        pago.setPgoCambio(this.pagoSelected.getPgoCambio());
        pago.setPgoForPago(this.pagoSelected.getPgoForPago());
        pago.setPgoMonto(this.pagoSelected.getPgoMonto());
        pago.setPgoMontoLetras(this.Convertir(this.pagoSelected.getPgoMonto().toString(), false));
        pago.setPgoNro(this.pagoSelected.getPgoNro());
        pago.setPgoObservacion(this.pagoSelected.getPgoObservacion());
        pago.setPgoTipo(this.pagoSelected.getPgoTipo());
        pago.setMonId(this.pagoSelected.getMonId());
        pago.setFacId(this.pagoSelected.getFacId());
        pago.setPgoAnulado('N');
        pagoEJB.update(pago);

        //Actualizamos el saldo y el estado de la factura
        BigDecimal nuevoSaldo = this.pagoSelected.getFacId().getFacSaldo().subtract(this.pagoSelected.getPgoMonto());
        this.pagoSelected.getFacId().setFacSaldo(nuevoSaldo);
        if(nuevoSaldo.compareTo(BigDecimal.ZERO)==0)
            this.pagoSelected.getFacId().setFacEst('P');
        facturaEJB.update(this.pagoSelected.getFacId());
        
        context.addMessage(null, new FacesMessage("Felicidades! El pago fue guardado con éxito"));
        pagos = pagoEJB.getAll();
        facturas = facturaEJB.getAll();
        RequestContext.getCurrentInstance().update("pagos-form:dtPagos");
        this.clean();
    }
    
    public void deletePago(){
        pagoEJB.delete(this.pagoSelected);
        pagos = pagoEJB.getAll();
        RequestContext.getCurrentInstance().update("pagos-form:dtPagos");
    }
    
    public void onRowSelect(SelectEvent event){
        this.pagoSelected = (ComPagos) event.getObject();
        RequestContext.getCurrentInstance().update("pagos-form:dtPagos");
    }

    public List<ComPagos> getPagos() {
        return pagos;
    }

    public void setPagos(List<ComPagos> pagos) {
        this.pagos = pagos;
    }

    public List<ComFacturas> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<ComFacturas> facturas) {
        this.facturas = facturas;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public ComPagos getPagoSelected() {
        return pagoSelected;
    }

    public void setPagoSelected(ComPagos pagoSelected) {
        this.pagoSelected = pagoSelected;
    }

    public PgeMonedas getMoneda() {
        return moneda;
    }

    public void setMoneda(PgeMonedas moneda) {
        this.moneda = moneda;
    }

    public ComFacturas getFactura() {
        return factura;
    }

    public void setFactura(ComFacturas factura) {
        this.factura = factura;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   

    public Integer getSecuencia(){
        return secuenciaEJB.getSec(this.clave)+1;
    }
    
     public String Convertir(String numero, boolean mayusculas) {
        String literal = "";
        String parte_decimal;    
        //si el numero utiliza (.) en lugar de (,) -> se reemplaza
        numero = numero.replace(".", ",");
        //si el numero no tiene parte decimal, se le agrega ,00
        if(!numero.contains(",")){
            numero = numero + ",00";
        }
        //se valida formato de entrada -> 0,00 y 999 999 999,00
        if (Pattern.matches("\\d{1,9},\\d{1,2}", numero)) {
            //se divide el numero 0000000,00 -> entero y decimal
            String Num[] = numero.split(",");            
            //de da formato al numero decimal
            parte_decimal = Num[1] + "/100 Guaraníes.";
            //se convierte el numero a literal
            if (Integer.parseInt(Num[0]) == 0) {//si el valor es cero
                literal = "cero ";
            } else if (Integer.parseInt(Num[0]) > 999999) {//si es millon
                literal = getMillones(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 999) {//si es miles
                literal = getMiles(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 99) {//si es centena
                literal = getCentenas(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 9) {//si es decena
                literal = getDecenas(Num[0]);
            } else {//sino unidades -> 9
                literal = getUnidades(Num[0]);
            }
            //devuelve el resultado en mayusculas o minusculas
            if (mayusculas) {
                return (literal + parte_decimal).toUpperCase();
            } else {
                return (literal + parte_decimal);
            }
        } else {//error, no se puede convertir
            return literal = null;
        }
    }

    /* funciones para convertir los numeros a literales */

    private String getUnidades(String numero) {// 1 - 9
        //si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9
        String num = numero.substring(numero.length() - 1);
        return UNIDADES[Integer.parseInt(num)];
    }

    private String getDecenas(String num) {// 99                        
        int n = Integer.parseInt(num);
        if (n < 10) {//para casos como -> 01 - 09
            return getUnidades(num);
        } else if (n > 19) {//para 20...99
            String u = getUnidades(num);
            if (u.equals("")) { //para 20,30,40,50,60,70,80,90
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8];
            } else {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8] + "y " + u;
            }
        } else {//numeros entre 11 y 19
            return DECENAS[n - 10];
        }
    }

    private String getCentenas(String num) {// 999 o 099
        if( Integer.parseInt(num)>99 ){//es centena
            if (Integer.parseInt(num) == 100) {//caso especial
                return " cien ";
            } else {
                 return CENTENAS[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            } 
        }else{//por Ej. 099 
            //se quita el 0 antes de convertir a decenas
            return getDecenas(Integer.parseInt(num)+"");            
        }        
    }

    private String getMiles(String numero) {// 999 999
        //obtiene las centenas
        String c = numero.substring(numero.length() - 3);
        //obtiene los miles
        String m = numero.substring(0, numero.length() - 3);
        String n="";
        //se comprueba que miles tenga valor entero
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);           
            return n + "mil " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }

    }

    private String getMillones(String numero) { //000 000 000        
        //se obtiene los miles
        String miles = numero.substring(numero.length() - 6);
        //se obtiene los millones
        String millon = numero.substring(0, numero.length() - 6);
        String n = "";
        if(millon.length()>1){
            n = getCentenas(millon) + "millones ";
        }else{
            n = getUnidades(millon) + "millon ";
        }
        return n + getMiles(miles);        
    }
}
