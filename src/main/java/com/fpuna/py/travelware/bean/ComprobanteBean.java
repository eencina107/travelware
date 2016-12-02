/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ComprobanteDao;
import com.fpuna.py.travelware.dao.ComprobanteDetDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.PagComprobantes;
import com.fpuna.py.travelware.model.PagComprobantesDet;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
@Named(value = "comprobanteBean")
@ViewScoped
public class ComprobanteBean implements Serializable{
    private PagComprobantes comprobante;
    private PagComprobantesDet comprobanteDetSelected;
    private List<PagComprobantes> comprobantes;
    private List<PagComprobantesDet> comprobantesDet;
    
    private List<PgePersonas> personas;
    private List<PgeMonedas> monedas;
    
    private List<ViaViajes> viajesPersona;
    
    @EJB
    private ComprobanteDao comprobanteEJB;
    @EJB
    private ComprobanteDetDao comprobanteDetEJB;
    
    @EJB
    private PersonaDao personaEJB;
    @EJB
    private MonedaDao monedaEJB;
    
    @EJB
    private ViajeDao viajeEJB;
    
    private LoginBean loginBean;
    
    //crea una nueva instancia de ComprobanteBean
    public ComprobanteBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        FacesContext context= javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        comprobante = new PagComprobantes();
        comprobanteDetSelected = new PagComprobantesDet();
        comprobantes = comprobanteEJB.getAll();
        viajesPersona = new ArrayList<>();
        personas = personaEJB.getAll();
        monedas = monedaEJB.getAll();
    }

    private void clean() {
        this.comprobante = new PagComprobantes();
    }
    
    private void cleanDet(PgePersonas persona) {
        this.comprobanteDetSelected = new PagComprobantesDet();
        this.viajesPersona = persona.getViaViajesList();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        comprobanteDetSelected = new PagComprobantesDet();
    }
    
    public void addComprobante(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PagComprobantes com:comprobantes){
            if (com.getComConc().equals(this.comprobante.getComConc()) &&
                    com.getComTimbrado().equals(this.comprobante.getComTimbrado()) &&
                    com.getComNumDoc() == this.comprobante.getComNumDoc() &&
                    com.getComEstado().equals(this.comprobante.getComEstado().equals('A'))){
                context.addMessage(null, new FacesMessage("Advertencia. Este Nro. de Documento ya fue registrado"));
                this.clean();
                return;
            }
        }
        
        PagComprobantes comp = new PagComprobantes();
        if (this.comprobante.getComIdTran()!=null){
            comp.setComIdTran(this.comprobante.getComIdTran());
            comp.setComUsuIns(this.comprobante.getComUsuIns());
            comp.setComFecIns(this.comprobante.getComFecIns());
            comp.setComUsuMod(loginBean.getUsername());
            comp.setComFecMod(new Date());
        }
        else
        {
            comp.setComUsuIns(loginBean.getUsername());
            comp.setComFecIns(new Date());
        }
        
        comp.setComConc(this.comprobante.getComConc());
        comp.setComCondicion(this.comprobante.getComCondicion());
        comp.setComCotizacion(this.comprobante.getComCotizacion());
        comp.setComEstado(this.comprobante.getComEstado());
        comp.setComFecEmis(this.comprobante.getComFecEmis());
        comp.setComFecVenc(this.comprobante.getComFecVenc());
        comp.setComNumDoc(this.comprobante.getComNumDoc());
        comp.setComObservacion(this.comprobante.getComObservacion());
        comp.setComTimbrado(this.comprobante.getComTimbrado());
        comp.setComTipDoc(this.comprobante.getComTipDoc());
        comp.setMonId(this.comprobante.getMonId());
        comp.setPerId(this.comprobante.getPerId());
        comp.setPagComprobantesDetList(this.comprobantesDet);
        comprobanteEJB.create(comp);
        context.addMessage(null, new FacesMessage("Felicidades! La transacción fue guardada con éxito"));
        comprobantesDet = new ArrayList<>();
        RequestContext.getCurrentInstance().update("comprobantesDet-form:dtComprobanteDet");
        this.clean();
    }
    
    public void addComprobanteDet(){
        Double precio,gravada, exenta, impuesto, total;
        FacesContext context = FacesContext.getCurrentInstance();
        Integer cant = comprobantesDet.size();
        this.comprobanteDetSelected.setCdeNroDet(cant+1);
        for (PagComprobantesDet comDet:this.comprobantesDet){
            if(comDet.getViaId().equals(this.comprobanteDetSelected.getViaId()) && comDet.getCdeNumCuota() == this.comprobanteDetSelected.getCdeNumCuota()){
                context.addMessage(null, new FacesMessage("Advertencia. La cuota "+comDet.getCdeNumCuota()+" del viaje "+comDet.getViaId().getViaDesc()+" ya fue registrada"));
                this.clean();
                return;
            }
        }
        
        //calculo de valores de impuesto
        precio = this.comprobanteDetSelected.getViaId().getViaCost().doubleValue();
        if (this.comprobanteDetSelected.getCdePorRde().intValue()!=0){
            impuesto = (this.comprobanteDetSelected.getCdeTot().doubleValue()
                    * this.comprobanteDetSelected.getCdePorRde().doubleValue())/100;
            gravada = precio - impuesto;
            exenta = new Double("0");
        }
        else
        {
            impuesto = new Double("0");
            gravada = new Double("0");
            exenta = this.comprobanteDetSelected.getCdeTot().doubleValue();
        }
        
        PagComprobantesDet detalle = new PagComprobantesDet();
        if (this.comprobanteDetSelected.getComDetId()!=null) {
            detalle.setComId(this.comprobanteDetSelected.getComId());
            detalle.setCdeFecIns(this.comprobanteDetSelected.getCdeFecIns());
            detalle.setCdeUsuIns(this.comprobanteDetSelected.getCdeUsuIns());
            detalle.setCdeFecMod(new Date());
            detalle.setCdeUsuMod(loginBean.getUsername());
            detalle.setCdeNroDet(this.comprobanteDetSelected.getCdeNroDet());
        }
        else
        {
            detalle.setComId(this.comprobante);
            detalle.setCdeFecIns(new Date());
            detalle.setCdeUsuIns(loginBean.getUsername());
            detalle.setCdeNroDet(cant+1);
        }
        detalle.setViaId(this.comprobanteDetSelected.getViaId());
        detalle.setCdeExe(BigDecimal.valueOf(exenta));
        detalle.setCdeGra(BigDecimal.valueOf(gravada));
        detalle.setCdeIva(BigDecimal.valueOf(impuesto));
        detalle.setCdePorRde(this.comprobanteDetSelected.getCdePorRde());
        detalle.setCdeNumCuota(this.comprobanteDetSelected.getCdeNumCuota());
        detalle.setCdeTot(this.comprobanteDetSelected.getCdeTot());
        boolean exito= comprobantesDet.add(detalle);
        if (!exito){
            context.addMessage(null, new FacesMessage("Error. No se pudo guardar el detalle"));
        }
        else
        {
            context.addMessage(null, new FacesMessage("Felicidades! Se ha agregado el detalle con éxito"));
        }
        RequestContext.getCurrentInstance().update("comprobantesDet-form:dtComprobanteDet");
        this.cleanDet(this.comprobante.getPerId());
    }
    
    public void deleteComprobante(){
        comprobanteEJB.delete(this.comprobante);
        comprobantes = comprobanteEJB.getAll();
        RequestContext.getCurrentInstance().update("comprobante-form");
    }
    
    public void deleteComprobanteDet(){
        comprobanteDetEJB.delete(this.comprobanteDetSelected);
        comprobantesDet = comprobanteDetEJB.getAll(comprobante);
        RequestContext.getCurrentInstance().update("comprobante-form:dtComprobante");
    }
    
    public void onRowSelect(SelectEvent event){
        this.comprobanteDetSelected = (PagComprobantesDet) event.getObject();
        RequestContext.getCurrentInstance().update("comprobante-form:dtComprobante");
    }

    public PagComprobantes getComprobante() {
        return comprobante;
    }

    public void setComprobante(PagComprobantes comprobante) {
        this.comprobante = comprobante;
    }

    public PagComprobantesDet getComprobanteDetSelected() {
        return comprobanteDetSelected;
    }

    public void setComprobanteDetSelected(PagComprobantesDet comprobanteDetSelected) {
        this.comprobanteDetSelected = comprobanteDetSelected;
    }

    public List<PagComprobantes> getComprobantes() {
        return comprobantes;
    }

    public void setComprobantes(List<PagComprobantes> comprobantes) {
        this.comprobantes = comprobantes;
    }

    public List<PagComprobantesDet> getComprobantesDet() {
        return comprobantesDet;
    }

    public void setComprobantesDet(List<PagComprobantesDet> comprobantesDet) {
        this.comprobantesDet = comprobantesDet;
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public List<ViaViajes> getViajesPersona() {
        return viajesPersona;
    }

    public void setViajesPersona(List<ViaViajes> viajesPersona) {
        this.viajesPersona = viajesPersona;
    }
    
    
}
