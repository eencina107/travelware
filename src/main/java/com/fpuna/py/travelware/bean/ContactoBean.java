/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ContactoDao;
import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.model.ConContactos;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.PgeUsuarios;
import java.io.Serializable;
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
 * @author olimp
 */
@Named(value = "contactoBean")
@SessionScoped
public class ContactoBean implements Serializable{
    private List<ConContactos> contactos;
    private List<PgePersonas> personas;
    private List<PgeUsuarios> usuarios;

    @EJB
    private ContactoDao contactoEJB;
    @EJB
    private PersonaDao personaEJB;
    @EJB
    private UsuarioDao usuarioEJB;

    private LoginBean loginBean;

    private ConContactos contactoSelected;
    private PgePersonas personaSelected;
    private PgeUsuarios usuarioSelected;
    
//    crea una nueva instacia de contacto
    public ContactoBean(){

    }

    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        contactoSelected = new ConContactos();

        personaSelected = new PgePersonas();
        contactoSelected.setConIdPersonaCont(personaSelected);
        personas = personaEJB.getAll();

        usuarioSelected = new PgeUsuarios();
        contactoSelected.setConIdUsuarioCont(usuarioSelected);
        usuarios = usuarioEJB.getAll();

        contactos = contactoEJB.getAll();
    }

    private void clean() {
        this.contactoSelected = new ConContactos();
    }
    
    public void buttonAction(ActionEvent event){
        this.contactoSelected = new ConContactos();
    }

    public void addContacto(){
        FacesContext context = FacesContext.getCurrentInstance();
        ConContactos con= new ConContactos();
        
        if (this.contactoSelected.getConId()!= null){ //modificacion
            con.setConId(this.contactoSelected.getConId());
            con.setConEstado(this.contactoSelected.getConEstado());
            con.setConExito(this.contactoSelected.getConExito());
        }
        else //nuevo
        {
            con.setConEstado('P');
            con.setConExito(false);
        }
        con.setConFechaContacto(this.contactoSelected.getConFechaContacto());
        con.setConIdPersonaCont(this.contactoSelected.getConIdPersonaCont());
        con.setConIdUsuarioCont(this.contactoSelected.getConIdUsuarioCont());
        con.setConObservacion(this.contactoSelected.getConObservacion());
        
        contactoEJB.update(con);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! " + "El contacto fue agregado con éxito."));
        contactos = contactoEJB.getAll();
        this.clean();
    }
    
    public void deleteContacto(){
        contactoEJB.delete(contactoSelected);
        contactos = contactoEJB.getAll();
        RequestContext.getCurrentInstance().update("contacto-form:dtContacto");
    }
    
    public void onRowSelect(SelectEvent event){
        this.contactoSelected = (ConContactos) event.getObject();
        RequestContext.getCurrentInstance().update("contacto-form:dtContacto");
    }

    public List<ConContactos> getContactos() {
        return contactos;
    }

    public void setContactos(List<ConContactos> contactos) {
        this.contactos = contactos;
    }

    public ConContactos getContactoSelected() {
        return contactoSelected;
    }

    public void setContactoSelected(ConContactos contactoSelected) {
        this.contactoSelected = contactoSelected;
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public List<PgeUsuarios> getUsuarios() {
        return this.usuarios;
    }

    public void setUsuarios(List<PgeUsuarios> usuarios) {
        this.usuarios = usuarios;
    }

    public PgePersonas getPersonaSelected() {
        return personaSelected;
    }

    public void setPersonaSelected(PgePersonas personaSelected) {
        this.personaSelected = personaSelected;
    }

    public PgeUsuarios getUsuarioSelected() {
        return this.usuarioSelected;
    }

    public void setPersonaSelected(PgeUsuarios usuarioSelected) {
        this.usuarioSelected = usuarioSelected;
    }

    public ContactoDao getContactoEJB() {
        return contactoEJB;
    }

    public void setContactoEJB(ContactoDao contactoEJB) {
        this.contactoEJB = contactoEJB;
    }
    
    
}