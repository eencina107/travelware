/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.dao.PaisDao;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.PgePaises;
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
@Named(value = "monedaBean")
@ViewScoped
public class MonedaBean implements Serializable{
    private String monDesc;
    private List<PgeMonedas> monedas;
    private List<PgePaises> paises;
    private PgeMonedas monedaSelected;
    @EJB
    private MonedaDao monedaEJB;
    @EJB
    private PaisDao paisEJB;
    private LoginBean loginBean;
    private PgePaises pais;
    
    private boolean habilitado;

    //crea una nueva instancia de Moneda
    public MonedaBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        monedaSelected = new PgeMonedas();
        pais = new PgePaises();
        monedaSelected.setPaiId(pais);
        monedas = monedaEJB.getAll();
        paises = paisEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.monDesc = null;
        this.monedaSelected = new PgeMonedas();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        System.out.println("aqui!");
        monedaSelected = new PgeMonedas();
        habilitado = true;
    }
    
    public void addMoneda(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (monedas !=null){
            for (PgeMonedas mon:monedas){
                if (mon.getMonDesc().equalsIgnoreCase(this.monedaSelected.getMonDesc()) && mon.getPaiId().equals(this.monedaSelected.getPaiId())){
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. El "+this.monedaSelected.getMonDesc()+" de "+this.monedaSelected.getPaiId().getPaiDesc()+" ya existe.", ""));
                    //this.clean();
                    return ;
                }
            }
        }
        
        PgeMonedas moneda= new PgeMonedas();
        if (this.monedaSelected.getMonId()!= null){ //modificacion
            moneda.setMonId(this.monedaSelected.getMonId());
        }
        moneda.setMonDesc(this.monedaSelected.getMonDesc());
        moneda.setMonAbreviatura(this.monedaSelected.getMonAbreviatura());
        moneda.setPaiId(this.monedaSelected.getPaiId());
        moneda.setMonUsuIns(loginBean.getUsername());
        moneda.setMonFecIns(new Date());
        monedaEJB.update(moneda);
        if (this.monedaSelected.getMonId()!=null) {
            context.addMessage("Mensaje", new FacesMessage("Felicidades! " + moneda.getMonDesc() + " fue actualizado con éxito.", ""));
        }
        else{
            context.addMessage("Mensaje", new FacesMessage("Felicidades! " + moneda.getMonDesc() + " fue creado con éxito.", ""));
        }
        monedas = monedaEJB.getAll();
        RequestContext.getCurrentInstance().update("moneda-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgMonedaAdd').hide();");
    }
    
    public void deleteMoneda(){
        FacesContext context = FacesContext.getCurrentInstance();
        monedaEJB.delete(this.monedaSelected);
        context.addMessage(null, new FacesMessage("Felicidades!" + this.monedaSelected.getMonDesc() + " fue borrado con éxito.", ""));
        monedas = monedaEJB.getAll();
        RequestContext.getCurrentInstance().update("moneda-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgMonedaAdd').hide();");
    }
    
    public void onRowSelect(SelectEvent event){
        this.monedaSelected = (PgeMonedas) event.getObject();
      //  RequestContext.getCurrentInstance().update("moneda-form:dtMoneda");
    }

    public String getMonDesc() {
        return monDesc;
    }

    public void setMonDesc(String monDesc) {
        this.monDesc = monDesc;
    }

    public PgeMonedas getMonedaSelected() {
        return monedaSelected;
    }

    public void setMonedaSelected(PgeMonedas monedaSelected) {
        this.monedaSelected = monedaSelected;
    }

    public MonedaDao getMonedaEJB() {
        return monedaEJB;
    }

    public void setMonedaEJB(MonedaDao monedaEJB) {
        this.monedaEJB = monedaEJB;
    }

<<<<<<< HEAD
=======
    public PgePaises getPais() {
        return pais;
    }

    public void setPais(PgePaises pais) {
        this.pais = pais;
    }

>>>>>>> ed7c8ab95f4e17d7d3de0c1366b38e8d1895c766
    public List<PgePaises> getPaises() {
        return paises;
    }

    public void setPaises(List<PgePaises> paises) {
        this.paises = paises;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}