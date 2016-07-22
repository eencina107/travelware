/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.OrganizacionDao;
import com.fpuna.py.travelware.dao.ProveedorDao;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.PgeOrganizaciones;
import com.fpuna.py.travelware.model.ComProveedores;
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
@Named(value = "proveedorBean")
@SessionScoped
public class ProveedorBean implements Serializable{
    private List<ComProveedores> proveedores;
    private List<PgePersonas> personas;
    private List<PgeOrganizaciones> organizaciones;
    private ComProveedores proveedorSelected;
    @EJB
    private ProveedorDao proveedorEJB;
    @EJB
    private PersonaDao personaEJB;
    @EJB
    private OrganizacionDao organizacionEJB;
    
    private LoginBean loginBean;
    
    private PgePersonas persona;
    private PgeOrganizaciones organizacion;
    
//    crea una nueva instacia de proveedor
    public ProveedorBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        proveedorSelected = new ComProveedores();
 
        persona = new PgePersonas();
        proveedorSelected.setPerId(persona);
        personas = personaEJB.getAll();

        organizacion = new PgeOrganizaciones();
        proveedorSelected.setOrgId(organizacion);
        organizaciones = organizacionEJB.getAll();

        proveedores = proveedorEJB.getAll();
    }

    private void clean() {
        this.proveedorSelected = new ComProveedores();
    }
    
    public void buttonAction(ActionEvent event){
        this.proveedorSelected = new ComProveedores();
    }
    
    public void addProveedor(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (ComProveedores pro:proveedores){
            if (pro.getPerId()!=null && pro.getPerId().equals(this.proveedorSelected.getPerId()) && this.proveedorSelected.getProId()!=null && !pro.getProId().equals(this.proveedorSelected.getProId())){
                context.addMessage(null, new FacesMessage("Advertencia. Ya existe un proveedor para esta persona"));
                this.clean();
                return;
            }
            if (pro.getOrgId()!=null && pro.getOrgId().equals(this.proveedorSelected.getOrgId()) && this.proveedorSelected.getProId()!=null && !pro.getProId().equals(this.proveedorSelected.getProId())){
                context.addMessage(null, new FacesMessage("Advertencia. Ya existe un proveedor para esta organizacion"));
                this.clean();
                return;
            }
        }
        
        ComProveedores proveedor= new ComProveedores();
        if (proveedorSelected.getProId()!= null){
            proveedor.setProUsuMod(loginBean.getUsername());
            proveedor.setProFecMod(new Date());
            proveedor.setProUsuIns(proveedorSelected.getProUsuIns());
            proveedor.setProFecIns(proveedorSelected.getProFecIns());
            proveedor.setProId(proveedorSelected.getProId());
        }
        else
        {
            proveedor.setProUsuIns(loginBean.getUsername());
            proveedor.setProFecIns(new Date());
        }
        proveedor.setProDesc(proveedorSelected.getProDesc());
        proveedor.setProEst('A');
        proveedor.setPerId(proveedorSelected.getPerId());
        proveedor.setOrgId(proveedorSelected.getOrgId());
        proveedor.setProNroTim(proveedorSelected.getProNroTim());
        proveedor.setProFecVen(proveedorSelected.getProFecVen());
        proveedorEJB.update(proveedor);
        context.addMessage(null, new FacesMessage("Felicidades! "+ proveedor.getProDesc()+" fue guardado con éxito"));
        proveedores=proveedorEJB.getAll();
        RequestContext.getCurrentInstance().update("proveedor-form:dtProveedor");
        this.clean();
    }
    
    public void deleteProveedor(){
        proveedorEJB.delete(proveedorSelected);
        proveedores=proveedorEJB.getAll();
        RequestContext.getCurrentInstance().update("proveedor-form:dtProveedor");
    }
    
    public void onRowSelect(SelectEvent event){
        this.proveedorSelected = (ComProveedores) event.getObject();
        RequestContext.getCurrentInstance().update("proveedor-form:dtProveedor");
    }

    public List<ComProveedores> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<ComProveedores> proveedores) {
        this.proveedores = proveedores;
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public List<PgeOrganizaciones> getOrganizaciones() {
        return organizaciones;
    }

    public void setOrganizaciones(List<PgeOrganizaciones> organizaciones) {
        this.organizaciones = organizaciones;
    }

    public ComProveedores getProveedorSelected() {
        return proveedorSelected;
    }

    public void setProveedorSelected(ComProveedores proveedorSelected) {
        this.proveedorSelected = proveedorSelected;
    }

    public PgePersonas getPersona() {
        return persona;
    }

    public void setPersona(PgePersonas persona) {
        this.persona = persona;
    }
    
    public PgeOrganizaciones getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(PgeOrganizaciones organizacion) {
        this.organizacion = organizacion;
    }
}