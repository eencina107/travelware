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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import static org.hibernate.internal.util.io.StreamCopier.BUFFER_SIZE;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author damia_000
 */
@Named(value = "viajeDetBean")
@ViewScoped
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

    String nombreArchivo = null;
    String nombreCarpetaImg;

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
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. Concepto de viaje debe ser igual a concepto en detalle de factura: " +
                                                        this.viajeDetSelected.getFadId().getConId().getConDesc()+". Verifique.", ""));
            return;
        }

        if (this.viajeDetSelected.getFadId() != null && this.viajeDetSelected.getFadId().getFadUti() == 'S' ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. Detalle de factura ya fue utilizado. Verifique.", ""));
            return;
        }

        ViaViajesDet viajeDet = new ViaViajesDet();
        if (this.viajeDetSelected.getVidId()!=null){ //modificacion
            if (this.nombreArchivo != null)
                viajeDet.setVidImg(this.nombreArchivo);
            else
                viajeDet.setVidImg(this.viajeDetSelected.getVidImg());
            viajeDet.setVidId(this.viajeDetSelected.getVidId());
            viajeDet.setVidUsuIns(this.viajeDetSelected.getVidUsuIns());
            viajeDet.setVidFecIns(this.viajeDetSelected.getVidFecIns());
            viajeDet.setVidUsuMod(loginBean.getUsername());
            viajeDet.setVidFecMod(new Date());
        }
        else {
            viajeDet.setVidImg(this.nombreArchivo);
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
        
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El detalle "+viajeDet.getVidDesc()+" de "+viajeSelected.getViaDesc()+" ha sido guardada con éxito.", ""));
        this.facturasDet = facturaDetEJB.getAllNoUti();
        this.viajesDet = viajeDetEJB.getAll(viajeSelected);
        this.viajes = viajeEJB.getAll();
        RequestContext.getCurrentInstance().update("viaje-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgViajeDetAdd').hide();");
    }
    
    public void deleteViajeDet(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.viajeDetSelected.getViaId()!= null && this.viajeDetSelected.getViaId().getViaCantVend()!= null &&
                this.viajeDetSelected.getViaId().getViaCantVend() > 0 ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error. No se puede borrar detalle de viaje con paquetes vendidos.", ""));
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

        context.addMessage(null, new FacesMessage("Felicidades! El detalle de "+viajeSelected.getViaDesc()+" fue borrado con éxito.", ""));

        this.facturasDet = facturaDetEJB.getAllNoUti();
        this.viajesDet = viajeDetEJB.getAll(viajeSelected);
        this.viajes = viajeEJB.getAll();
        RequestContext.getCurrentInstance().update("viaje-form");
        this.clean();
        //RequestContext.getCurrentInstance().execute("PF('dlgViajeDetAdd').hide();");
    }
    
    public void onRowSelect(SelectEvent event){
        this.viajeDetSelected = (ViaViajesDet) event.getObject();
        RequestContext.getCurrentInstance().update("viaje-form:dtViajeDet");
    }
    
    public void onRowToggle(ViaViajes viaje){
        this.viajeSelected = viaje;
    }

    public void handleFileUpload(FileUploadEvent event) {
         ExternalContext extContext = javax.faces.context.FacesContext.getCurrentInstance().getExternalContext();
         Boolean existeDir; 
         File targetFolder;
         File result;
            try {
                targetFolder = new File("/opt/py.travelware/vidImg/");
                result = new File("/opt/py.travelware/vidImg/"+event.getFile().getFileName());
                existeDir = targetFolder.canRead();
                if (!existeDir){
                    targetFolder.mkdirs();
                }
                //this.getClass().getResource("/").getPath(); 
                
                System.out.println("/opt/py.travelware/vidImg/" + event.getFile().getFileName()+" subido");
               } catch (Exception e) {
                   return;
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(result);

                byte[] buffer = new byte[BUFFER_SIZE];

                int bulk;
                InputStream inputStream = event.getFile().getInputstream();
                while (true) {
                    bulk = inputStream.read(buffer);
                    if (bulk < 0) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, bulk);
                    fileOutputStream.flush();
                }

                fileOutputStream.close();
                inputStream.close();

                FacesMessage msg = 
                            new FacesMessage("Propiedades "+ "Nombre: " +
                            event.getFile().getFileName() + " Tamaño: " + 
                            event.getFile().getSize() / 1024 + 
                            " Kb Tipo: " + 
                            event.getFile().getContentType() + 
                                    " Subido correctamente.", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                this.nombreArchivo = event.getFile().getFileName();

            } catch (IOException e) {
                e.printStackTrace();

                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al subir", "");
                FacesContext.getCurrentInstance().addMessage(null, error);
            }     
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

    public String getNombreCarpetaImg(String nombreArchivo) {
        return nombreCarpetaImg+nombreArchivo;
    }
}
