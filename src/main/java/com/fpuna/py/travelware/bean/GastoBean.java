/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.GastoDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.ViaGastos;
import com.fpuna.py.travelware.model.ViaPasajeros;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "gastoBean")
@SessionScoped
public class GastoBean implements Serializable{
    private List<ViaGastos> gastos;
    private List<PgeMonedas> monedas;
    private ViaGastos gastoSelected;
    private ViaPasajeros pasajeroSelected;
    @EJB
    private GastoDao gastoEJB;
    @EJB
    private MonedaDao monedaEJB;
    
    //crea una nueva instancia de Gasto
    public GastoBean(){
    
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        this.gastoSelected = new ViaGastos();
        this.monedas = monedaEJB.getAll();
    }

    private void clean() {
        this.gastoSelected = new ViaGastos();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.gastoSelected = new ViaGastos();
    }
    
    public void addGasto(){
        FacesContext context = FacesContext.getCurrentInstance();
        ViaGastos gasto= new ViaGastos();
        if (this.gastoSelected.getGasId()!=null){
            gasto.setGasId(this.gastoSelected.getGasId());
        }
        gasto.setGasMonto(this.gastoSelected.getGasMonto());
        gasto.setGasNro(this.gastoSelected.getGasNro());
        gasto.setGasTip(this.gastoSelected.getGasTip());
        gasto.setMonId(this.gastoSelected.getMonId());
        gasto.setPviId(pasajeroSelected);
        gastoEJB.update(gasto);
        context.addMessage("Mensaje", new FacesMessage("Felicidades!", "El gasto ha sido guardado exitosamente"));
        gastos = gastoEJB.getAll(pasajeroSelected);
        this.clean();
    }
    
    public void deleteGasto(){
        gastoEJB.delete(this.gastoSelected);
        gastos = gastoEJB.getAll(this.pasajeroSelected);
        RequestContext.getCurrentInstance().update("pasajero-form:dtPasajero:dtGasto");
    }
    
    public void onRowSelect(SelectEvent event){
        this.gastoSelected = (ViaGastos) event.getObject();
        RequestContext.getCurrentInstance().update("pasajero-form:dtPasajero:dtGasto");
    }
    
    public void onRowToggle(ViaPasajeros pasajero){
        this.pasajeroSelected = pasajero;
    }

    public List<ViaGastos> getGastos() {
        gastos = gastoEJB.getAll(this.pasajeroSelected);
        return gastos;
    }

    public void setGastos(List<ViaGastos> gastos) {
        this.gastos = gastos;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public ViaGastos getGastoSelected() {
        return gastoSelected;
    }

    public void setGastoSelected(ViaGastos gastoSelected) {
        this.gastoSelected = gastoSelected;
    }

    public ViaPasajeros getPasajeroSelected() {
        return pasajeroSelected;
    }

    public void setPasajeroSelected(ViaPasajeros pasajeroSelected) {
        this.pasajeroSelected = pasajeroSelected;
    }
    
    
}
