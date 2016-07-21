/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PaisDao;
import com.fpuna.py.travelware.dao.VisaDao;
import com.fpuna.py.travelware.model.PgePaises;
import com.fpuna.py.travelware.model.ViaPasaportes;
import com.fpuna.py.travelware.model.ViaVisas;
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
@Named(value = "visaBean")
@SessionScoped
public class VisaBean implements Serializable{
    private List<ViaVisas> visas;
    private List<PgePaises> paises;
    private ViaVisas visaSelected;
    private ViaPasaportes pasaporteSelected;
    @EJB
    private VisaDao visaEJB;
    @EJB
    private PaisDao paisEJB;
    
    //crea una nueva instancia de Visa
    public VisaBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        this.visaSelected = new ViaVisas();
        this.paises = paisEJB.getAll();
    }

    private void clean() {
        this.visaSelected = new ViaVisas();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.visaSelected = new ViaVisas();
    }
    
    public void addVisa(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (visas!=null){
            for (ViaVisas visa:visas){
                if (visa.getVisNroSec().equals(this.visaSelected.getVisNroSec()) && 
                        visa.getPaiId().equals(this.visaSelected.getPaiId()) && this.visaSelected.getVisId()==null){
                    context.addMessage(null, new FacesMessage("Advertencia. " + this.visaSelected.getVisNro()+" ya exste"));
                    this.clean();
                    return;
                }
        }
        }
        
        
        ViaVisas visa = new ViaVisas();
        if (this.visaSelected.getVisId()!=null){
            visa.setVisId(this.visaSelected.getVisId());
        }
        visa.setPaiId(this.visaSelected.getPaiId());
        visa.setPatId(pasaporteSelected);
        visa.setVisNroSec(this.visaSelected.getVisNroSec());
        Integer nroSec ;
        if (this.visaSelected.getVisNro()==null){
            nroSec = visaEJB.getMaxByPasaporte(pasaporteSelected)+1;
        }
        else
        {
            nroSec = this.visaSelected.getVisNro();
        }
        visa.setVisNro(nroSec);
        visa.setVisFecVenc(this.visaSelected.getVisFecVenc());
        visaEJB.update(visa);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! La visa ha sido guardada con éxito"));
        visas = visaEJB.getAll(pasaporteSelected);
        this.clean();
    }
    
    public void deleteVisa(){
        visaEJB.delete(this.visaSelected);
        visas = visaEJB.getAll(pasaporteSelected);
        RequestContext.getCurrentInstance().update("pasaporte-form:dtPasaporte:dtVisa");
    }
    
    public void onRowSelect(SelectEvent event){
        this.visaSelected = (ViaVisas) event.getObject();
        RequestContext.getCurrentInstance().update("pasaporte-form:dtVisa");
    }
    
    public void onRowToggle(ViaPasaportes pasaporte){
        this.pasaporteSelected = pasaporte;
    }

    public List<ViaVisas> getVisas() {
        visas = visaEJB.getAll(pasaporteSelected);
        return visas;
    }

    public void setVisas(List<ViaVisas> visas) {
        this.visas = visas;
    }

    public List<PgePaises> getPaises() {
        return paises;
    }

    public void setPaises(List<PgePaises> paises) {
        this.paises = paises;
    }

    public ViaVisas getVisaSelected() {
        return visaSelected;
    }

    public void setVisaSelected(ViaVisas visaSelected) {
        this.visaSelected = visaSelected;
    }
    
    
}
