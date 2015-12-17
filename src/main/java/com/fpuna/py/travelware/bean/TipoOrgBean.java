/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.TipoOrgDao;
import com.fpuna.py.travelware.model.PgeTipoOrg;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "tipoOrgBean")
@SessionScoped
public class TipoOrgBean implements Serializable{
    private List<PgeTipoOrg> tiposOrg;
    private PgeTipoOrg tipoOrgSelected;
    @EJB
    private TipoOrgDao tipoOrgEJB;
    private String tipCodigo;
    private String tipDescripcion;
    
    public TipoOrgBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        tipoOrgSelected = new PgeTipoOrg();
        tiposOrg = tipoOrgEJB.getAll();
    }

    private void clean() {
        tipoOrgSelected = new PgeTipoOrg();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        tipoOrgSelected = new PgeTipoOrg();
        this.tipCodigo = tipoOrgSelected.getTipCodigo();
        this.tipDescripcion = tipoOrgSelected.getTipDescripcion();
    }
    
    public void addTipoOrg(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgeTipoOrg tipo:tiposOrg){
            if (tipo.getTipCodigo().toUpperCase().equals(tipCodigo.toUpperCase()) && !Objects.equals(tipo.getTipId(), this.tipoOrgSelected.getTipId())){
                context.addMessage(null, new FacesMessage("Advertencia", "El tipo "+this.tipoOrgSelected.getTipCodigo()+" ya existe"));
                this.clean();
                return;
            }
            if (tipo.getTipDescripcion().toUpperCase().equals(tipDescripcion.toUpperCase()) && !Objects.equals(tipo.getTipId(), this.tipoOrgSelected.getTipId())){
                context.addMessage(null, new FacesMessage("Advertencia", "El tipo "+this.tipoOrgSelected.getTipDescripcion()+" ya existe"));
                this.clean();
                return;
            }
        }
        
        
        PgeTipoOrg tipoOrg= new PgeTipoOrg();
        if (this.tipoOrgSelected.getTipId()!= null){
            tipoOrg.setTipId(this.tipoOrgSelected.getTipId());
        }
        tipoOrg.setTipCodigo(tipCodigo);
        tipoOrg.setTipDescripcion(tipDescripcion);
        tipoOrgEJB.update(tipoOrg);
        context.addMessage("Mensaje", new FacesMessage("Felicidades", tipoOrg.getTipDescripcion()+" fue guardado con éxito"));
        tiposOrg = tipoOrgEJB.getAll();
        this.clean();
    }
    
    public void deleteTipoOrg(){
        tipoOrgEJB.delete(tipoOrgSelected);
        tiposOrg = tipoOrgEJB.getAll();
        RequestContext.getCurrentInstance().update("tipoOrg-form:dtTipoOrg");
 
    }
    
    public void onRowSelect(SelectEvent event){
        this.tipoOrgSelected = (PgeTipoOrg) event.getObject();

    }

    public List<PgeTipoOrg> getTiposOrg() {
        return tiposOrg;
    }

    public void setTiposOrg(List<PgeTipoOrg> tiposOrg) {
        this.tiposOrg = tiposOrg;
    }

    public PgeTipoOrg getTipoOrgSelected() {
        return tipoOrgSelected;
    }

    public void setTipoOrgSelected(PgeTipoOrg tipoOrgSelected) {
        this.tipoOrgSelected = tipoOrgSelected;
        this.tipCodigo = tipoOrgSelected.getTipCodigo();
        this.tipDescripcion = tipoOrgSelected.getTipDescripcion();
    }

    public TipoOrgDao getTipoOrgEJB() {
        return tipoOrgEJB;
    }

    public void setTipoOrgEJB(TipoOrgDao tipoOrgEJB) {
        this.tipoOrgEJB = tipoOrgEJB;
    }

    public String getTipCodigo() {
        return tipCodigo;
    }

    public void setTipCodigo(String tipCodigo) {
        this.tipCodigo = tipCodigo;
    }

    public String getTipDescripcion() {
        return tipDescripcion;
    }

    public void setTipDescripcion(String tipDescripcion) {
        this.tipDescripcion = tipDescripcion;
    }
    
    
    
}
