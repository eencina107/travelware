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
@Named(value = "usuRolBean")
@ViewScoped
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
        usuRoles = usuRolEJB.getAll();
        roles = rolEJB.getAll();
        usuarios = usuarioEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.usuRolSelected = new PgeUsuRoles();
    }

    public void buttonAction(ActionEvent actionEvent){
        usuRolSelected = new PgeUsuRoles();
<<<<<<< HEAD
=======
        this.rol = new PgeRoles();
        this.usuario = new PgeUsuarios();
        usuRolSelected.setRolId(rol);
        usuRolSelected.setUsuId(usuario);
>>>>>>> ed7c8ab95f4e17d7d3de0c1366b38e8d1895c766
        habilitado = true;
    }
    
    public void addUsuRol(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgeUsuRoles ur:usuRoles){
            if (!ur.getUsuRolId().equals(this.usuRolSelected.getUsuRolId()) &&
                    ur.getRolId().equals(this.usuRolSelected.getRolId()) && ur.getUsuId().equals(this.usuRolSelected.getUsuId())){
                context.addMessage("Mensaje", new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. " + this.usuRolSelected.getUsuId().getUsuCod()+
                                                                    " ya se encuentra relacionado a " + this.usuRolSelected.getRolId().getRolDesc(), ""));
                //this.clean();
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
        context.addMessage(null, new FacesMessage("Felicidades! La relación fue guardada con éxito.", ""));
        usuRoles = usuRolEJB.getAll();
        RequestContext.getCurrentInstance().update("usuRol-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgUsuRolAdd').hide();");
    }
    
    public void deleteUsuRol(){
        FacesContext context = FacesContext.getCurrentInstance();
        usuRolEJB.delete(usuRolSelected);
        context.addMessage(null, new FacesMessage("Felicidades! La relación fue borrada con éxito.", ""));
        usuRoles = usuRolEJB.getAll();
        RequestContext.getCurrentInstance().update("usuRol-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgUsuRolAdd').hide();");
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

    public boolean isHabilitado() {
        return habilitado;
    }

<<<<<<< HEAD
=======
    public PgeUsuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(PgeUsuarios usuario) {
        this.usuario = usuario;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

>>>>>>> ed7c8ab95f4e17d7d3de0c1366b38e8d1895c766
    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}
