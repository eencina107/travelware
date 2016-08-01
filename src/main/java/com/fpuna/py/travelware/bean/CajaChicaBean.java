/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.CajaChicaDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.model.ComCajas;
import com.fpuna.py.travelware.model.PgeMonedas;
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

/**
 *
 * @author damia_000
 */
@Named(value = "cajaChicaBean")
@SessionScoped
public class CajaChicaBean implements Serializable{
    private List<ComCajas> cajasChicas;
    private List<PgeMonedas> monedas;
    private ComCajas cajaSelected;
    @EJB
    private CajaChicaDao cajaChicaEJB;
    @EJB
    private MonedaDao monedaEJB;
    
    private LoginBean loginBean;
    private PgeMonedas moneda;
    private boolean existeRegistro;
    
    private boolean habilitado;

    //crea una nueva instancia de caja chica
    public CajaChicaBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.cajaSelected.setMonId(this.moneda);
        cajasChicas = cajaChicaEJB.getAll();
        this.monedas = monedaEJB.getAll();
        this.cajaSelected = cajasChicas.get(0);
        existeRegistro = cajasChicas.size()>0;
        habilitado = true;
    }

    private void clean() {
       this.cajaSelected = new ComCajas();
    }
    
    public void saveCaja(){
        FacesContext context = FacesContext.getCurrentInstance();
        ComCajas cajaChica = new ComCajas();
        if (this.cajaSelected.getCajId()!=null)
        {
            cajaChica.setCajId(this.cajaSelected.getCajId());
            cajaChica.setCajUsuIns(this.cajaSelected.getCajUsuIns());
            cajaChica.setCajFecIns(this.cajaSelected.getCajFecIns());
            cajaChica.setCajUsuMod(loginBean.getUsername());
            cajaChica.setCajFecMod(new Date());
        }
        else
        {
            cajaChica.setCajUsuIns(loginBean.getUsername());
            cajaChica.setCajFecIns(new Date());
        }
        cajaChica.setCajDesc(this.cajaSelected.getCajDesc());
        cajaChica.setCajEst(this.cajaSelected.getCajEst());
        cajaChica.setMonId(this.cajaSelected.getMonId());
        cajaChica.setCajLim(this.cajaSelected.getCajLim());
        cajaChica.setCajSaldo(this.cajaSelected.getCajSaldo());
        cajaChicaEJB.update(cajaChica);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! Caja Chica guardada con éxito!"));
        cajasChicas = cajaChicaEJB.getAll();
        existeRegistro = cajasChicas.size()>0;
        this.cajaSelected = cajasChicas.get(0);
        this.clean();
    }
    
    public void deleteCaja(){
        cajaChicaEJB.delete(this.cajaSelected);
        cajasChicas = cajaChicaEJB.getAll();
        existeRegistro = cajasChicas.size()>0;
        RequestContext.getCurrentInstance().update("cajasChicas-form:dtCajasChicas");
    }
    
    public void buttonAction(ActionEvent actionEvent){
//        this.cajaSelected = new ComCajas();
    }

    public List<ComCajas> getCajasChicas() {
        return cajasChicas;
    }

    public void setCajasChicas(List<ComCajas> cajasChicas) {
        this.cajasChicas = cajasChicas;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public ComCajas getCajaSelected() {
        return cajaSelected;
    }

    public void setCajaSelected(ComCajas cajaSelected) {
        this.cajaSelected = cajaSelected;
    }

    public PgeMonedas getMoneda() {
        return moneda;
    }

    public void setMoneda(PgeMonedas moneda) {
        this.moneda = moneda;
    }

    public boolean isExisteRegistro() {
        return existeRegistro;
    }

    public void setExisteRegistro(boolean existeRegistro) {
        this.existeRegistro = existeRegistro;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}
