/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PasaporteDao;
import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.ViaPasaportes;
import java.io.Serializable;
import java.util.Calendar;
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
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "pasaporteBean")
@SessionScoped
public class PasaporteBean implements Serializable{
    private List<ViaPasaportes> pasaportes;
    private List<PgePersonas> personas;
    private ViaPasaportes pasaporteSelected;
    @EJB
    private PasaporteDao pasaporteEJB;
    @EJB
    private PersonaDao personaEJB;
    private LoginBean loginBean;
    private Date fecha;
    private int anho;
    
    private boolean habilitado;

    //crea una nueva instancia de Pasaporte
    public PasaporteBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.pasaporteSelected = new ViaPasaportes();
        this.pasaportes = pasaporteEJB.getAll();
        this.personas = personaEJB.getAll();
        this.fecha = new Date();
        this.fecha = DateUtils.round(this.fecha, Calendar.MONTH);
        habilitado = true;
    }

    private void clean() {
        this.pasaporteSelected = new ViaPasaportes();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.pasaporteSelected = new ViaPasaportes();
    }
    
    public void addPasaporte(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (ViaPasaportes pas:pasaportes){
            if (this.pasaporteSelected!=null && pas.getPatNroPas().equals(this.pasaporteSelected.getPatNroPas()) && !(pas.getPatId().equals(this.pasaporteSelected.getPatId())) ){
                context.addMessage(null, new FacesMessage("Advertencia. Pasaporte "+this.pasaporteSelected.getPatNroPas()+" ya existe!"));
                this.clean();
                return;
            }
            
            if (this.pasaporteSelected!=null && pas.getPerId().equals(this.pasaporteSelected.getPerId()) && !(pas.getPatId().equals(this.pasaporteSelected.getPatId()))){
                context.addMessage(null, new FacesMessage("Advertencia. "+ this.pasaporteSelected.getPerId().getPerNom()+" "+this.pasaporteSelected.getPerId().getPerApe()+" ya posee pasaporte."));
                this.clean();
                return;
            }
        }
        
        ViaPasaportes pasaporte = new ViaPasaportes();
        if (this.pasaporteSelected.getPatId()!=null){
            pasaporte.setPatId(this.pasaporteSelected.getPatId());
            pasaporte.setPatUsuIns(this.pasaporteSelected.getPatUsuIns());
            pasaporte.setPatFecIns(this.pasaporteSelected.getPatFecIns());
            pasaporte.setPatUsuMod(loginBean.getUsername());
            pasaporte.setPatFecMod(new Date());
        }
        else
        {
            pasaporte.setPatUsuIns(loginBean.getUsername());
            pasaporte.setPatFecIns(new Date());
        }
        pasaporte.setPatNroPas(this.pasaporteSelected.getPatNroPas());
        pasaporte.setPatFecEmi(this.pasaporteSelected.getPatFecEmi());
        pasaporte.setPatFecVen(this.pasaporteSelected.getPatFecVen());
        pasaporte.setPerId(this.pasaporteSelected.getPerId());
        pasaporteEJB.update(pasaporte);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El pasaporte "+this.pasaporteSelected.getPatNroPas()+" fue guardado con éxito!"));
        pasaportes = pasaporteEJB.getAll();
        this.clean();
    }
    
    public void deletePasaporte(){
        pasaporteEJB.delete(this.pasaporteSelected);
        pasaportes = pasaporteEJB.getAll();
        RequestContext.getCurrentInstance().update("pasaporte-form:dtPasaporte");
    }
    
    public void onRowSelect(SelectEvent event){
        this.pasaporteSelected = (ViaPasaportes) event.getObject(); 
        RequestContext.getCurrentInstance().update("pasaporte-form:dtPasaporte");
    }

    public List<ViaPasaportes> getPasaportes() {
        return pasaportes;
    }

    public void setPasaportes(List<ViaPasaportes> pasaportes) {
        this.pasaportes = pasaportes;
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   

    public ViaPasaportes getPasaporteSelected() {
        return pasaporteSelected;
    }

    public void setPasaporteSelected(ViaPasaportes pasaporteSelected) {
        this.pasaporteSelected = pasaporteSelected;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getAnho() {
        return anho;
    }
    
    public String getValidezVenc(Date fechaVenc){
        Date rounded = DateUtils.truncate(fechaVenc, Calendar.MONTH);
        if (rounded.equals(this.fecha) || rounded.before(this.fecha)){
            return "#FF0000";
        }
        else
        {
            return "#000000";
        }
    }
    
}
