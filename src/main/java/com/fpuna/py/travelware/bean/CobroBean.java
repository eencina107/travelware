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
 * @author eencina
 */
@Named(value = "cobroBean")
@SessionScoped
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
        cobro.setCobNro(this.cobroSelected.getCobNro());
        cobro.setCobObservacion(this.cobroSelected.getCobObservacion());
        cobro.setCobTipo(this.cobroSelected.getCobTipo());
        cobro.setMonId(this.cobroSelected.getMonId());
        cobro.setViaId(this.cobroSelected.getViaId());
        cobro.setCobAnulado('N');
        cobro.setPerId(persona);
        cobroEJB.update(cobro);
        context.addMessage(null, new FacesMessage("Felicidades!", "El cobro fue guardado con éxito"));
        cobros = cobroEJB.getAll();
        RequestContext.getCurrentInstance().update("cobros-form:dtCobros");
        this.clean();
    }
    
    public void deleteCobro(){
        cobroEJB.delete(this.cobroSelected);
        cobros = cobroEJB.getAll();
        RequestContext.getCurrentInstance().update("cobros-form:dtCobros");
    }
    
    public void onRowSelect(SelectEvent event){
        this.cobroSelected = (PagCobros) event.getObject();
        RequestContext.getCurrentInstance().update("cobros-form:dtCobros");
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
    
    public Integer getSecuencia(){
        return secuenciaEJB.getSec(this.clave)+1;
    }
}
