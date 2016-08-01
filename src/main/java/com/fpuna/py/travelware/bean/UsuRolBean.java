/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.RolDao;
import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.dao.UsuarioRolDao;
import com.fpuna.py.travelware.model.PgeRoles;
import com.fpuna.py.travelware.model.PgeUsuRoles;
import com.fpuna.py.travelware.model.PgeUsuarios;
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
 * @author eencina
 */
@Named(value = "usuRolBean")
@SessionScoped
public class UsuRolBean implements Serializable{
    private List<PgeUsuRoles> usuRoles;
    private List<PgeRoles> roles;
    private List<PgeUsuarios> usuarios;
    private PgeUsuRoles usuRolSelected;
    @EJB
    private UsuarioRolDao usuRolEJB;
    @EJB
    private RolDao rolEJB;
    @EJB
    private UsuarioDao usuarioEJB;
    private PgeRoles rol;
    private PgeUsuarios usuario;
    private LoginBean loginBean;
    
    private boolean habilitado;

    //crea una nueva instancia de Usuario-Rol
    public UsuRolBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        usuRolSelected = new PgeUsuRoles();
        rol = new PgeRoles();
        usuRolSelected.setRolId(rol);
        usuario = new PgeUsuarios();
        usuRolSelected.setUsuId(usuario);
        usuRoles = usuRolEJB.getAll();
        roles = rolEJB.getAll();
        usuarios = usuarioEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.usuRolSelected = new PgeUsuRoles();
        this.rol = new PgeRoles();
        this.usuario = new PgeUsuarios();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        usuRolSelected = new PgeUsuRoles();
        this.rol = new PgeRoles();
        this.usuario = new PgeUsuarios();
        usuRolSelected.setRolId(rol);
        usuRolSelected.setUsuId(usuario);
    }
    
    public void addUsuRol(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgeUsuRoles ur:usuRoles){
            if (ur.getRolId().equals(rol) && ur.getUsuId().equals(usuario)){
                context.addMessage("Mensaje", new FacesMessage("Advertencia. Este usuario ya se encuentra en este rol"));
                this.clean();
                return;
            }
        }
        
        PgeUsuRoles usuRol = new PgeUsuRoles();
        if (this.usuRolSelected.getUsuRolId()!=null){
            usuRol.setUsuRolId(this.usuRolSelected.getUsuRolId());
            usuRol.setUroFecMod(new Date());
            usuRol.setUroUsuMod(loginBean.getUsername());
            usuRol.setUroFecIns(this.usuRolSelected.getUroFecIns());
            usuRol.setUroUsuIns(this.usuRolSelected.getUroUsuIns());
        }
        else
        {
            usuRol.setUroFecIns(new Date());
            usuRol.setUroUsuIns(loginBean.getUsername());
        }
        usuRol.setRolId(this.usuRolSelected.getRolId());
        usuRol.setUsuId(this.usuRolSelected.getUsuId());
        usuRolEJB.update(usuRol);
        context.addMessage(null, new FacesMessage("Felicidades! La relación fue guardada con éxito"));
        usuRoles = usuRolEJB.getAll();
        RequestContext.getCurrentInstance().update("usuRol-form");
        this.clean();
    }
    
    public void deleteUsuRol(){
        usuRolEJB.delete(usuRolSelected);
        usuRoles = usuRolEJB.getAll();
        RequestContext.getCurrentInstance().update("usuRol-form:dtUsuRol");
    }
    
    public void onRowSelect(SelectEvent event){
        usuRolSelected = (PgeUsuRoles) event.getObject();
        RequestContext.getCurrentInstance().update("usuRol-form:dtUsuRol");
    }

    public List<PgeUsuRoles> getUsuRoles() {
        return usuRoles;
    }

    public void setUsuRoles(List<PgeUsuRoles> usuRoles) {
        this.usuRoles = usuRoles;
    }

    public List<PgeRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<PgeRoles> roles) {
        this.roles = roles;
    }

    public List<PgeUsuarios> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<PgeUsuarios> usuarios) {
        this.usuarios = usuarios;
    }

    public PgeUsuRoles getUsuRolSelected() {
        return usuRolSelected;
    }

    public void setUsuRolSelected(PgeUsuRoles usuRolSelected) {
        this.usuRolSelected = usuRolSelected;
    }

    public PgeRoles getRol() {
        return rol;
    }

    public void setRol(PgeRoles rol) {
        this.rol = rol;
    }

    public PgeUsuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(PgeUsuarios usuario) {
        this.usuario = usuario;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}
