/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PasaporteDao;
import com.fpuna.py.travelware.model.ViaPasaportes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author olimp
 */
@Named(value = "pantallaInicioBean")
@SessionScoped
public class PantallaInicioBean implements Serializable {

    @EJB
    private PasaporteDao pasaporteDAO;
    private List<ViaPasaportes> listaPasaportesVencidos;

    public PantallaInicioBean() {

    }

    @PostConstruct
    private void init() {
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        listaPasaportesVencidos = pasaporteDAO.getListaVencidos(new Date());
    }

    private void clean() {
        this.listaPasaportesVencidos = new ArrayList<>();
    }

    public List<ViaPasaportes> getListaPasaportesVencidos() {
        return listaPasaportesVencidos;
    }

    public void setListaPasaportesVencidos(List<ViaPasaportes> listaPasaportesVencidos) {
        this.listaPasaportesVencidos = listaPasaportesVencidos;
    }

    public String getCantPasaportesVencidos() {
        if (listaPasaportesVencidos == null || listaPasaportesVencidos.size() < 1) {
            return String.valueOf("0");
        } else {
            return String.valueOf(this.listaPasaportesVencidos.size());
        }
    }

    public void mostrarDialogPasaportesVencidos() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("pasaportesVenc", options, null);
    }
}
