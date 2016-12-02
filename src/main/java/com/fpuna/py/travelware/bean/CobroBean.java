/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.CobroDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.SecuenciaDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.PagCobros;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "cobroBean")
@ViewScoped
public class CobroBean implements Serializable{
    private List<PagCobros> cobros;
    private List<PgePersonas> personas;
    private List<ViaViajes> viajes;
    private List<PgeMonedas> monedas;
    
    private PagCobros cobroSelected;
    
    @EJB
    private CobroDao cobroEJB;
    @EJB
    private PersonaDao personaEJB;
    @EJB
    private ViajeDao viajeEJB;
    @EJB
    private MonedaDao monedaEJB;
    @EJB
    private SecuenciaDao secuenciaEJB;
    
    private LoginBean loginBean;
    private String clave;
    
    private PgePersonas persona;
    private PgeMonedas moneda;
    private ViaViajes viaje;
    
    //arreglos utilizados en la funcion convertir()
    private final String[] UNIDADES = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private final String[] DECENAS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
        "diecisiete ", "dieciocho ", "diecinueve", "veinte ", "treinta ", "cuarenta ",
        "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "};
    private final String[] CENTENAS = {"", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
        "setecientos ", "ochocientos ", "novecientos "};
    
    private boolean habilitado;

    //crea una nueva instancia de Cobro
    public CobroBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clave = "cobros"; //nombre para secuencia
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.cobroSelected.setPerId(this.persona);
        this.cobroSelected.setMonId(this.moneda);
        this.cobroSelected.setViaId(this.viaje);
        this.cobros = cobroEJB.getAll();
        this.monedas = monedaEJB.getAll();
        this.personas = personaEJB.getAll();
        this.viajes = viajeEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.cobroSelected = new PagCobros();
        Integer nro = secuenciaEJB.getSec(clave)+1;
        this.cobroSelected.setCobNro(nro.toString());
        this.persona = new PgePersonas();
        this.viaje = new ViaViajes();
        this.moneda = new PgeMonedas();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.cobroSelected = new PagCobros();
        Integer nro = secuenciaEJB.getSec(clave)+1;
        this.cobroSelected.setCobNro(nro.toString());
        habilitado = true;
    }
    
    public void addCobro(){
        FacesContext context = FacesContext.getCurrentInstance();
        
        PagCobros cobro = new PagCobros();
        if (this.cobroSelected.getCobId()!= null){ //modificacion
            cobro.setCobId(this.cobroSelected.getCobId());
            cobro.setCobUsuIns(this.cobroSelected.getCobUsuIns());
            cobro.setCobFecIns(this.cobroSelected.getCobFecIns());
            cobro.setCobUsuMod(loginBean.getUsername());
            cobro.setCobFecMod(new Date());
        }
        else
        {
            cobro.setCobUsuIns(loginBean.getUsername());
            cobro.setCobFecIns(new Date());
        }
        cobro.setCobCambio(this.cobroSelected.getCobCambio());
        cobro.setCobForPago(this.cobroSelected.getCobForPago());
        cobro.setCobMonto(this.cobroSelected.getCobMonto());
        cobro.setCobMontoLetras(this.Convertir(this.cobroSelected.getCobMonto().toString(), false));
        cobro.setCobNro(this.cobroSelected.getCobNro());
        cobro.setCobObservacion(this.cobroSelected.getCobObservacion());
        cobro.setCobTipo(this.cobroSelected.getCobTipo());
        cobro.setMonId(this.cobroSelected.getMonId());
        cobro.setViaId(this.cobroSelected.getViaId());
        cobro.setCobAnulado('N');
        cobro.setPerId(persona);
        cobroEJB.update(cobro);
        context.addMessage(null, new FacesMessage("Felicidades! El cobro fue guardado con éxito.", ""));
        cobros = cobroEJB.getAll();
        RequestContext.getCurrentInstance().update("cobro-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgCobroAdd').hide();");
    }
    
    public void deleteCobro(){
        //
        FacesContext context = FacesContext.getCurrentInstance();
        cobroEJB.delete(this.cobroSelected);
        context.addMessage(null, new FacesMessage("Felicidades! El cobro fue anulado con éxito.", ""));
        cobros = cobroEJB.getAll();
        RequestContext.getCurrentInstance().update("cobro-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgCobroAdd').hide();");
    }
    
    public void onRowSelect(SelectEvent event){
        this.cobroSelected = (PagCobros) event.getObject();
        RequestContext.getCurrentInstance().update("cobro-form:dtCobros");
    }

    public List<PagCobros> getCobros() {
        return cobros;
    }

    public void setCobros(List<PagCobros> cobros) {
        this.cobros = cobros;
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public List<ViaViajes> getViajes() {
        return viajes;
    }

    public void setViajes(List<ViaViajes> viajes) {
        this.viajes = viajes;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public PagCobros getCobroSelected() {
        return cobroSelected;
    }

    public void setCobroSelected(PagCobros cobroSelected) {
        this.cobroSelected = cobroSelected;
    }

    public PgePersonas getPersona() {
        return persona;
    }

    public void setPersona(PgePersonas persona) {
        this.persona = persona;
        this.viajes = persona.getViaViajesList();
    }

    public PgeMonedas getMoneda() {
        return moneda;
    }

    public void setMoneda(PgeMonedas moneda) {
        this.moneda = moneda;
    }

    public ViaViajes getViaje() {
        return viaje;
    }

    public void setViaje(ViaViajes viaje) {
        this.viaje = viaje;
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
