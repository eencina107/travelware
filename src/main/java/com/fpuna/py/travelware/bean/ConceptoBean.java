/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ConceptoDao;
import com.fpuna.py.travelware.model.ViaConceptos;
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
 * @author damia_000
 */
@Named(value = "conceptoBean")
@SessionScoped
public class ConceptoBean implements Serializable{
    private List<ViaConceptos> conceptos;
    private ViaConceptos conceptoSelected;

    @EJB
    private ConceptoDao conceptoEJB;

    private LoginBean loginBean;
    
    private boolean habilitado;

//    crea una nueva instacia de concepto
    public ConceptoBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        conceptoSelected = new ViaConceptos();
        conceptos = conceptoEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.conceptoSelected = new ViaConceptos();
    }
    
    public void buttonAction(ActionEvent event){
        this.conceptoSelected = new ViaConceptos();
        habilitado = true;
    }
    
    public void addConcepto(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (ViaConceptos con:conceptos){
            if (con.getConDesc().equals(this.conceptoSelected.getConDesc()) && con.getConId()!=this.conceptoSelected.getConId()) {
                context.addMessage(null, new FacesMessage("Advertencia. El concepto "+this.conceptoSelected.getConDesc()+" ya existe. Verifique."));
                this.clean();
                return;
            }
        }
        
        ViaConceptos concepto= new ViaConceptos();
        if (conceptoSelected.getConId()!= null){ //modificado
            concepto.setConUsuMod(loginBean.getUsername());
            concepto.setConFecMod(new Date());
            concepto.setConUsuIns(conceptoSelected.getConUsuIns());
            concepto.setConFecIns(conceptoSelected.getConFecIns());
            concepto.setConId(conceptoSelected.getConId());
        }
        else //nuevo
        {
            concepto.setConUsuIns(loginBean.getUsername());
            concepto.setConFecIns(new Date());
        }
        concepto.setConDesc(conceptoSelected.getConDesc());
        concepto.setConEst(conceptoSelected.getConEst());
        concepto.setConReq(conceptoSelected.getConReq());

        conceptoEJB.update(concepto);
        context.addMessage(null, new FacesMessage("Felicidades! " + concepto.getConDesc()+" fue guardado con éxito."));
        conceptos=conceptoEJB.getAll();
        RequestContext.getCurrentInstance().update("concepto-form:dtConcepto");
        this.clean();
    }
    
    public void deleteConcepto(){
        conceptoEJB.delete(conceptoSelected);
        conceptos=conceptoEJB.getAll();
        RequestContext.getCurrentInstance().update("concepto-form:dtConcepto");
    }
    
    public void onRowSelect(SelectEvent event){
        this.conceptoSelected = (ViaConceptos) event.getObject();
        RequestContext.getCurrentInstance().update("concepto-form:dtConcepto");
    }

    public List<ViaConceptos> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<ViaConceptos> conceptos) {
        this.conceptos = conceptos;
    }

    public ViaConceptos getConceptoSelected() {
        return conceptoSelected;
    }

    public void setConceptoSelected(ViaConceptos conceptoSelected) {
        this.conceptoSelected = conceptoSelected;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}