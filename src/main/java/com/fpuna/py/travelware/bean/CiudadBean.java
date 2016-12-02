/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.CiudadDao;
import com.fpuna.py.travelware.dao.PaisDao;
import com.fpuna.py.travelware.model.PgeCiudades;
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
@Named(value = "ciudadBean")
@ViewScoped
public class CiudadBean implements Serializable{
    private List<PgeCiudades> ciudades;
    private List<PgePaises> paises;
    private PgeCiudades ciudadSelected;
    private PgeCiudades ciudadNuevo;
    @EJB
    private CiudadDao ciudadEJB;
    @EJB
    private PaisDao paisEJB;
    private LoginBean loginBean;
    private PgePaises pais;
    
    private boolean habilitado;

    //crea una nueva instancia de ciudad
    public CiudadBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session= (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        ciudadSelected = new PgeCiudades();
        pais= new PgePaises();
        ciudadSelected.setPaiId(pais);
        ciudades = ciudadEJB.getAll();
        paises = paisEJB.getAll();
        ciudadNuevo = new PgeCiudades();
        ciudadNuevo.setPaiId(pais);
        habilitado = true;
    }

    private void clean() {
        this.ciudadSelected= new PgeCiudades();
        this.ciudadNuevo = new PgeCiudades();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        ciudadNuevo = new PgeCiudades();
        habilitado = true;
    }
    
    public void addCiudad(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgeCiudades ciu: ciudades){
            if (ciu.getCiuDesc().toUpperCase().equals(this.ciudadNuevo.getCiuDesc().toUpperCase())){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. " + this.ciudadNuevo.getCiuDesc() + " ya existe.", ""));
                //this.clean();
                return;
            }
        }
       
        PgeCiudades ciudad = new PgeCiudades();
        ciudad.setCiuDesc(this.ciudadNuevo.getCiuDesc());
        ciudad.setPaiId(this.ciudadNuevo.getPaiId());
        ciudad.setCiuUbi(this.ciudadNuevo.getCiuUbi());
        ciudad.setCiuUsuIns(loginBean.getUsername());
        ciudad.setCiuFecIns(new Date());
        ciudadEJB.create(ciudad);
        context.addMessage("Mensaje", new FacesMessage("Felcicidades! " + ciudad.getCiuDesc()+" fue creado con éxito.", ""));
        ciudades = ciudadEJB.getAll();
        RequestContext.getCurrentInstance().update("ciudad-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgCiudadAdd').hide();");
    }
   
    public void deleteCiudad(){
        FacesContext context = FacesContext.getCurrentInstance();
        ciudadEJB.delete(this.ciudadSelected);
        context.addMessage(null, new FacesMessage("Felicidades! " + this.ciudadSelected.getCiuDesc() + " fue borrado con éxito.", ""));
        ciudades= ciudadEJB.getAll();
        RequestContext.getCurrentInstance().update("ciudad-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgCiudadUpd').hide();");
    }
   
   public void editCiudad(){
       if (this.ciudadSelected.getCiuId() == null){
           addCiudad();
           return;
       }
       
       PgeCiudades ciudad = ciudadEJB.getById(this.ciudadSelected.getCiuId());
       FacesContext context = FacesContext.getCurrentInstance();
       ciudad.setCiuDesc(this.ciudadSelected.getCiuDesc());
       ciudad.setCiuUbi(this.ciudadSelected.getCiuUbi());
       ciudad.setPaiId(this.ciudadSelected.getPaiId());
       ciudad.setCiuUsuIns(loginBean.getUsername());
       ciudad.setCiuFecIns(new Date());
       ciudadEJB.update(ciudad);
       context.addMessage(null, new FacesMessage("Felicidades! " + ciudad.getCiuDesc() + " fue modificado con éxito.", ""));
       ciudades = ciudadEJB.getAll();
        RequestContext.getCurrentInstance().update("ciudad-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgCiudadUpd').hide();");
   }
   
   public void onRowSelect(SelectEvent event){
       this.ciudadSelected = (PgeCiudades) event.getObject();
       RequestContext.getCurrentInstance().update("ciudad-form:dtCiudad");
   }
   
   public void onCancel(){
        
   }

    public List<PgeCiudades> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<PgeCiudades> ciudades) {
        this.ciudades = ciudades;
    }

    public List<PgePaises> getPaises() {
        return paises;
    }

    public void setPaises(List<PgePaises> paises) {
        this.paises = paises;
    }

    public PgeCiudades getCiudadSelected() {
        return ciudadSelected;
    }

    public void setCiudadSelected(PgeCiudades ciudadSelected) {
        this.ciudadSelected = ciudadSelected;
    }

    public PgeCiudades getCiudadNuevo() {
        return ciudadNuevo;
    }

    public void setCiudadNuevo(PgeCiudades ciudadNuevo) {
        this.ciudadNuevo = ciudadNuevo;
    }

    public CiudadDao getCiudadEJB() {
        return ciudadEJB;
    }

    public void setCiudadEJB(CiudadDao ciudadEJB) {
        this.ciudadEJB = ciudadEJB;
    }
   
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}
