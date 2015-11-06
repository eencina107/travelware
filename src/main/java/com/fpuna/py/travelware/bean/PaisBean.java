/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PaisDao;
import com.fpuna.py.travelware.model.PgePaises;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;

/**
 *
 * @author eencina
 */
@Named(value= "paisBean")
@RequestScoped
public class PaisBean {
    private int paiId;
    private String paiDesc;
    private String paiNac;
    private String paiUbi;
    private List<PgePaises> paises;
    private PgePaises paisSelected;
    private DefaultMapModel paisUbiModel;

    public DefaultMapModel getPaisUbiModel() {
        return paisUbiModel;
    }

    public void setPaisUbiModel(DefaultMapModel paisUbiModel) {
        this.paisUbiModel = paisUbiModel;
    }
    @EJB
    private PaisDao paisEJB;
    
    //crear nueva instancia de PaisBean
    public PaisBean(){
    }
    
    @PostConstruct
    private void init(){
        paisSelected = new PgePaises();
        paisUbiModel = new DefaultMapModel();
        paises= paisEJB.getAll();
    }
    
    public void clean(){
        this.paiDesc= null;
        this.paiNac = null;
        this.paiUbi = null;
        this.paisSelected= null;
        this.paisUbiModel=null;
    }
    
    public void addPais(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgePaises pai: paises){
            if (pai.getPaiDesc().equals(paiDesc)){
                context.addMessage(null, new FacesMessage("Advertencia","El pais "+paiDesc+" ya existe"));
                this.clean();
                return;
            }
            if (pai.getPaiNac().equals(paiNac)){
                context.addMessage(null, new FacesMessage("Advertencia","La nacionalidad "+paiNac+" ya existe"));
                this.clean();
                return;
            }
        }
        
        PgePaises pais = new PgePaises();
        pais.setPaiDesc(paiDesc);
        pais.setPaiNac(paiNac);
        pais.setPaiUbi(paiUbi);
        paisEJB.create(pais);
        context.addMessage("Mensaje",new FacesMessage(paiDesc+"fue agregado con exito!"));
        paises = paisEJB.getAll();
        this.clean();
    }
    
    public void deletePais(){
        paisEJB.delete(this.getPaisSelected());
        paises=paisEJB.getAll();
    }
    
    public void editPais(){
        PgePaises pais= paisEJB.getById(this.paisSelected.getPaiId());
        FacesContext context= FacesContext.getCurrentInstance();
        pais.setPaiDesc(paiDesc);
        pais.setPaiNac(paiNac);
        pais.setPaiUbi(paiUbi);
        paisEJB.update(pais);
        context.addMessage(null, new FacesMessage("Mensaje", "El pais "+
                                paiDesc+" fue modificado con éxito!"));
        
        paises= paisEJB.getAll();
        
        this.clean();
    }
    
    public void onRowSelect(SelectEvent event) {
        this.paisSelected = ((PgePaises) event.getObject());
        this.paiDesc = this.getPaisSelected().getPaiDesc();
        this.paiNac = this.getPaisSelected().getPaiNac();
        this.paiUbi = this.getPaisSelected().getPaiUbi();
        RequestContext.getCurrentInstance().update("pais-form:dtPais");
    }
    
    public void onPointSelect(PointSelectEvent event){
        LatLng latlng = event.getLatLng(); //objeto Latitud-Longitud propio de primefaces
        this.paiUbi=latlng.toString();
    }
    
    public int getPaiId(){
        return paiId;
    }
    
    public void setId(int id){
        this.paiId=id;
    }
    
    public String getPaiDesc(){
        return paiDesc;
    }
    
    public void setDescripcion(String desc){
        this.paiDesc=desc;
    }
    
    public String getPaiNac(){
        return paiNac;
    }
    
    public void setPaiNac(String pai_nac){
        this.paiNac=pai_nac;
    }
    
    public String getPaiUbi(){
        return paiUbi;
    }
    
    public void setPaiUbi(String paiUbi){
        this.paiUbi= paiUbi;
        this.paisUbiModel= new DefaultMapModel();
        LatLng coord1= new LatLng(Double.parseDouble(paiUbi.substring(0, paiUbi.indexOf(",")-1)),Double.parseDouble(paiUbi.substring(paiUbi.indexOf(",")+1)));
        this.paisUbiModel.addOverlay(new Marker(coord1));
    }
        
    public List<PgePaises> getPaises(){
        return paises;
    }
    
    public void setPaises(List<PgePaises> paises){
        this.paises=paises;
    }
    
    public PgePaises getPaisSelected(){
        return paisSelected;
    }
    
    public void setPaisSelected(PgePaises paisSelected){
        this.paisSelected=paisSelected;
    }
}


