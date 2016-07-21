/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.model.ViaViajes;
import com.fpuna.py.travelware.model.PgeMonedas;
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
@Named(value = "viajeBean")
@SessionScoped
public class ViajeBean implements Serializable{
    private List<ViaViajes> viajes;
    private List<PgeMonedas> monedas;
    private ViaViajes viajeSelected;
    @EJB
    private ViajeDao viajeEJB;
    @EJB
    private MonedaDao monedaEJB;

    private LoginBean loginBean;

    private PgeMonedas moneda;

    //crea una nueva instancia de Viaje
    public ViajeBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        this.viajeSelected = new ViaViajes();
        this.viajeSelected.setMonId(this.moneda);
        this.monedas = monedaEJB.getAll();
        viajes = viajeEJB.getAll();
    }

    private void clean() {
        this.viajeSelected = new ViaViajes();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.viajeSelected = new ViaViajes();
    }
    
    public void addViaje(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (ViaViajes via:viajes){
            if (via.getViaDesc().equals(this.viajeSelected.getViaDesc()) && this.viajeSelected.getViaId() == null){
                context.addMessage(null, new FacesMessage("Advertencia. " +  this.viajeSelected.getViaDesc()+" ya existe. Verifique."));
                this.clean();
                return;
            }
        }
        
        ViaViajes viaje = new ViaViajes();
        if (this.viajeSelected.getViaId()!= null){ //modificacion
            viaje.setViaId(this.viajeSelected.getViaId());
            viaje.setViaUsuIns(this.viajeSelected.getViaUsuIns());
            viaje.setViaFecIns(this.viajeSelected.getViaFecIns());
            viaje.setViaUsuMod(loginBean.getUsername());
            viaje.setViaFecMod(new Date());
            viaje.setViaCantTot(this.viajeSelected.getViaCantTot());
            viaje.setViaCantVend(this.viajeSelected.getViaCantVend());
        }
        else //insercion
        {
            viaje.setViaUsuIns(loginBean.getUsername());
            viaje.setViaFecIns(new Date());
            viaje.setViaCantTot(0);
            viaje.setViaCantVend(0);
        }
        viaje.setViaDesc(this.viajeSelected.getViaDesc());
        viaje.setViaFecSali(this.viajeSelected.getViaFecSali());
        viaje.setViaFecReg(this.viajeSelected.getViaFecReg());
        viaje.setMonId(this.viajeSelected.getMonId());
        viaje.setViaCost(this.viajeSelected.getViaCost());
        viajeEJB.update(viaje);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! " + viaje.getViaDesc()+" fue agregado con éxito."));
        viajes = viajeEJB.getAll();
        this.clean();
    }
    
    public void deleteViaje(){
        viajeEJB.delete(this.viajeSelected);
        viajes = viajeEJB.getAll();
        RequestContext.getCurrentInstance().update("viaje-form:dtViaje");
    }
    
    public void onRowSelect(SelectEvent event){
        this.viajeSelected = (ViaViajes) event.getObject();
        RequestContext.getCurrentInstance().update("viaje-form:dtViaje");
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

    public ViaViajes getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(ViaViajes viajeSelected) {
        this.viajeSelected = viajeSelected;
    }

    public PgeMonedas getMoneda() {
        return moneda;
    }

    public void setMoneda(PgeMonedas moneda) {
        this.moneda = moneda;
    }
    
}
