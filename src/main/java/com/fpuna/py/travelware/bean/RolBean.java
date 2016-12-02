/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.RolDao;
import com.fpuna.py.travelware.model.PgeRoles;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "rolBean")
@ViewScoped
public class RolBean implements Serializable{
    private int rolId;
    private String rolDesc;
    private List<PgeRoles> roles;
    private PgeRoles rolSelected;
    private PgeRoles rolNuevo;
    @EJB
    private RolDao rolEJB;
    private LoginBean loginBean;
    
    public RolBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context=javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session= (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        rolSelected= new PgeRoles();
        roles= rolEJB.getAll();
    }
    

    private void clean() {
        this.rolDesc= null;
        this.rolSelected=null;
        this.rolNuevo= new PgeRoles();
    }
    
    public void addRol(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgeRoles rol: roles){
            if (rol.getRolDesc().equals(this.rolNuevo.getRolDesc())){
                context.addMessage(null, new FacesMessage("Advertencia. El rol "+this.rolNuevo.getRolDesc()+" ya existe"));
                this.clean();
                return;
            }
        }
        
        PgeRoles rol = new PgeRoles();
        rol.setRolDesc(this.rolNuevo.getRolDesc());
        rol.setRolUsuIns(loginBean.getUsername());
        rol.setRolFecIns(new Date());
        rolEJB.create(rol);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! " + rol.getRolDesc()+" fue creado con éxito!"));
        roles= rolEJB.getAll();
        this.clean();
    }
    
    public void deleteRol(){
        rolEJB.delete(this.rolSelected);
        roles= rolEJB.getAll();
    }
    
    public void editRol(){
        if (this.rolSelected.getRolId()==null){
            addRol();
            return;
        }
        
        PgeRoles rol=rolEJB.getById(this.rolSelected.getRolId());
        FacesContext context = FacesContext.getCurrentInstance();
        rol.setRolDesc(this.rolSelected.getRolDesc());
        rol.setRolFecMod(new Date());
        rol.setRolUsuMod(loginBean.getUsername());
        rolEJB.update(rol);
        roles= rolEJB.getAll();
        this.clean();
    }
    
    public void onRowSelect(SelectEvent event){
        this.rolSelected = (PgeRoles) event.getObject();
        RequestContext.getCurrentInstance().update("rol-form:dtRol");
    }

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public String getRolDesc() {
        return rolDesc;
    }

    public void setRolDesc(String rolDesc) {
        this.rolDesc = rolDesc;
    }

    public List<PgeRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<PgeRoles> roles) {
        this.roles = roles;
    }

    public PgeRoles getRolSelected() {
        return rolSelected;
    }

    public void setRolSelected(PgeRoles rolSelected) {
        this.rolSelected = rolSelected;
    }

    public PgeRoles getRolNuevo() {
        return rolNuevo;
    }

    public void setRolNuevo(PgeRoles rolNuevo) {
        this.rolNuevo = rolNuevo;
    }
    
    
}
