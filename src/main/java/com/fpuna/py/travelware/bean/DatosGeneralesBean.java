/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.DatosGeneralesDao;
import com.fpuna.py.travelware.model.PgeDatosGenerales;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author eencina
 */
@Named(value = "datosGeneralesBean")
@ViewScoped
public class DatosGeneralesBean implements Serializable{
    private List<PgeDatosGenerales> datosGenerales;
    private PgeDatosGenerales datoSelected;
    @EJB
    private DatosGeneralesDao datosGeneralesEJB;
    
    private LoginBean loginBean;
    private boolean existeRegistro;
    
    //crea una nueva instancia de datos generales
    public DatosGeneralesBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        datosGenerales = datosGeneralesEJB.getAll();
        this.datoSelected = datosGenerales.get(0);
        existeRegistro = datosGenerales.size()>0;
    }

    private void clean() {
       this.datoSelected = new PgeDatosGenerales();
    }
    
    public void saveDatos(){
        FacesContext context = FacesContext.getCurrentInstance();
        PgeDatosGenerales datoGeneral = new PgeDatosGenerales();
        if (this.datoSelected.getGralId()!=null){
            datoGeneral.setGralId(this.datoSelected.getGralId());
            datoGeneral.setGralUsuIns(this.datoSelected.getGralUsuIns());
            datoGeneral.setGralFecIns(this.datoSelected.getGralFecIns());
            datoGeneral.setGralUsuMod(loginBean.getUsername());
            datoGeneral.setGralFecMod(new Date());
        }
        else
        {
            datoGeneral.setGralUsuIns(loginBean.getUsername());
            datoGeneral.setGralFecIns(new Date());
        }
        datoGeneral.setGralDireccion(this.datoSelected.getGralDireccion());
        datoGeneral.setGralEmail1(this.datoSelected.getGralEmail1());
        datoGeneral.setGralEmail2(this.datoSelected.getGralEmail2());
        datoGeneral.setGralNombAbrev(this.datoSelected.getGralNombAbrev());
        datoGeneral.setGralNombExt(this.datoSelected.getGralNombExt());
        datoGeneral.setGralRuc(this.datoSelected.getGralRuc());
        datoGeneral.setGralTel1(this.datoSelected.getGralTel1());
        datoGeneral.setGralTel2(this.datoSelected.getGralTel2());
        datosGeneralesEJB.update(datoGeneral);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! Datos Guardados con éxito", ""));
        datosGenerales = datosGeneralesEJB.getAll();
        existeRegistro = datosGenerales.size()>0;
        this.datoSelected = datosGenerales.get(0);
        RequestContext.getCurrentInstance().update("datosGenerales-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgDatosGeneralesAdd').hide();");
    }
    
    public void deleteDatos(){
        FacesContext context = FacesContext.getCurrentInstance();
        datosGeneralesEJB.delete(this.datoSelected);
        context.addMessage(null, new FacesMessage("Felicidades! Datos borrados con éxito.", ""));
        datosGenerales = datosGeneralesEJB.getAll();
        existeRegistro = datosGenerales.size()>0;
        RequestContext.getCurrentInstance().update("datosGenerales-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgDatosGeneralesAdd').hide();");
    }
    
    public void buttonAction(ActionEvent actionEvent){
//        this.datoSelected = new PgeDatosGenerales();
    }

    public List<PgeDatosGenerales> getDatosGenerales() {
        return datosGenerales;
    }

    public void setDatosGenerales(List<PgeDatosGenerales> datosGenerales) {
        this.datosGenerales = datosGenerales;
    }

    public PgeDatosGenerales getDatoSelected() {
        return datoSelected;
    }

    public void setDatoSelected(PgeDatosGenerales datoSelected) {
        this.datoSelected = datoSelected;
    }

    public boolean isExisteRegistro() {
        return existeRegistro;
    }

    public void setExisteRegistro(boolean existeRegistro) {
        this.existeRegistro = existeRegistro;
    }

}
