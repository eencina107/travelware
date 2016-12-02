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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "profesionBean")
@ViewScoped
public class ProfesionBean implements Serializable{
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
        this.profesionSelected= null;
        this.profesionNuevo= new PgeProfesiones();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        profesionNuevo = new PgeProfesiones();
    }
    
    public void addProfesion(){
        FacesContext context= FacesContext.getCurrentInstance();
        for (PgeProfesiones prof: profesiones){
            if (prof.getPrfDesc().toUpperCase().equals(this.profesionNuevo.getPrfDesc().toUpperCase())){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. La profesión "+this.profesionNuevo.getPrfDesc()+" ya existe.", ""));
                //this.clean();
                return;
            }
        }
        
        PgeProfesiones profesion= new PgeProfesiones();
        profesion.setPrfDesc(this.profesionNuevo.getPrfDesc());
        profesion.setPrfObs(this.profesionNuevo.getPrfObs());
        profesion.setPrfUsuIns(loginBean.getUsername());
        profesion.setPrfFecIns(new Date());
        profesionEJB.create(profesion);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! La profesión " + profesion.getPrfDesc() + " fue creada con éxito.", ""));
        profesiones= profesionEJB.getAll();
        RequestContext.getCurrentInstance().update("profesion-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgProfesionAdd').hide();");
    }
    
    public void deleteProfesion(){
        FacesContext context = FacesContext.getCurrentInstance();
        profesionEJB.delete(this.getProfesionSelected());
        context.addMessage(null, new FacesMessage("Felicidades! La profesión " + this.getProfesionSelected().getPrfDesc() + " fue borrada con éxito.", ""));
        profesiones = profesionEJB.getAll();
        RequestContext.getCurrentInstance().update("profesion-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgProfesionUpd').hide();");
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
        context.addMessage("Mensaje", new FacesMessage("Felicidades! La profesión " + profesion.getPrfDesc() + " fue modificada con éxito.", ""));
        profesiones=profesionEJB.getAll();
        RequestContext.getCurrentInstance().update("profesion-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgProfesionUpd').hide();");
    }
    
    public void onRowSelect(SelectEvent event){
        this.profesionSelected = (PgeProfesiones) event.getObject();
        RequestContext.getCurrentInstance().update("profesion-form:dtProfesion");
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
