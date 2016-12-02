/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PaisDao;
import com.fpuna.py.travelware.model.PgePaises;
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
@Named(value= "paisBean")
@ViewScoped
public class PaisBean implements Serializable{
    private List<PgePaises> paises;
    private PgePaises paisSelected;
    private PgePaises paisNuevo;
    @EJB
    private PaisDao paisEJB;
    private LoginBean loginBean;

    
    //crear nueva instancia de Pais
    public PaisBean(){

    }
    
    @PostConstruct
    private void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean =(LoginBean) session.getAttribute("loginBean");
        paisSelected = new PgePaises();
        paises= paisEJB.getAll();
    }

    public void clean(){
        this.paisSelected= new PgePaises();
        this.paisNuevo = new PgePaises();
    }

    public void buttonAction(ActionEvent actionEvent){
        paisNuevo = new PgePaises();
    }

    public void addPais(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgePaises pai: paises){
            if (pai.getPaiDesc().equals(this.paisNuevo.getPaiDesc())){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. El pais "+this.paisNuevo.getPaiDesc()+" ya existe.", ""));
                //this.clean();
                return;
            }
            if (pai.getPaiNac().equals(this.paisNuevo.getPaiNac())){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. La nacionalidad "+this.paisNuevo.getPaiNac()+" ya existe.", ""));
                //this.clean();
                return;
            }
        }
        
        PgePaises pais = new PgePaises();
        pais.setPaiDesc(this.paisNuevo.getPaiDesc());
        pais.setPaiNac(this.paisNuevo.getPaiNac());
        pais.setPaiUbi(this.paisNuevo.getPaiUbi());
        pais.setPaiUsuIns(loginBean.getUsername());
        pais.setPaiFecIns(new Date());
        paisEJB.create(pais);
        context.addMessage("Mensaje",new FacesMessage("Felicidades! " + pais.getPaiDesc() + " fue agregado con exito.", ""));
        paises = paisEJB.getAll();
        RequestContext.getCurrentInstance().update("pais-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgPaisAdd').hide();");
    }
    
    public void deletePais(){
        FacesContext context = FacesContext.getCurrentInstance();
        paisEJB.delete(this.getPaisSelected());
        context.addMessage(null, new FacesMessage("Felicidades! " + this.paisSelected.getPaiDesc()+" fue borrado con éxito.", ""));
        paises=paisEJB.getAll();
        RequestContext.getCurrentInstance().update("pais-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgPaisUpd').hide();");
}
    
    public void editPais(){
        //Verificar si es nulo el idPais
        if (null == this.paisSelected.getPaiId())
        {
            addPais();
            return;
        }
        
        PgePaises pais= paisEJB.getById(this.paisSelected.getPaiId());
        FacesContext context= FacesContext.getCurrentInstance();
        pais.setPaiDesc(this.paisSelected.getPaiDesc());
        pais.setPaiNac(this.paisSelected.getPaiNac());
        pais.setPaiUbi(this.paisSelected.getPaiUbi());
        pais.setPaiUsuMod(loginBean.getUsername());
        pais.setPaiFecMod(new Date());
        paisEJB.update(pais);
        context.addMessage(null, new FacesMessage("Felicidades! " + this.paisSelected.getPaiDesc()+" fue modificado con éxito.", ""));
        paises= paisEJB.getAll();
        RequestContext.getCurrentInstance().update("pais-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgPaisUpd').hide();");
    }
    
    public PgePaises prepareCreate() {
        this.paisSelected = new PgePaises();
        return this.paisSelected;
    }
    
//    public void onRowSelect(SelectEvent event) {
//        //(Car) event.getObject()).getId();
//        PgePaises pais=paisEJB.getById(((PgePaises) event.getObject()).getPaiId());
//        setPaisSelected(pais);
//    }
    
    public void onCancel(){
        
    }

    public List<PgePaises> getPaises(){
        return paises;
    }
    
    public void setPaises(List<PgePaises> paises){
        this.paises=paises;
    }
    
    public PgePaises getPaisSelected(){
        return this.paisSelected;
    }
    
    public void setPaisSelected(PgePaises paisSelected){
        this.paisSelected=paisSelected;
//        this.paiDesc = this.getPaisSelected().getPaiDesc();
//        this.paiNac = this.getPaisSelected().getPaiNac();
//        this.paiUbi = this.getPaisSelected().getPaiUbi();
        RequestContext.getCurrentInstance().update("pais-form:dtPais");
    }
    public void onRowSelect(SelectEvent event) {
        this.paisSelected = (PgePaises) event.getObject();
//        this.bloquearBotones = false;
        RequestContext.getCurrentInstance().update("pais-form:dtPais");
    } 
    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public PgePaises getPaisNuevo() {
        return paisNuevo;
    }

    public void setPaisNuevo(PgePaises paisNuevo) {
        this.paisNuevo = paisNuevo;
    }
    
}


