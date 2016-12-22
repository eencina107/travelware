/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ConceptoDao;
import com.fpuna.py.travelware.dao.FacturaDetDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.dao.ViajeDetDao;
import com.fpuna.py.travelware.model.ViaConceptos;
import com.fpuna.py.travelware.model.ComFacturas;
import com.fpuna.py.travelware.model.ComFacturasDet;
import com.fpuna.py.travelware.model.ViaViajes;
import com.fpuna.py.travelware.model.ViaViajesDet;
import java.io.Serializable;
import java.math.BigDecimal;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author damia_000
 */
@Named(value = "facturaDetBean")
@ViewScoped
public class FacturaDetBean implements Serializable{
    private List<ComFacturasDet> facturasDet;
    private List<ViaConceptos> conceptos;
    private ComFacturasDet facturaDetSelected;
    private ComFacturas facturaSelected;
    private List<ViaViajes> viajes;
    private List<ViaViajesDet> detallesViaje;
    
    private ViaViajes viajeSelected;

    @EJB
    private FacturaDetDao facturaDetEJB;
    @EJB
    private ConceptoDao conceptoEJB;
    @EJB
    private ViajeDao viajeEJB;
    @EJB
    private ViajeDetDao viajeDetEJB;

    private LoginBean loginBean;

    private ViaConceptos concepto;

    //crea una nueva instancia de FacturaDet
    public FacturaDetBean(){
        
    }
    
    @PostConstruct
    public void init(){
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.facturaDetSelected = new ComFacturasDet();
        this.facturaDetSelected.setConId(this.concepto);
        this.conceptos = conceptoEJB.getAll();
        
        this.viajes = viajeEJB.getAll();
    }

    private void clean() {
        this.facturaDetSelected = new ComFacturasDet();
        this.concepto = new ViaConceptos();
    }
    
    
    public void seleccionViaje(){
        this.detallesViaje = viajeDetEJB.getAll(this.viajeSelected);
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.facturaDetSelected = new ComFacturasDet();
    }
    
    
    
    public void addFacturaDet(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (facturasDet!=null){
            for (ComFacturasDet facturaDet:facturasDet){
                if (this.facturaDetSelected.getFadId()!=null && facturaDet.getFadId() != this.facturaDetSelected.getFadId() &&
                        facturaDet.getFadNroSec() == this.facturaDetSelected.getFadNroSec()) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. "+ this.facturaDetSelected.getFacId().getFacNro()+" "+
                                                                               this.facturaDetSelected.getFadNroSec()+" ya existe. Verifique.", ""));
                    this.clean();
                    return;
                }
            }
        }

        //Validaciones de monto de factura
        if ((this.facturaDetSelected.getFadMtoGra()!=null && this.facturaDetSelected.getFadMtoGra().compareTo(BigDecimal.ZERO) < 0) ||
              (this.facturaDetSelected.getFadMtoImp()!=null && this.facturaDetSelected.getFadMtoImp().compareTo(BigDecimal.ZERO) < 0) ||
              (this.facturaDetSelected.getFadMtoExe()!=null && this.facturaDetSelected.getFadMtoExe().compareTo(BigDecimal.ZERO) < 0) ||
              (this.facturaDetSelected.getFadValUni()!=null && this.facturaDetSelected.getFadValUni().compareTo(BigDecimal.ZERO) < 0) ){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. Monto del Detalle no puede ser negativo. Verifique.", ""));
            return;
        }

        //Validaciones de monto de factura
        if ((this.facturaDetSelected.getConId()!=null && this.facturaDetSelected.getFadCant()==null) ||
              (this.facturaDetSelected.getConId()!=null && this.facturaDetSelected.getFadValUni()==null)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. Debe ingresar cantidad y valor unitario para " + 
                                                        this.facturaDetSelected.getConId().getConDesc() + ". Verifique.", ""));
            return;
        }

        Integer nroSec ;
        ComFacturasDet facturaDet = new ComFacturasDet();
        if (this.facturaDetSelected.getFadId()!=null){
            facturaDet.setFadId(this.facturaDetSelected.getFadId());
            facturaDet.setFadUsuIns(this.facturaDetSelected.getFadUsuIns());
            facturaDet.setFadFecIns(this.facturaDetSelected.getFadFecIns());
            facturaDet.setFadUsuMod(loginBean.getUsername());
            facturaDet.setFadFecMod(new Date());
            nroSec = this.facturaDetSelected.getFadNroSec();
        }
        else {
            facturaDet.setFadUsuIns(loginBean.getUsername());
            facturaDet.setFadFecIns(new Date());
            nroSec = facturaDetEJB.getMaxByFactura(facturaSelected)+1;
        }
        facturaDet.setFadNroSec(nroSec);
        facturaDet.setFacId(facturaSelected);
        facturaDet.setFadDesc(this.facturaDetSelected.getFadDesc());
        facturaDet.setArtId(this.facturaDetSelected.getArtId());
        facturaDet.setFadCant(this.facturaDetSelected.getFadCant());
        facturaDet.setFadValUni(this.facturaDetSelected.getFadValUni());
        facturaDet.setFadValor(this.facturaDetSelected.getFadValor());
        facturaDet.setFadPorImp(this.facturaDetSelected.getFadPorImp());
        facturaDet.setFadMtoGra(this.facturaDetSelected.getFadMtoGra());
        facturaDet.setFadMtoImp(this.facturaDetSelected.getFadMtoImp());
        facturaDet.setFadMtoExe(this.facturaDetSelected.getFadMtoExe());
        facturaDet.setConId(this.facturaDetSelected.getConId());
        facturaDet.setFadUti('N');
        facturaDet.setFadMtoTot(this.facturaDetSelected.getFadMtoGra().add(this.facturaDetSelected.getFadMtoImp().add(this.facturaDetSelected.getFadMtoExe())));
        
        facturaDetEJB.update(facturaDet);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El detalle "+nroSec+" de la factura "+facturaSelected.getFacNro()+" ha sido guardada con éxito.", ""));
        facturasDet = facturaDetEJB.getAll(facturaSelected);
        RequestContext.getCurrentInstance().update("factura-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgFacturaDetAdd').hide();");
    }

    public List<ViaViajes> getViajes() {
        return viajes;
    }

    public void setViajes(List<ViaViajes> viajes) {
        this.viajes = viajes;
    }

    public List<ViaViajesDet> getDetallesViaje() {
        return detallesViaje;
    }

    public void setDetallesViaje(List<ViaViajesDet> detallesViaje) {
        this.detallesViaje = detallesViaje;
    }

    public ViaViajes getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(ViaViajes viajeSelected) {
        this.viajeSelected = viajeSelected;
    }
    
    public void deleteFacturaDet(){
        FacesContext context = FacesContext.getCurrentInstance();
        facturaDetEJB.delete(this.facturaDetSelected);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El detalle de la factura "+facturaSelected.getFacNro()+" ha sido borrado con éxito.", ""));
        facturasDet = facturaDetEJB.getAll(facturaSelected);
        RequestContext.getCurrentInstance().update("factura-form");
        this.clean();
        //RequestContext.getCurrentInstance().execute("PF('dlgFacturaDetAdd').hide();");
    }
    
    public void onRowSelect(SelectEvent event){
        this.facturaDetSelected = (ComFacturasDet) event.getObject();
        RequestContext.getCurrentInstance().update("factura-form:dtFacturaDet");
    }
    
    public void onRowToggle(ComFacturas factura){
        this.facturaSelected = factura;
    }

    public List<ComFacturasDet> getFacturasDet() {
        facturasDet = facturaDetEJB.getAll(facturaSelected);
        return facturasDet;
    }

    public void setFacturasDet(List<ComFacturasDet> facturasDet) {
        this.facturasDet = facturasDet;
    }

    public List<ViaConceptos> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<ViaConceptos> conceptos) {
        this.conceptos = conceptos;
    }

    public ComFacturasDet getFacturaDetSelected() {
        return facturaDetSelected;
    }

    public void setFacturaDetSelected(ComFacturasDet facturaDetSelected) {
        this.facturaDetSelected = facturaDetSelected;
    }
    
    public ViaConceptos getConcepto() {
        return this.concepto;
    }

    public void setConcepto(ViaConceptos concepto) {
        this.concepto = concepto;
    }
}
