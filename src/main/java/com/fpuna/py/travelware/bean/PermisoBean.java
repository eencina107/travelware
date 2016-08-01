/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.MenuDao;
import com.fpuna.py.travelware.dao.PermisoDao;
import com.fpuna.py.travelware.dao.RolDao;
import com.fpuna.py.travelware.model.PgeMenus;
import com.fpuna.py.travelware.model.PgePermisos;
import com.fpuna.py.travelware.model.PgeRoles;
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
@Named(value = "permisoBean")
@SessionScoped
public class PermisoBean implements Serializable{
    private List<PgePermisos> permisos;
    private List<PgeRoles> roles;
    private List<PgeMenus> menus;
    private PgePermisos permisoSelected;
    @EJB
    private RolDao rolEJB;
    @EJB
    private PermisoDao permisoEJB;
    @EJB
    private MenuDao menuEJB;
    private PgeRoles rol;
    private PgeMenus menu;
    
    private boolean habilitado;

    //crea una nueva instancia de Permiso
    public PermisoBean(){
    
    }
    
    @PostConstruct
    public void init(){
        clean();
        permisoSelected = new PgePermisos();
        rol = new PgeRoles();
        permisoSelected.setRolId(rol);
        menu= new PgeMenus();
        permisoSelected.setPgeMenus(menu);
        permisos = permisoEJB.getAll();
        roles = rolEJB.getAll();
        menus = menuEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.permisoSelected = new PgePermisos();
        this.menu = new PgeMenus();
        this.rol = new PgeRoles();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        permisoSelected = new PgePermisos();
        this.menu = permisoSelected.getPgeMenus();
        this.rol = permisoSelected.getRolId();
    }
    
    public void addPermiso(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgePermisos per:permisos){
            if (per.getPgeMenus().equals(this.menu) && per.getRolId().equals(this.rol)){
                context.addMessage("Mensaje", new FacesMessage("Advertencia. El permiso ya existe"));
                this.clean();
                return;
            }
        }
        
        PgePermisos permiso= new PgePermisos();
        if (this.permisoSelected.getPrmId()!= null){
            permiso.setPrmId(this.permisoSelected.getPrmId());
        }
        permiso.setPgeMenus(this.permisoSelected.getPgeMenus());
        permiso.setRolId(this.permisoSelected.getRolId());
        permisoEJB.update(permiso);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El permiso fue guardado con éxito"));
        permisos = permisoEJB.getAll();
        RequestContext.getCurrentInstance().update("permiso-form:dtPermiso");
        this.clean();
    }
    
    public void deletePermiso(){
        permisoEJB.delete(permisoSelected);
        permisos = permisoEJB.getAll();
        RequestContext.getCurrentInstance().update("permiso-form:dtPermiso");
    }
    
    public void onRowSelect(SelectEvent event){
        permisoSelected = (PgePermisos) event.getObject();
        RequestContext.getCurrentInstance().update("permiso-form:dtPermiso");
    }

    public List<PgePermisos> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<PgePermisos> permisos) {
        this.permisos = permisos;
    }

    public List<PgeRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<PgeRoles> roles) {
        this.roles = roles;
    }

    public List<PgeMenus> getMenus() {
        return menus;
    }

    public void setMenus(List<PgeMenus> menus) {
        this.menus = menus;
    }

    public PgePermisos getPermisoSelected() {
        return permisoSelected;
    }

    public void setPermisoSelected(PgePermisos permisoSelected) {
        this.permisoSelected = permisoSelected;
    }

    public PgeRoles getRol() {
        return rol;
    }

    public void setRol(PgeRoles rol) {
        this.rol = rol;
    }

    public PgeMenus getMenu() {
        return menu;
    }

    public void setMenu(PgeMenus menu) {
        this.menu = menu;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}
