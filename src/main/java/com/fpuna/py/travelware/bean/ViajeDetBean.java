/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ConceptoDao;
import com.fpuna.py.travelware.dao.FacturaDetDao;
import com.fpuna.py.travelware.dao.ViajeDetDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.model.ViaConceptos;
import com.fpuna.py.travelware.model.ComFacturasDet;
import com.fpuna.py.travelware.model.ViaViajes;
import com.fpuna.py.travelware.model.ViaViajesDet;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author damia_000
 */
@Named(value = "viajeDetBean")
@SessionScoped
public class ViajeDetBean implements Serializable{
    private List<ViaViajesDet> viajesDet;
    private List<ViaConceptos> conceptos, conceptosReq;
    private List<ComFacturasDet> facturasDet;
    private List<PgeMonedas> monedas;
    private List<ViaViajes> viajes;
    private ViaViajesDet viajeDetSelected;
    private ViaViajes viajeSelected;

    @EJB
    private ViajeDetDao viajeDetEJB;
    @EJB
    private ConceptoDao conceptoEJB;
    @EJB
    private FacturaDetDao facturaDetEJB;
    @EJB
    private MonedaDao monedaEJB;
    @EJB
    private ViajeDao viajeEJB;

    private LoginBean loginBean;

    private ViaConceptos concepto, conceptoReq;
    private ComFacturasDet facturaDet;
    private PgeMonedas moneda;
    private ViaViajes viaje;

    //crea una nueva instancia de ViajeDet
    public ViajeDetBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.viajeDetSelected = new ViaViajesDet();
        this.viajeDetSelected.setConId(this.concepto);
        this.viajeDetSelected.setFadId(this.facturaDet);
        this.viajeDetSelected.setMonId(this.moneda);
        this.viajeDetSelected.setViaId(this.viaje);
        this.conceptos = conceptoEJB.getAll();
        this.conceptosReq = conceptoEJB.getAllReq();
        this.facturasDet = facturaDetEJB.getAllNoUti();
        this.monedas = monedaEJB.getAll();
        this.viajes = viajeEJB.getAll();
    }

    private void clean() {
        this.viajeDetSelected = new ViaViajesDet();
        this.concepto = new ViaConceptos();
        this.conceptoReq = new ViaConceptos();
        this.facturaDet = new ComFacturasDet();
        this.viaje = new ViaViajes();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.viajeDetSelected = new ViaViajesDet();
    }
    
    public void addViajeDet(){
        FacesContext context = FacesContext.getCurrentInstance();

        //Validaciones de concepto
        if (this.viajeDetSelected.getFadId() != null && !(this.viajeDetSelected.getConId().equals(this.viajeDetSelected.getFadId().getConId()))) {
            context.addMessage(null, new FacesMessage("Advertencia. Concepto de viaje debe ser igual a concepto en detalle de factura: " +
                                                        this.viajeDetSelected.getFadId().getConId().getConDesc()+". Verifique."));
            return;
        }

        if (this.viajeDetSelected.getFadId() != null && this.viajeDetSelected.getFadId().getFadUti() == 'S' ) {
            context.addMessage(null, new FacesMessage("Advertencia. Detalle de factura ya fue utilizado. Verifique."));
            return;
        }

        ViaViajesDet viajeDet = new ViaViajesDet();
        if (this.viajeDetSelected.getVidId()!=null){ //modificacion
            viajeDet.setVidId(this.viajeDetSelected.getVidId());
            viajeDet.setVidUsuIns(this.viajeDetSelected.getVidUsuIns());
            viajeDet.setVidFecIns(this.viajeDetSelected.getVidFecIns());
            viajeDet.setVidUsuMod(loginBean.getUsername());
            viajeDet.setVidFecMod(new Date());
        }
        else {
            viajeDet.setVidUsuIns(loginBean.getUsername());
            viajeDet.setVidFecIns(new Date());
        }
        viajeDet.setViaId(viajeSelected);
        viajeDet.setVidDesc(this.viajeDetSelected.getVidDesc());
        viajeDet.setConId(this.viajeDetSelected.getConId());
        viajeDet.setVidCantVend(0);

        if(this.viajeDetSelected.getFadId() != null) { //si tiene asociada una factura (paquete turistico, pasaje aereo, etc.)
            viajeDet.setFadId(this.viajeDetSelected.getFadId());
            viajeDet.setVidCantTot(this.viajeDetSelected.getFadId().getFadCant());
            viajeDet.setMonId(this.viajeDetSelected.getFadId().getFacId().getMonId());
            viajeDet.setVidMonto(this.viajeDetSelected.getFadId().getFadValUni());
            viajeDet.setVidTip('I'); //Tipo --> I - Incluido
        }
        else { //si no tiene asociada una factura (actividad extra, entradas a un lugar, propinas, etc.)
            viajeDet.setVidCantTot(0);
            viajeDet.setMonId(this.viajeDetSelected.getMonId());
            viajeDet.setVidMonto(this.viajeDetSelected.getVidMonto());
            viajeDet.setVidTip(this.viajeDetSelected.getVidTip()); //Tipo --> I - Incluido / N - No Incluido
        }
        viajeDetEJB.update(viajeDet);

        //Actualizamos el Detalle de la Factura como Utilizado
        if (this.viajeDetSelected.getFadId() != null) {
            this.viajeDetSelected.getFadId().setFadUti('S');
            facturaDetEJB.update(this.viajeDetSelected.getFadId());
        }

        Integer maxCant = 0;
        //Actualizamos la cantidad total del viaje si corresponde
        for (ViaConceptos con: conceptosReq){
            //System.out.println("Concepto: "+con.getConDesc());
            Integer actCant = viajeDetEJB.getCantTotCon(viajeDet.getViaId(), con);
            if(actCant == 0 ) {//Si hay algun concepto requerido que no este cargado entonces salimos del bucle
                maxCant = 0;
                break;
            }
            else if (actCant > maxCant)
                maxCant = actCant;
        }
        viajeDet.getViaId().setViaCantTot(maxCant);
        viajeEJB.update(viajeDet.getViaId());
        
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El detalle "+viajeDet.getVidDesc()+" del viaje "+viajeSelected.getViaDesc()+" ha sido guardada con éxito"));
        this.facturasDet = facturaDetEJB.getAllNoUti();
        this.viajesDet = viajeDetEJB.getAll(viajeSelected);
        this.viajes = viajeEJB.getAll();
        this.clean();
    }
    
    public void deleteViajeDet(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.viajeDetSelected.getViaId()!= null && this.viajeDetSelected.getViaId().getViaCantVend() > 0 ) {
            context.addMessage(null, new FacesMessage("Error. No se puede borrar detalle de viaje con paquetes vendidos."));
            return;
        }

        //Borramos el Detalle del Viaje
        viajeDetEJB.delete(this.viajeDetSelected);

        //Actualizamos el Detalle de la Factura como No Utilizado
        if (this.viajeDetSelected.getFadId() != null) {
            this.viajeDetSelected.getFadId().setFadUti('N');
            facturaDetEJB.update(this.viajeDetSelected.getFadId());
        }

        Integer maxCant = 0;
        //Actualizamos la cantidad total del viaje si corresponde
        for (ViaConceptos con: conceptosReq){
            Integer actCant = viajeDetEJB.getCantTotCon(this.viajeDetSelected.getViaId(), con);
            if(actCant == 0 ) {//Si hay algun concepto requerido que no este cargado entonces salimos del bucle
                maxCant = 0;
                break;
            }
            else if (actCant > maxCant)
                maxCant = actCant;
        }
        this.viajeDetSelected.getViaId().setViaCantTot(maxCant);
        viajeEJB.update(this.viajeDetSelected.getViaId());

        this.facturasDet = facturaDetEJB.getAllNoUti();
        this.viajesDet = viajeDetEJB.getAll(viajeSelected);
        this.viajes = viajeEJB.getAll();
        RequestContext.getCurrentInstance().update("viaje-form:dtViaje:dtViajeDet");
    }
    
    public void onRowSelect(SelectEvent event){
        this.viajeDetSelected = (ViaViajesDet) event.getObject();
        RequestContext.getCurrentInstance().update("viaje-form:dtViajeDet");
    }
    
    public void onRowToggle(ViaViajes viaje){
        this.viajeSelected = viaje;
    }

    public List<ViaViajesDet> getViajesDet() {
        viajesDet = viajeDetEJB.getAll(viajeSelected);
        return viajesDet;
    }

    public void setViajesDet(List<ViaViajesDet> viajesDet) {
        this.viajesDet = viajesDet;
    }

    public List<ViaConceptos> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<ViaConceptos> conceptos) {
        this.conceptos = conceptos;
    }

    public List<ViaConceptos> getConceptosReq() {
        return conceptosReq;
    }

    public void setConceptosReq(List<ViaConceptos> conceptosReq) {
        this.conceptosReq = conceptosReq;
    }

    public List<ComFacturasDet> getFacturasDet() {
        return facturasDet;
    }

    public void setFacturasDet(List<ComFacturasDet> facturasDet) {
        this.facturasDet = facturasDet;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public ViaViajesDet getViajeDetSelected() {
        return viajeDetSelected;
    }

    public void setViajeDetSelected(ViaViajesDet viajeDetSelected) {
        this.viajeDetSelected = viajeDetSelected;
    }
    
    public ViaConceptos getConcepto() {
        return this.concepto;
    }

    public void setConcepto(ViaConceptos concepto) {
        this.concepto = concepto;
    }

    public ViaConceptos getConceptoReq() {
        return this.conceptoReq;
    }

    public void setConceptoReq(ViaConceptos conceptoReq) {
        this.conceptoReq = conceptoReq;
    }

    public ComFacturasDet getFacturaDet() {
        return this.facturaDet;
    }

    public void setFacturaDet(ComFacturasDet facturaDet) {
        this.facturaDet = facturaDet;
    }

    public PgeMonedas getMoneda() {
        return moneda;
    }

    public void setMoneda(PgeMonedas moneda) {
        this.moneda = moneda;
    }
}
