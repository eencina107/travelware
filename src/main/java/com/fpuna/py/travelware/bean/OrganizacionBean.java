/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.CiudadDao;
import com.fpuna.py.travelware.dao.OrganizacionDao;
import com.fpuna.py.travelware.model.PgeCiudades;
import com.fpuna.py.travelware.model.PgeOrganizaciones;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "organizacionBean")
@SessionScoped
public class OrganizacionBean implements Serializable{
    private List<PgeOrganizaciones> organizaciones;
    private List<PgeCiudades> ciudades;
    private PgeOrganizaciones organizacionSelected;
    @EJB
    private OrganizacionDao organizacionEJB;
    @EJB
    private CiudadDao ciudadEJB;
    private LoginBean loginBean;
    private PgeCiudades ciudad;
    
    //Crea una nueva instancia
    public OrganizacionBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context= javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session= (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        organizacionSelected = new PgeOrganizaciones();
        ciudad = new PgeCiudades();
        organizacionSelected.setCiuId(ciudad);
        organizaciones = organizacionEJB.getAll();
        ciudades = ciudadEJB.getAll();
    }

    private void clean() {
        organizacionSelected = new PgeOrganizaciones();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        organizacionSelected = new PgeOrganizaciones();
    }
    
    public void addOrganizacion(){
        FacesContext context = FacesContext.getCurrentInstance();
        String mensaje;
        for (PgeOrganizaciones org:organizaciones){
            if (org.getOrgDesc().toUpperCase().equals(this.organizacionSelected.getOrgDesc().toUpperCase())){
                context.addMessage(null, new FacesMessage("Advertencia", "La organización "+this.organizacionSelected.getOrgDesc()+" ya existe"));
                this.clean();
                return;
            }
        }
        
        PgeOrganizaciones organizacion = new PgeOrganizaciones();
        if (this.organizacionSelected.getOrgId()!=null){
            organizacion.setOrgFecMod(new Date());
            organizacion.setOrgUsuMod(loginBean.getUsername());
            organizacion.setOrgId(this.organizacionSelected.getOrgId());
            mensaje = organizacion.getOrgDesc()+" fue modificado con éxito!";
        }
        else{
            organizacion.setOrgFecMod(new Date());
            organizacion.setOrgUsuMod(loginBean.getUsername());
            mensaje = organizacion.getOrgDesc()+" fue creado con éxito!";
        }
        organizacion.setOrgDesc(this.organizacionSelected.getOrgDesc());
        organizacion.setOrgDir(this.organizacionSelected.getOrgDir());
        organizacion.setOrgLogo(this.organizacionSelected.getOrgLogo());
        organizacion.setOrgPagWeb(this.organizacionSelected.getOrgPagWeb());
        organizacion.setOrgSubTipo(this.organizacionSelected.getOrgSubTipo());
        organizacion.setOrgTel(this.organizacionSelected.getOrgTel());
        organizacion.setOrgTipo(this.organizacionSelected.getOrgTipo());
        organizacion.setOrgUbi(this.organizacionSelected.getOrgUbi());
        context.addMessage("Mensaje", new FacesMessage("Felicidades!"+mensaje));
        organizaciones = organizacionEJB.getAll();
        this.clean();        
    }
    
    public void deleteOrganizacion(){
        organizacionEJB.delete(this.organizacionSelected);
        organizaciones = organizacionEJB.getAll();
        RequestContext.getCurrentInstance().update("organizacion-form:dtOrganizacion");
    }
    
    public void onRowSelect(SelectEvent event){
        this.organizacionSelected = (PgeOrganizaciones) event.getObject();
        RequestContext.getCurrentInstance().update("organizacion-form:dtOrganizacion");
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        try{
            this.organizacionSelected.setOrgLogo(event.getFile().getContents());
        }
        catch (Exception e){
            FacesMessage message = new FacesMessage("Error!", event.getFile().getFileName() + e);
            return;
        }
        FacesMessage message = new FacesMessage("Felicidades!", event.getFile().getFileName() + " fue subido con éxito.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<PgeOrganizaciones> getOrganizaciones() {
        return organizaciones;
    }

    public void setOrganizaciones(List<PgeOrganizaciones> organizaciones) {
        this.organizaciones = organizaciones;
    }

    public List<PgeCiudades> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<PgeCiudades> ciudades) {
        this.ciudades = ciudades;
    }

    public PgeOrganizaciones getOrganizacionSelected() {
        return organizacionSelected;
    }

    public void setOrganizacionSelected(PgeOrganizaciones organizacionSelected) {
        this.organizacionSelected = organizacionSelected;
    }

    public OrganizacionDao getOrganizacionEJB() {
        return organizacionEJB;
    }

    public void setOrganizacionEJB(OrganizacionDao organizacionEJB) {
        this.organizacionEJB = organizacionEJB;
    }

    public CiudadDao getCiudadEJB() {
        return ciudadEJB;
    }

    public void setCiudadEJB(CiudadDao ciudadEJB) {
        this.ciudadEJB = ciudadEJB;
    }

    public PgeCiudades getCiudad() {
        return ciudad;
    }

    public void setCiudad(PgeCiudades ciudad) {
        this.ciudad = ciudad;
    }
    
    
}
