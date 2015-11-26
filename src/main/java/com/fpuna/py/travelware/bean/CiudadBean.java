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
@Named(value = "ciudadBean")
@SessionScoped
public class CiudadBean implements Serializable{
    private int ciuId;
    private int paiId;
    private String ciuDesc;
    private String ciuUbi;
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
    }

    private void clean() {
        this.ciuDesc = null;
        this.ciuUbi= null;
        this.ciudadSelected= new PgeCiudades();
        this.ciudadNuevo = new PgeCiudades();
    }
    
   public void addCiudad(){
       FacesContext context = FacesContext.getCurrentInstance();
       for (PgeCiudades ciu: ciudades){
           if (ciu.getCiuDesc().toUpperCase().equals(this.ciudadNuevo.getCiuDesc().toUpperCase())){
               context.addMessage(null, new FacesMessage("Advertencia", "La ciudad "+this.ciudadNuevo.getCiuDesc()+" ya existe"));
               this.clean();
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
       context.addMessage("Mensaje", new FacesMessage("Felcicidades!", ciudad.getCiuDesc()+" fue creada con éxito!"));
       ciudades = ciudadEJB.getAll();
       this.clean();
   }
   
   public void deleteCiudad(){
       ciudadEJB.delete(this.ciudadSelected);
       ciudades= ciudadEJB.getAll();
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
       context.addMessage(null, new FacesMessage("Felicidades!", ciudad.getCiuDesc()+" fue modificado con éxito!"));
       ciudades = ciudadEJB.getAll();
       this.clean();
   }
   
   public void onRowSelect(SelectEvent event){
       this.ciudadSelected = (PgeCiudades) event.getObject();
       RequestContext.getCurrentInstance().update("ciudad-form:dtCiudad");
   }
   
   public void onCancel(){
        
   }

    public int getCiuId() {
        return ciuId;
    }

    public void setCiuId(int ciuId) {
        this.ciuId = ciuId;
    }

    public int getPaiId() {
        return paiId;
    }

    public void setPaiId(int paiId) {
        this.paiId = paiId;
    }

    public String getCiuDesc() {
        return ciuDesc;
    }

    public void setCiuDesc(String ciuDesc) {
        this.ciuDesc = ciuDesc;
    }

    public String getCiuUbi() {
        return ciuUbi;
    }

    public void setCiuUbi(String ciuUbi) {
        this.ciuUbi = ciuUbi;
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
   
   
}
