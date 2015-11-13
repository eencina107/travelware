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
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

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
    @EJB
    private PaisDao paisEJB;
    private LoginBean loginBean;

    
    //crear nueva instancia de PaisBean
    public PaisBean(){
    }
    
    @PostConstruct
    private void init(){
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean =(LoginBean) session.getAttribute("loginBean");
        paisSelected = new PgePaises();
        paises= paisEJB.getAll();
    }
    
    public void clean(){
        this.paiDesc= null;
        this.paiNac = null;
        this.paiUbi = null;
        this.paisSelected= null;
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
        pais.setPaiUsuMod(loginBean.getUsername());
        pais.setPaiFecMod(new Date());
        paisEJB.update(pais);
        context.addMessage(null, new FacesMessage("Mensaje", "El pais  fue modificado con éxito!"));
        
        paises= paisEJB.getAll();
        
        this.clean();
    }
    
    public PgePaises prepareCreate() {
        this.paisSelected = new PgePaises();
        return this.paisSelected;
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
        this.paiDesc = this.getPaisSelected().getPaiDesc();
        this.paiNac = this.getPaisSelected().getPaiNac();
        this.paiUbi = this.getPaisSelected().getPaiUbi();
        RequestContext.getCurrentInstance().update("pais-form:dtPais");
    }
        
    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
}


