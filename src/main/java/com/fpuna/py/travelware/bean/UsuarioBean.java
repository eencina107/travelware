/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.model.PgePersonas;
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
@Named(value = "usuarioBean")
@SessionScoped
public class UsuarioBean implements Serializable{
    private List<PgeUsuarios> usuarios;
    private List<PgePersonas> personas;
    private PgeUsuarios usuarioSelected;
    @EJB
    private UsuarioDao usuarioEJB;
    @EJB
    private PersonaDao personaEJB;
    
    private LoginBean loginBean;
    
    private PgePersonas persona;
    
    private boolean habilitado;

//    crea una nueva instacia de usuario
    public UsuarioBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        usuarioSelected = new PgeUsuarios();
        persona = new PgePersonas();
        usuarioSelected.setPerId(persona);
        usuarios = usuarioEJB.getAll();
        personas = personaEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.usuarioSelected = new PgeUsuarios();
    }
    
    public void buttonAction(ActionEvent event){
        this.usuarioSelected = new PgeUsuarios();
    }
    
    public void addUsuario(){
        FacesContext context = FacesContext.getCurrentInstance();
        usuarios = usuarioEJB.getAllTot();
        for (PgeUsuarios usu:usuarios){
            if (usu.getUsuCod().equals(this.usuarioSelected.getUsuCod()) &&
                    ((this.usuarioSelected.getUsuId()!=null && !usu.getUsuId().equals(this.usuarioSelected.getUsuId())) || this.usuarioSelected.getUsuId()==null)){
                context.addMessage(null, new FacesMessage("Advertencia. Ya existe un usuario con éste codigo"));
                usuarios = usuarioEJB.getAll();
                this.clean();
                return;
            }
        }
        usuarios = usuarioEJB.getAll();
        for (PgeUsuarios usu:usuarios){
            if (usu.getPerId().equals(this.usuarioSelected.getPerId()) &&
                    ((this.usuarioSelected.getUsuId()!=null && !usu.getUsuId().equals(this.usuarioSelected.getUsuId())) || this.usuarioSelected.getUsuId()==null)){
                context.addMessage(null, new FacesMessage("Advertencia. Ya existe un usuario para esta persona"));
                this.clean();
                return;
            }
        }
        
        PgeUsuarios usuario= new PgeUsuarios();
        if (usuarioSelected.getUsuId()!= null){
            usuario.setUsuUsuMod(loginBean.getUsername());
            usuario.setUsuFecMod(new Date());
            usuario.setUsuUsuIns(usuarioSelected.getUsuUsuIns());
            usuario.setUsuFecIns(usuarioSelected.getUsuFecIns());
            usuario.setUsuId(usuarioSelected.getUsuId());
            usuario.setUsuFecIng(usuarioSelected.getUsuFecIng());
        }
        else
        {
            usuario.setUsuUsuIns(loginBean.getUsername());
            usuario.setUsuFecIns(new Date());
            usuario.setUsuFecIng(new Date());
        }
        usuario.setPerId(usuarioSelected.getPerId());
        usuario.setUsuCargo(usuarioSelected.getUsuCargo());
        usuario.setUsuCod(usuarioSelected.getUsuCod());
        usuario.setUsuEst('A');
        usuario.setUsuPass(usuarioSelected.getUsuPass());
        usuarioEJB.update(usuario);
        context.addMessage(null, new FacesMessage("Felicidades! " + usuario.getUsuCod()+" fue guardado con éxito"));
        usuarios=usuarioEJB.getAll();
        RequestContext.getCurrentInstance().update("usuario-form:dtUsuario");
        this.clean();
    }
    
    public void deleteUsuario(){
        usuarioEJB.delete(usuarioSelected);
        usuarios=usuarioEJB.getAll();
        RequestContext.getCurrentInstance().update("usuario-form:dtUsuario");
    }
    
    public void onRowSelect(SelectEvent event){
        this.usuarioSelected = (PgeUsuarios) event.getObject();
        RequestContext.getCurrentInstance().update("usuario-form:dtUsuario");
    }

    public List<PgeUsuarios> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<PgeUsuarios> usuarios) {
        this.usuarios = usuarios;
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public PgeUsuarios getUsuarioSelected() {
        return usuarioSelected;
    }

    public void setUsuarioSelected(PgeUsuarios usuarioSelected) {
        this.usuarioSelected = usuarioSelected;
    }

    public PgePersonas getPersona() {
        return persona;
    }

    public void setPersona(PgePersonas persona) {
        this.persona = persona;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}
