/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.dao.PrecioViajeDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.ViaPreViajes;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@Named(value = "precioViajeBean")
@SessionScoped
public class PrecioViajeBean implements Serializable{
    private List<ViaPreViajes> preciosViajes;
    private List<ViaPreViajes> preciosViajesFilt;
    private List<PgeMonedas> monedas;
    private List<ViaViajes> viajes;
    private ViaPreViajes precioViajeSelected;
    private ViaViajes viajeSelected;
    @EJB
    private MonedaDao monedaEJB;
    @EJB
    private PrecioViajeDao precioViajeEJB;
    @EJB
    private ViajeDao viajeEJB;
    
    //crea una nueva instancia de PrecioViajeBean
    public PrecioViajeBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        viajes = viajeEJB.getAll();
        monedas = monedaEJB.getAll();
        this.precioViajeSelected = new ViaPreViajes();
        this.viajeSelected = new ViaViajes();
        preciosViajes = precioViajeEJB.getAll();
    }

    private void clean() {
        this.precioViajeSelected= new ViaPreViajes();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.precioViajeSelected = new ViaPreViajes();
    }
    
    public void addPrecioViaje(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (preciosViajes!=null){
            for (ViaPreViajes pre:preciosViajes){
                if (pre.getPreNombre().equals(this.precioViajeSelected.getPreNombre()) && this.precioViajeSelected.getPreId()==null){
                    context.addMessage(null, new FacesMessage("Advertencia. "+ this.precioViajeSelected.getPreNombre()+" ya existe"));
                    this.clean();
                    return;
                }

                if (pre.getMonId().equals(this.precioViajeSelected.getMonId()) && pre.getPrePrecio().equals(this.precioViajeSelected.getPrePrecio()) && this.precioViajeSelected.getPreId()==null){
                    context.addMessage(null, new FacesMessage("Advertencia. Ya existe este precio para este viaje"));
                    this.clean();
                    return;
                }
            }
        }
        
        
        ViaPreViajes precio= new ViaPreViajes();
        if (this.precioViajeSelected.getPreId()!=null){
            precio.setPreId(this.precioViajeSelected.getPreId());
        }
        precio.setMonId(this.precioViajeSelected.getMonId());
        precio.setPreDescripcion(this.precioViajeSelected.getPreDescripcion());
        precio.setPreNombre(this.precioViajeSelected.getPreNombre());
        precio.setPrePrecio(this.precioViajeSelected.getPrePrecio());
        precio.setViaId(viajeSelected);
        precioViajeEJB.update(precio);
        context.addMessage(null, new FacesMessage("Felicidades! El precio fue guardado con éxito!"));
        preciosViajes = precioViajeEJB.getAll(this.viajeSelected);
        this.clean();
    }
    
    public void deletePrecio(){
        precioViajeEJB.delete(this.precioViajeSelected);
        preciosViajes = precioViajeEJB.getAll(this.viajeSelected);
        RequestContext.getCurrentInstance().update("precioViaje-form:dtPrecioViaje");
    }
    
    public void onRowSelect(SelectEvent event){
        this.precioViajeSelected = (ViaPreViajes) event.getObject();
        RequestContext.getCurrentInstance().update("precioViaje-form:dtPrecioViaje");
    }

    public List<ViaPreViajes> getPreciosViajes() {
       
        return preciosViajes;
    }

    public void setPreciosViajes(List<ViaPreViajes> preciosViajes) {
        this.preciosViajes = preciosViajes;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public List<ViaViajes> getViajes() {
        return viajes;
    }

    public void setViajes(List<ViaViajes> viajes) {
        this.viajes = viajes;
    }

    public ViaPreViajes getPrecioViajeSelected() {
        return precioViajeSelected;
    }

    public void setPrecioViajeSelected(ViaPreViajes precioViajeSelected) {
        this.precioViajeSelected = precioViajeSelected;
    }

    public ViaViajes getViajeSelected() {
            return viajeSelected;
    }

    public void setViajeSelected(ViaViajes viajeSelected) {
        this.viajeSelected = viajeSelected;
         this.preciosViajes = precioViajeEJB.getAll(viajeSelected);
    }
    
    public String getSimpleDateFormat(Date fecha){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }

    public List<ViaPreViajes> getPreciosViajesFilt() {
        return preciosViajesFilt;
    }

    public void setPreciosViajesFilt(List<ViaPreViajes> preciosViajesFilt) {
        this.preciosViajesFilt = preciosViajesFilt;
    }
    
    
}
