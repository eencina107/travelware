/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PasajeroDao;
import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.PrecioViajeDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.ViaPasajeros;
import com.fpuna.py.travelware.model.ViaPreViajes;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@Named(value = "pasajeroBean")
@SessionScoped
public class PasajeroBean implements Serializable {

    private List<ViaPasajeros> pasajeros;
    private List<ViaViajes> viajes;
    private List<PgePersonas> personas;
    private List<ViaPreViajes> precViajes;
    private ViaPasajeros pasajeroSelected;
    private ViaViajes viajeSelected;
    @EJB
    private PasajeroDao pasajeroEJB;
    @EJB
    private ViajeDao viajeEJB;
    @EJB
    private PersonaDao personaEJB;
    @EJB
    private PrecioViajeDao precViajeEJB;

    private LoginBean loginBean;

    //crea una nueva instancia de Pasajero
    public PasajeroBean() {

    }

    @PostConstruct
    public void init() {
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.pasajeroSelected = new ViaPasajeros();
        this.viajeSelected = new ViaViajes();
        this.pasajeros = pasajeroEJB.getAll();
        this.viajes = viajeEJB.getAll();
        this.personas = personaEJB.getAll();
    }

    private void clean() {
        this.pasajeroSelected = new ViaPasajeros();
        this.viajeSelected = new ViaViajes();
    }

    public void buttonAction(ActionEvent actionEvent) {
        this.pasajeroSelected = new ViaPasajeros();
    }

    public void addPasajero() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (pasajeros != null) {
            for (ViaPasajeros pas : pasajeros) {
                if (pas.getPerId() == this.pasajeroSelected.getPerId() && pas.getViaId().equals(this.pasajeroSelected.getViaId()) && this.pasajeroSelected.getPviId() != null) {
                    context.addMessage(null, new FacesMessage("Advertencia", "Esta persona ya se encuentra registrada"));
                    this.clean();
                    return;
                }
            }
        }
        ViaPasajeros pasajero = new ViaPasajeros();
        if (this.pasajeroSelected.getPviId() != null) {
            pasajero.setPviId(this.pasajeroSelected.getPviId());
            pasajero.setPviUsuIns(this.pasajeroSelected.getPviUsuIns());
            pasajero.setPviFecIns(this.pasajeroSelected.getPviFecIns());
            pasajero.setPviUsuMod(loginBean.getUsername());
            pasajero.setPviFecMod(new Date());
        } else {
            pasajero.setPviUsuIns(loginBean.getUsername());
            pasajero.setPviFecIns(new Date());
        }
        pasajero.setViaId(this.pasajeroSelected.getViaId());
        pasajero.setPerId(this.pasajeroSelected.getPerId());
        pasajero.setPasPrefAsi(this.pasajeroSelected.getPasPrefAsi());
        pasajero.setPasPrefComi(this.pasajeroSelected.getPasPrefComi());
        pasajero.setPasRel(this.pasajeroSelected.getPasRel());
        pasajeroEJB.update(pasajero);
        context.addMessage("Mensaje", new FacesMessage("Felicidades!", "El pasajero fue guardado con éxito"));
        pasajeros = pasajeroEJB.getAll();
        this.clean();
    }

    public void deletePasajero() {
        pasajeroEJB.delete(this.pasajeroSelected);
        pasajeros = pasajeroEJB.getAll();
        RequestContext.getCurrentInstance().update("pasajero-form:dtPasajero");
    }

    public void onRowSelect(SelectEvent event) {
        this.pasajeroSelected = (ViaPasajeros) event.getObject();
        RequestContext.getCurrentInstance().update("pasajero-form:dtPasajero");
    }

    public List<ViaPasajeros> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(List<ViaPasajeros> pasajeros) {
        this.pasajeros = pasajeros;
    }

    public List<ViaViajes> getViajes() {
        return viajes;
    }

    public void setViajes(List<ViaViajes> viajes) {
        this.viajes = viajes;
    }

    public ViaPasajeros getPasajeroSelected() {
        return pasajeroSelected;
    }

    public void setPasajeroSelected(ViaPasajeros pasajeroSelected) {
        this.pasajeroSelected = pasajeroSelected;
    }

    public ViaViajes getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(ViaViajes viajeSelected) {
        this.viajeSelected = viajeSelected;
        pasajeros = pasajeroEJB.getAll(viajeSelected);
        RequestContext.getCurrentInstance().update("pasajero-form:dtPasajero");
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public List<ViaPreViajes> getPrecViajes() {
        precViajes = precViajeEJB.getAll(this.viajeSelected);
        return precViajes;
    }

    public void setPrecViajes(List<ViaPreViajes> precViajes) {
        this.precViajes = precViajes;
    }

    public String getSimpleDateFormat(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }
}
