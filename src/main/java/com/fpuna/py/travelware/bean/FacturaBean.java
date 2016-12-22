/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ConceptoDao;
import com.fpuna.py.travelware.dao.FacturaDao;
import com.fpuna.py.travelware.dao.ProveedorDao;
import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.dao.PagoDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.dao.ViajeDetDao;
import com.fpuna.py.travelware.model.ComProveedores;
import com.fpuna.py.travelware.model.ComFacturas;
import com.fpuna.py.travelware.model.ComFacturasDet;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.ViaConceptos;
import com.fpuna.py.travelware.model.ViaViajes;
import com.fpuna.py.travelware.model.ViaViajesDet;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author damia_000
 */
@Named(value = "facturaBean")
@ViewScoped
public class FacturaBean implements Serializable {

    private List<ComFacturas> facturas;
    private List<ComFacturasDet> detallesFacturaSelected;
    private ComFacturasDet detalleFacturaSelected;
    private List<ComProveedores> proveedores;
    private List<PgeMonedas> monedas;
    private List<ViaConceptos> conceptos;
    private List<ViaViajes> viajes;

    private ComFacturas facturaSelected;
    private ViaViajes viajeSelected;
    private List<ViaViajesDet> detallesViajeSelected;
    @EJB
    private FacturaDao facturaEJB;
    @EJB
    private ProveedorDao proveedorEJB;
    @EJB
    private MonedaDao monedaEJB;
    @EJB
    private PagoDao pagoEJB;
    @EJB
    private ConceptoDao conceptoEJB;
    @EJB
    private ViajeDao viajeEJB;
    @EJB
    private ViajeDetDao viajeDetEJB;

    private LoginBean loginBean;
    private PgeMonedas moneda;
    private Date fecha;
    private int anho;

    private boolean habilitado;

    //crea una nueva instancia de Factura
    public FacturaBean() {

    }

    @PostConstruct
    public void init() {
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.detalleFacturaSelected = new ComFacturasDet();
        this.facturaSelected = new ComFacturas();
        this.facturaSelected.setMonId(this.moneda);
        this.facturaSelected.setFacFecha(new Date());
        this.detallesFacturaSelected = new ArrayList<>();
        this.facturas = facturaEJB.getAll();
        this.proveedores = proveedorEJB.getAll();
        this.monedas = monedaEJB.getAll();
        this.conceptos = conceptoEJB.getAll();
        this.viajes = viajeEJB.getAll();
        this.fecha = new Date();
        this.fecha = DateUtils.round(this.fecha, Calendar.MONTH);
        habilitado = true;
    }

    public void cambioProveedor() {
        //    this.facturaSelected.setFacNroTim(this.facturaSelected.getProId().getProNroTim());
    }

    private void clean() {
        this.facturaSelected = new ComFacturas();
        this.facturaSelected.setMonId(this.moneda);
        this.facturaSelected.setFacTotal(BigDecimal.ZERO);
        this.facturaSelected.setFacFecha(new Date());
        this.detallesFacturaSelected = new ArrayList<>();
    }

    public void onRowEdit() {
        System.out.println("Editando");
    }

    public void onRowCancel() {
        if (this.detallesViajeSelected != null)
        this.detallesViajeSelected.clear();
    }
    
    public void seleccionViaje(){
        this.detallesViajeSelected = viajeDetEJB.getAll(viajeSelected);
        RequestContext.getCurrentInstance().update("form-add:viajeDetAdd");
    }

    public void buttonAction(ActionEvent actionEvent) {
        this.facturaSelected = new ComFacturas();
        habilitado = true;
    }

    public void addFactura() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.facturaSelected.getProId() == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. Proveedor no válido. Verifique.", ""));
            return;
        }

        for (ComFacturas fac : facturas) {
            if (this.facturaSelected != null && !(fac.getFacId().equals(this.facturaSelected.getFacId()))
                    && fac.getFacNro().equals(this.facturaSelected.getFacNro())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Advertencia. " + this.facturaSelected.getFacNro() + " ya existe ",""));
                //this.clean();
                return;
            }
        }

        //Validaciones de facturas
        if ((this.facturaSelected.getFacTotal() != null) && (this.facturaSelected.getFacCambio() != null)
                && (this.facturaSelected.getFacTotal().compareTo(BigDecimal.ZERO) < 0 || this.facturaSelected.getFacCambio().compareTo(BigDecimal.ZERO) < 0)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Advertencia. Monto de la factura o tipo de cambio no pueden ser negativos. Verifique.", ""));
            return;
        }

        ComFacturas factura = new ComFacturas();
        if (this.facturaSelected.getFacId() != null) {
            factura.setFacId(this.facturaSelected.getFacId());
            factura.setFacUsuIns(this.facturaSelected.getFacUsuIns());
            factura.setFacFecIns(this.facturaSelected.getFacFecIns());
            factura.setFacUsuMod(loginBean.getUsername());
            factura.setFacFecMod(new Date());
            if (this.facturaSelected.getFacSaldo() == null && 
                    this.facturaSelected.getFacTotal() != null) {
                factura.setFacSaldo(this.facturaSelected.getFacTotal());
            } else {
                factura.setFacSaldo(this.facturaSelected.getFacSaldo());
            }
        } else {
            factura.setFacUsuIns(loginBean.getUsername());
            factura.setFacFecIns(new Date());
            factura.setFacSaldo(this.facturaSelected.getFacTotal());
        }
        factura.setProId(this.facturaSelected.getProId());
        factura.setFacNro(this.facturaSelected.getFacNro());
        factura.setFacFecha(this.facturaSelected.getFacFecha());
        factura.setMonId(this.facturaSelected.getMonId());
        factura.setFacCambio(this.facturaSelected.getFacCambio());
        factura.setFacTip(this.facturaSelected.getFacTip());
        factura.setFacIvaInc(this.facturaSelected.getFacIvaInc());
        factura.setFacTotal(this.facturaSelected.getFacTotal());
        factura.setFacDesc(this.facturaSelected.getFacDesc());
        factura.setFacEst(this.facturaSelected.getFacEst());
        factura.setFacNroTim(this.facturaSelected.getFacNroTim());
        factura.setFacFecVen(this.facturaSelected.getFacFecVen());
        factura.setFacCond(this.facturaSelected.getFacCond());
        factura.setFacRuc(this.facturaSelected.getFacRuc());
        factura.setFacCanCuo(this.facturaSelected.getFacCanCuo());
        factura.setFacImg(this.facturaSelected.getFacImg());

        facturaEJB.update(factura);
        factura = facturaEJB.getLast();
        pagoEJB.agregarCuotasFactura(factura);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! La factura " + 
                this.facturaSelected.getFacNro() + " fue guardada con éxito.", ""));
        facturas = facturaEJB.getAll();
        RequestContext.getCurrentInstance().update("factura-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgFacturaAdd').hide();");
    }

    public void deleteFactura() {
        FacesContext context = FacesContext.getCurrentInstance();
        facturaEJB.delete(this.facturaSelected);
        context.addMessage(null, new FacesMessage("Felicidades! La factura " + 
                this.facturaSelected.getFacNro() + " fue anulada con éxito.", ""));
        facturas = facturaEJB.getAll();
        RequestContext.getCurrentInstance().update("factura-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgFacturaAdd').hide();");
    }

    public void onRowSelect(SelectEvent event) {
        this.facturaSelected = (ComFacturas) event.getObject();
        RequestContext.getCurrentInstance().update("factura-form:dtFactura");
    }
    
    public void agregarDetalle(){
        this.detallesFacturaSelected.add(new ComFacturasDet());
    }

    public List<ComFacturas> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<ComFacturas> facturas) {
        this.facturas = facturas;
    }

    public List<ComProveedores> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<ComProveedores> proveedores) {
        this.proveedores = proveedores;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public ComFacturas getFacturaSelected() {
        return facturaSelected;
    }

    public void setFacturaSelected(ComFacturas facturaSelected) {
        this.facturaSelected = facturaSelected;
    }

    public PgeMonedas getMoneda() {
        return moneda;
    }

    public void setMoneda(PgeMonedas moneda) {
        this.moneda = moneda;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getAnho() {
        return anho;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getValidezVenc(Date fechaVenc) {
        Date rounded = DateUtils.truncate(fechaVenc, Calendar.MONTH);
        if (rounded.equals(this.fecha) || rounded.before(this.fecha)) {
            return "#FF0000";
        } else {
            return "#000000";
        }
    }

    public List<ComFacturasDet> getDetallesFacturaSelected() {
        return detallesFacturaSelected;
    }

    public void setDetallesFacturaSelected(List<ComFacturasDet> detallesFacturaSelected) {
        this.detallesFacturaSelected = detallesFacturaSelected;
    }

    public ComFacturasDet getDetalleFacturaSelected() {
        return detalleFacturaSelected;
    }

    public void setDetalleFacturaSelected(ComFacturasDet detalleFacturaSelected) {
        this.detalleFacturaSelected = detalleFacturaSelected;
    }

    public List<ViaConceptos> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<ViaConceptos> conceptos) {
        this.conceptos = conceptos;
    }

    public ViaViajes getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(ViaViajes viajeSelected) {
        this.viajeSelected = viajeSelected;
    }

    public List<ViaViajes> getViajes() {
        return viajes;
    }

    public void setViajes(List<ViaViajes> viajes) {
        this.viajes = viajes;
    }

    public List<ViaViajesDet> getDetallesViajeSelected() {
        return detallesViajeSelected;
    }

    public void setDetallesViajeSelected(List<ViaViajesDet> detallesViajeSelected) {
        this.detallesViajeSelected = detallesViajeSelected;
    }

}
