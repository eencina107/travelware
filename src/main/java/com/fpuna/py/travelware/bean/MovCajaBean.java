/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.MovCajaDao;
import com.fpuna.py.travelware.dao.MonedaDao;
//import com.fpuna.py.travelware.dao.SecuenciaDao;
import com.fpuna.py.travelware.dao.CajaChicaDao;
import com.fpuna.py.travelware.dao.FacturaDao;
import com.fpuna.py.travelware.dao.FacturaDetDao;
import com.fpuna.py.travelware.model.ComMovCajas;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.ComCajas;
import com.fpuna.py.travelware.model.ComFacturas;
//import com.fpuna.py.travelware.model.ComFacturasDet;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
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
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author damia_000
 */
@Named(value = "movCajaBean")
@SessionScoped
public class MovCajaBean implements Serializable{
    private List<ComMovCajas> movCajas;
    private List<ComCajas> cajas;
    private List<PgeMonedas> monedas;
    
    private ComMovCajas movCajaSelected;
    
    @EJB
    private MovCajaDao movCajaEJB;
    @EJB
    private CajaChicaDao cajaEJB;
    @EJB
    private MonedaDao monedaEJB;
    @EJB
    private FacturaDao facturaEJB;
    @EJB
    private FacturaDetDao facturaDetDAO;
    //@EJB
    //private SecuenciaDao secuenciaEJB;
    
    private LoginBean loginBean;
    //private String clave;
    
    private PgeMonedas moneda;
    private ComCajas caja;
    
    //arreglos utilizados en la funcion convertir()
    private final String[] UNIDADES = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private final String[] DECENAS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
        "diecisiete ", "dieciocho ", "diecinueve", "veinte ", "treinta ", "cuarenta ",
        "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "};
    private final String[] CENTENAS = {"", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
        "setecientos ", "ochocientos ", "novecientos "};
    
    private boolean habilitado;

    //crea una nueva instancia de MovCaja
    public MovCajaBean(){
        
    }
    
    @PostConstruct
    public void init(){
        //this.clave = "movCajas"; //nombre para secuencia
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.movCajaSelected.setMonId(this.moneda);
        this.movCajaSelected.setCajId(this.caja);
        this.movCajas = movCajaEJB.getAll();
        this.monedas = monedaEJB.getAll();
        this.cajas = cajaEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.movCajaSelected = new ComMovCajas();
        //Integer nro = secuenciaEJB.getSec(clave)+1;
        //this.movCajaSelected.setMovNro(nro.toString());
        this.caja = new ComCajas();
        this.moneda = new PgeMonedas();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.movCajaSelected = new ComMovCajas();
        //Integer nro = secuenciaEJB.getSec(clave)+1;
        //this.movCajaSelected.setMovNro(nro.toString());
    }
    
    public void addMovCaja(){
        FacesContext context = FacesContext.getCurrentInstance();

        //Obtenemos el nuevo saldo de caja chica
        BigDecimal nuevoSaldo = getNuevoSaldo("Add");

        //Validaciones de movimientos de caja chica
        if (this.movCajaSelected.getMovCambio().compareTo(BigDecimal.ZERO) < 0 || this.movCajaSelected.getMovMonto().compareTo(BigDecimal.ZERO) < 0 ) {
            context.addMessage(null, new FacesMessage("Advertencia. Monto de movimiento o tipo de cambio no pueden ser negativos. Verifique."));
            return;
        }
        if ( !this.movCajaSelected.getMonId().equals(this.movCajaSelected.getCajId().getMonId()) ) {
            context.addMessage(null, new FacesMessage("Advertencia. Moneda del movimiento debe ser igual a moneda de la caja chica ("
                                                            +this.movCajaSelected.getCajId().getMonId().getMonDesc()+"). Verifique."));
            return;
        }
        if ( nuevoSaldo.compareTo(BigDecimal.ZERO) < 0 ) {
            context.addMessage(null, new FacesMessage("Advertencia. Saldo de la caja chica (" +this.movCajaSelected.getCajId().getCajSaldo()
                                                            +") insuficiente para el movimiento. Verifique."));
            return;
        }
        if ( nuevoSaldo.compareTo(this.movCajaSelected.getCajId().getCajLim()) > 0 ) {
            context.addMessage(null, new FacesMessage("Advertencia. Saldo no puede superar al límite de la caja chica ("
                                                            +this.movCajaSelected.getCajId().getCajLim()+"). Verifique."));
            return;
        }
        
        ComMovCajas movCaja = new ComMovCajas();
        if (this.movCajaSelected.getMovId()!= null){ //modificacion --> No esta habilitado
            movCaja.setMovId(this.movCajaSelected.getMovId());
            movCaja.setMovUsuIns(this.movCajaSelected.getMovUsuIns());
            movCaja.setMovFecIns(this.movCajaSelected.getMovFecIns());
            movCaja.setMovUsuMod(loginBean.getUsername());
            movCaja.setMovFecMod(new Date());
        }
        else
        {
            movCaja.setMovUsuIns(loginBean.getUsername());
            movCaja.setMovFecIns(new Date());
        }
        movCaja.setMovCambio(this.movCajaSelected.getMovCambio());
        movCaja.setMovForPago(this.movCajaSelected.getMovForPago());
        movCaja.setMovMonto(this.movCajaSelected.getMovMonto());
        movCaja.setMovMontoLetras(this.Convertir(this.movCajaSelected.getMovMonto().toString(), false));
        movCaja.setMovObservacion(this.movCajaSelected.getMovObservacion());
        movCaja.setMovTip(this.movCajaSelected.getMovTip());
        movCaja.setMonId(this.movCajaSelected.getMonId());
        movCaja.setCajId(this.movCajaSelected.getCajId());
        movCaja.setMovAnulado('N');
        movCajaEJB.update(movCaja);

        //Actualizamos el saldo de la caja chica
        this.movCajaSelected.getCajId().setCajSaldo(nuevoSaldo);
        cajaEJB.update(this.movCajaSelected.getCajId());
        
        //Insertamos la factura compra por la reposición
        if(movCaja.getMovTip().equals('C')) {
            ComFacturas factura = new ComFacturas();
//            factura.setProId(this.facturaSelected.getProId());
            factura.setFacNro("999-999-9999999");
            factura.setFacFecha(DateUtils.truncate(new Date(), Calendar.DATE));
            factura.setMonId(movCaja.getMonId());
            factura.setFacCambio(movCaja.getMovCambio());
            factura.setFacTotal(movCaja.getMovMonto());
            factura.setFacDesc("Reposición de Caja Chica");
            factura.setFacEst('P');
//            factura.setFacNroTim(this.facturaSelected.getFacNroTim());
//            factura.setFacFecVen(this.facturaSelected.getFacFecVen());
            factura.setFacCond('C');
//            factura.setFacRuc(this.facturaSelected.getFacRuc());
            factura.setFacCanCuo(0);
            factura.setFacSaldo(BigDecimal.ZERO);

            factura.setFacUsuIns(loginBean.getUsername());
            factura.setFacFecIns(new Date());
        }

        context.addMessage(null, new FacesMessage("Felicidades! El movimiento de caja chica fue guardado con éxito"));
        movCajas = movCajaEJB.getAll();
        cajas = cajaEJB.getAll();
        RequestContext.getCurrentInstance().update("movCaja-form:dtMovCaja");
        this.clean();
    }
    
    public void deleteMovCaja(){
        //Obtenemos el nuevo saldo de caja chica
        BigDecimal nuevoSaldo = getNuevoSaldo("Del");

        //Actualizamos el saldo de la caja chica
        this.movCajaSelected.getCajId().setCajSaldo(nuevoSaldo);
        cajaEJB.update(this.movCajaSelected.getCajId());

        movCajaEJB.delete(this.movCajaSelected);
        movCajas = movCajaEJB.getAll();
        RequestContext.getCurrentInstance().update("movCaja-form:dtMovCaja");
    }
    
    public void onRowSelect(SelectEvent event){
        this.movCajaSelected = (ComMovCajas) event.getObject();
        RequestContext.getCurrentInstance().update("movCaja-form:dtMovCaja");
    }

    public List<ComMovCajas> getMovCajas() {
        return movCajas;
    }

    public void setMovCajas(List<ComMovCajas> movCajas) {
        this.movCajas = movCajas;
    }

    public List<ComCajas> getCajas() {
        return cajas;
    }

    public void setCajas(List<ComCajas> cajas) {
        this.cajas = cajas;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public ComMovCajas getMovCajaSelected() {
        return movCajaSelected;
    }

    public void setMovCajaSelected(ComMovCajas movCajaSelected) {
        this.movCajaSelected = movCajaSelected;
    }

    public PgeMonedas getMoneda() {
        return moneda;
    }

    public void setMoneda(PgeMonedas moneda) {
        this.moneda = moneda;
    }

    public ComCajas getCajtura() {
        return caja;
    }

    public void setCaja(ComCajas caja) {
        this.caja = caja;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   

    /*public Integer getSecuencia(){
        return secuenciaEJB.getSec(this.clave)+1;
    }*/
    
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
    
    private BigDecimal getNuevoSaldo(String accion) {
        BigDecimal nuevoSaldo = this.movCajaSelected.getCajId().getCajSaldo();

        if( (this.movCajaSelected.getMovTip().equals('D') && accion.equals("Add")) ||
            (this.movCajaSelected.getMovTip().equals('C') && accion.equals("Del")) ) //débito --> corresponde a un egreso
            nuevoSaldo= nuevoSaldo.subtract(this.movCajaSelected.getMovMonto());
        else if( (this.movCajaSelected.getMovTip().equals('C') && accion.equals("Add")) ||
            (this.movCajaSelected.getMovTip().equals('D') && accion.equals("Del")) ) //crédito --> corresponde a una reposición
            nuevoSaldo= nuevoSaldo.add(this.movCajaSelected.getMovMonto());
 
        return nuevoSaldo;
    }
}
