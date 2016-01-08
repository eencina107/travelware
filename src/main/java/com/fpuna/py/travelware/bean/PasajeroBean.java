/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PasajeroDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.ViaPasajeros;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 *
 * @author eencina
 */
@Named(value = "pasajeroBean")
@SessionScoped
public class PasajeroBean implements Serializable{
    private List<ViaPasajeros> pasajeros;
    private List<ViaViajes> viajes;
    private ViaPasajeros pasajeroSelected;
    @EJB
    private PasajeroDao pasajeroEJB;
    @EJB
    private ViajeDao viajeEJB;
    
    private LoginBean loginBean;
    
    //crea una nueva instancia de Pasajero
    public PasajeroBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.pasajeroSelected = new ViaPasajeros();
        this.pasajeros = pasajeroEJB.getAll();
        this.viajes = viajeEJB.getAll();
    }

    private void clean() {
        this.pasajeroSelected = new ViaPasajeros();
    }
    
}
