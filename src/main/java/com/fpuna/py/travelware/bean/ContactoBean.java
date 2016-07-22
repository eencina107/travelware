/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ContactoDao;
import com.fpuna.py.travelware.model.ConContactos;
import com.fpuna.py.travelware.model.PgePersonas;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author olimp
 */
@Named(value = "contactoBean")
@SessionScoped
public class ContactoBean implements Serializable{
    private List<ConContactos> listaContactos;
    private ConContactos contactoSelected;
    private List<PgePersonas> listaPersonas;
    private PgePersonas personaSelected;
    
    @EJB
    private ContactoDao contactoEJB;
    
    public ContactoBean(){
        clean();
        this.listaContactos = contactoEJB.getAll();
        this.contactoSelected = new ConContactos();
    }

    private void clean() {
        this.contactoSelected = new ConContactos();
    }
    
    public void addContacto(){
        FacesContext context = FacesContext.getCurrentInstance();
        ConContactos con= new ConContactos();
        
        con.setConEstado('P');
        con.setConExito(false);
        con.setConFechaContacto(this.contactoSelected.getConFechaContacto());
        con.setConIdPersonaCont(this.contactoSelected.getConIdPersonaCont());
        con.setConObservacion(this.contactoSelected.getConObservacion());
        
        contactoEJB.update(con);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! " + "El contacto fue agregado con éxito."));
        listaContactos = contactoEJB.getAll();
        this.clean();
    }
    
    public void deleteContacto(){
        contactoEJB.delete(contactoSelected);
        listaContactos = contactoEJB.getAll();
        RequestContext.getCurrentInstance().update("contacto-form:dtContacto");
    }
    
    public void onRowSelect(SelectEvent event){
        this.contactoSelected = (ConContactos) event.getObject();
        RequestContext.getCurrentInstance().update("contacto-form:dtContacto");
    }

    public List<ConContactos> getListaContactos() {
        return listaContactos;
    }

    public void setListaContactos(List<ConContactos> listaContactos) {
        this.listaContactos = listaContactos;
    }

    public ConContactos getContactoSelected() {
        return contactoSelected;
    }

    public void setContactoSelected(ConContactos contactoSelected) {
        this.contactoSelected = contactoSelected;
    }

    public List<PgePersonas> getListaPersonas() {
        return listaPersonas;
    }

    public void setListaPersonas(List<PgePersonas> listaPersonas) {
        this.listaPersonas = listaPersonas;
    }

    public PgePersonas getPersonaSelected() {
        return personaSelected;
    }

    public void setPersonaSelected(PgePersonas personaSelected) {
        this.personaSelected = personaSelected;
    }

    public ContactoDao getContactoEJB() {
        return contactoEJB;
    }

    public void setContactoEJB(ContactoDao contactoEJB) {
        this.contactoEJB = contactoEJB;
    }
    
    
}