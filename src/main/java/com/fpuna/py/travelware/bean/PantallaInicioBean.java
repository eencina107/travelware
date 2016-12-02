/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ContactoDao;
import com.fpuna.py.travelware.dao.PasaporteDao;
import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.ConContactos;
import com.fpuna.py.travelware.model.ViaPasaportes;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * @author olimp
 */
@Named(value = "pantallaInicioBean")
@ViewScoped
public class PantallaInicioBean implements Serializable {
    private List<ViaPasaportes> listaPasaportesVencidos;
    private List<ViaViajes> listaViajesFuturos;
    private List<ConContactos> listaContactosPendientes;
    private Integer viajeSelected;

    @EJB
    private PasaporteDao pasaporteDAO;
    @EJB
    private ViajeDao viajeDao;
    @EJB
    private ContactoDao contactoDao;
    @EJB
    private UsuarioDao usuarioDao;
    
    private LoginBean loginBean;

    private ConContactos contactoSelected;

    public PantallaInicioBean() {

    }

    @PostConstruct
    private void init() {
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        contactoSelected = new ConContactos();

        this.listaPasaportesVencidos = pasaporteDAO.getListaVencidos(new Date());
        this.listaViajesFuturos = viajeDao.getAllFuturos();
        this.listaContactosPendientes = contactoDao.getPendientesByUsuario(usuarioDao.getByName(loginBean.getUsername()));
    }

    private void clean() {
        this.contactoSelected = new ConContactos();
        this.listaPasaportesVencidos = new ArrayList<>();
        this.listaViajesFuturos = new ArrayList<>();
        this.listaContactosPendientes = new ArrayList<>();
    }

    public void buttonAction(ConContactos contacto){
        this.contactoSelected = contacto;
    }

    public void updContacto(){
        FacesContext context = FacesContext.getCurrentInstance();
        ConContactos con= new ConContactos();
        
        con.setConId(this.contactoSelected.getConId());
        con.setConEstado(this.contactoSelected.getConEstado());
        con.setConExito(this.contactoSelected.getConExito());

        con.setConFechaContacto(this.contactoSelected.getConFechaContacto());
        con.setConIdPersonaCont(this.contactoSelected.getConIdPersonaCont());
        con.setConIdUsuarioCont(this.contactoSelected.getConIdUsuarioCont());
        con.setConObservacion(this.contactoSelected.getConObservacion());
        
        contactoDao.update(con); 
        context.addMessage("Mensaje", new FacesMessage("Felicidades! " + "El contacto fue actualizado con éxito."));
        this.clean();
        this.listaContactosPendientes = contactoDao.getPendientesByUsuario(usuarioDao.getByName(loginBean.getUsername()));
    }

    public void onRowSelect(SelectEvent event){
        this.contactoSelected = (ConContactos) event.getObject();
        RequestContext.getCurrentInstance().update("contacto-form:dtContacto");
    }

    public List<ViaPasaportes> getListaPasaportesVencidos() {
        return this.listaPasaportesVencidos;
    }

    public void setListaPasaportesVencidos(List<ViaPasaportes> listaPasaportesVencidos) {
        this.listaPasaportesVencidos = listaPasaportesVencidos;
    }

    public List<ViaViajes> getListaViajesFuturos() {
        return this.listaViajesFuturos;
    }

    public void setListaViajesFuturos(List<ViaViajes> listaViajesFuturos) {
        this.listaViajesFuturos = listaViajesFuturos;
    }

    public List<ConContactos> getListaContactosPendientes() {
        return this.listaContactosPendientes;
    }

    public void setListaContactosPendientes(List<ConContactos> listaContactosPendientes) {
        this.listaContactosPendientes = listaContactosPendientes;
    }

    public ConContactos getContactoSelected() {
        return contactoSelected;
    }

    public void setContactoSelected(ConContactos contactoSelected) {
        this.contactoSelected = contactoSelected;
    }

    public String getCantPasaportesVencidos() {
        this.listaPasaportesVencidos = pasaporteDAO.getListaVencidos(new Date());
        if (listaPasaportesVencidos == null || listaPasaportesVencidos.size() < 1) {
            return String.valueOf("0");
        } else {
            return String.valueOf(this.listaPasaportesVencidos.size());
        }
    }

    public String getCantViajesFuturos() {
        this.listaViajesFuturos = viajeDao.getAllFuturos();
        if (listaViajesFuturos == null || listaViajesFuturos.size() < 1) {
            return String.valueOf("0");
        } else {
            return String.valueOf(this.listaViajesFuturos.size());
        }
    }

    public String getCantContactosPendientes() {
        this.listaContactosPendientes = contactoDao.getPendientesByUsuario(usuarioDao.getByName(loginBean.getUsername()));
        if (listaContactosPendientes == null || listaContactosPendientes.size() < 1) {
            return String.valueOf("0");
        } else {
            return String.valueOf(this.listaContactosPendientes.size());
        }
    }

    public void mostrarDialogPasaportesVencidos() {
        this.listaPasaportesVencidos = pasaporteDAO.getListaVencidos(new Date());
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("pasaportesVenc", options, null);
    }
    
    public void goReporteFactura(){
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/py.travelware/repviaje?id="+viajeSelected.toString());
        } catch (IOException e) {
            System.out.println("Error en servlet repViaje - "+e);
        }
    }

    public void mostrarDialogViajesFuturos() {
        this.listaViajesFuturos = viajeDao.getAllFuturos();
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("viajesFut", options, null);
    }

    public void mostrarDialogContactosPendientes() {
        this.listaContactosPendientes = contactoDao.getPendientesByUsuario(usuarioDao.getByName(loginBean.getUsername()));
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        //options.put("height", 200);
        RequestContext.getCurrentInstance().openDialog("contactosPend", options, null);
    }

    public Integer getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(Integer viajeSelected) {
        this.viajeSelected = viajeSelected;
    }
    
    
}