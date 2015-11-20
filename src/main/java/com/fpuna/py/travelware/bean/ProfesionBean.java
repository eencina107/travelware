/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ProfesionDao;
import com.fpuna.py.travelware.model.PgeProfesiones;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "profesionBean")
@SessionScoped
public class ProfesionBean implements Serializable{
    private int profesionId;
    private String profesionDesc;
    private String profesionObs;
    private List<PgeProfesiones> profesiones;
    private PgeProfesiones profesionSelected;
    private PgeProfesiones profesionNuevo;
    @EJB
    private ProfesionDao profesionEJB;
    private LoginBean loginBean;
    
    public ProfesionBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context= javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session= (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        profesionSelected= new PgeProfesiones();
        profesiones=profesionEJB.getAll();
        
    }
    
    private void clean() {
        this.profesionDesc= null;
        this.profesionObs= null;
        this.profesionSelected= null;
        this.profesionNuevo= new PgeProfesiones();
    }
    
    public void addProfesion(){
        FacesContext context= FacesContext.getCurrentInstance();
        for (PgeProfesiones prof: profesiones){
            if (prof.getPrfDesc().toUpperCase().equals(this.profesionNuevo.getPrfDesc().toUpperCase())){
                context.addMessage(null, new FacesMessage("Advertencia", "La profesión "+this.profesionNuevo.getPrfDesc()+" ya existe."));
                this.clean();
                return;
            }
        }
        
        PgeProfesiones profesion= new PgeProfesiones();
        profesion.setPrfDesc(this.profesionNuevo.getPrfDesc());
        profesion.setPrfObs(this.profesionNuevo.getPrfObs());
        profesion.setPrfUsuIns(loginBean.getUsername());
        profesion.setPrfFecIns(new Date());
        profesionEJB.create(profesion);
        context.addMessage("Mensaje", new FacesMessage("Felicidades!", profesion.getPrfDesc()+" fue creado con éxito!"));
        profesiones= profesionEJB.getAll();
        this.clean();
    }
    
    public void deleteProfesion(){
        profesionEJB.delete(this.getProfesionSelected());
        profesiones = profesionEJB.getAll();
    }
    
    public void editProfesion(){
        if (this.profesionSelected.getPrfId()==null){
            addProfesion();
            return;
        }
        
        PgeProfesiones profesion = profesionEJB.getById(this.profesionSelected.getPrfId());
        FacesContext context = FacesContext.getCurrentInstance();
        profesion.setPrfDesc(this.profesionSelected.getPrfDesc());
        profesion.setPrfObs(this.profesionSelected.getPrfObs());
        profesion.setPrfUsuMod(loginBean.getUsername());
        profesion.setPrfFecMod(new Date());
        profesionEJB.update(profesion);
        profesiones=profesionEJB.getAll();
        this.clean();
    }
    
    public void onRowSelect(SelectEvent event){
        this.profesionSelected = (PgeProfesiones) event.getObject();
        RequestContext.getCurrentInstance().update("profesion-form:dtProfesion");
    }

    public int getProfesionId() {
        return profesionId;
    }

    public void setProfesionId(int profesionId) {
        this.profesionId = profesionId;
    }

    public String getProfesionDesc() {
        return profesionDesc;
    }

    public void setProfesionDesc(String profesionDesc) {
        this.profesionDesc = profesionDesc;
    }

    public String getProfesionObs() {
        return profesionObs;
    }

    public void setProfesionObs(String profesionObs) {
        this.profesionObs = profesionObs;
    }

    public List<PgeProfesiones> getProfesiones() {
        return profesiones;
    }

    public void setProfesiones(List<PgeProfesiones> profesiones) {
        this.profesiones = profesiones;
    }

    public PgeProfesiones getProfesionSelected() {
        return profesionSelected;
    }

    public void setProfesionSelected(PgeProfesiones profesionSelected) {
        this.profesionSelected = profesionSelected;
    }

    public PgeProfesiones getProfesionNuevo() {
        return profesionNuevo;
    }

    public void setProfesionNuevo(PgeProfesiones profesionNuevo) {
        this.profesionNuevo = profesionNuevo;
    }

    public ProfesionDao getProfesionEJB() {
        return profesionEJB;
    }

    public void setProfesionEJB(ProfesionDao profesionEJB) {
        this.profesionEJB = profesionEJB;
    }

   
    
}
