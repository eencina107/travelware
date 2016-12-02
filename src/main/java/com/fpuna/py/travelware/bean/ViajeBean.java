/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.MonedaDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.PgeMonedas;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import static org.hibernate.internal.util.io.StreamCopier.BUFFER_SIZE;
import org.primefaces.context.ApplicationContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "viajeBean")
@ViewScoped
public class ViajeBean implements Serializable{
    private List<ViaViajes> viajes;
    private List<PgeMonedas> monedas;
    private ViaViajes viajeSelected;
    @EJB
    private ViajeDao viajeEJB;
    @EJB
    private MonedaDao monedaEJB;

    private LoginBean loginBean;

    private PgeMonedas moneda;

    String nombreArchivo = null;
    String nombreCarpetaImg;

    private boolean habilitado;

    //crea una nueva instancia de Viaje
    public ViajeBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        this.viajeSelected = new ViaViajes();
        this.viajeSelected.setMonId(this.moneda);
        this.monedas = monedaEJB.getAll();
        viajes = viajeEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.viajeSelected = new ViaViajes();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        this.viajeSelected = new ViaViajes();
        habilitado = true;
    }
    
    public void addViaje(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (ViaViajes via:viajes){
            if (via.getViaDesc().equals(this.viajeSelected.getViaDesc()) && this.viajeSelected.getViaId() == null){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. " + this.viajeSelected.getViaDesc()+" ya existe. Verifique.", ""));
                //this.clean();
                return;
            }
        }
        
        ViaViajes viaje = new ViaViajes();
        if (this.viajeSelected.getViaId()!= null){ //modificacion
            if (this.nombreArchivo != null)
                viaje.setViaImg(this.nombreArchivo);
            else
                viaje.setViaImg(this.viajeSelected.getViaImg());
            viaje.setViaId(this.viajeSelected.getViaId());
            viaje.setViaUsuIns(this.viajeSelected.getViaUsuIns());
            viaje.setViaFecIns(this.viajeSelected.getViaFecIns());
            viaje.setViaUsuMod(loginBean.getUsername());
            viaje.setViaFecMod(new Date());
            viaje.setViaCantTot(this.viajeSelected.getViaCantTot());
            viaje.setViaCantVend(this.viajeSelected.getViaCantVend());
        }
        else //insercion
        {
            viaje.setViaImg(this.nombreArchivo);
            viaje.setViaUsuIns(loginBean.getUsername());
            viaje.setViaFecIns(new Date());
            viaje.setViaCantTot(0);
            viaje.setViaCantVend(0);
        }
        viaje.setViaDesc(this.viajeSelected.getViaDesc());
        viaje.setViaResumen(this.viajeSelected.getViaResumen());
        viaje.setViaFecSali(this.viajeSelected.getViaFecSali());
        viaje.setViaFecReg(this.viajeSelected.getViaFecReg());
        viaje.setMonId(this.viajeSelected.getMonId());
        viaje.setViaCost(this.viajeSelected.getViaCost());
        viajeEJB.update(viaje);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! " + viaje.getViaDesc()+" fue guardado con éxito.", ""));
        viajes = viajeEJB.getAll();
        RequestContext.getCurrentInstance().update("viaje-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgViajeAdd').hide();");
    }
    
    public void deleteViaje(){
        FacesContext context = FacesContext.getCurrentInstance();
        viajeEJB.delete(this.viajeSelected);
        context.addMessage(null, new FacesMessage("Felicidades! " + this.viajeSelected.getViaDesc() + " fue borrado con éxito.", ""));
        viajes = viajeEJB.getAll();
        RequestContext.getCurrentInstance().update("viaje-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgViajeAdd').hide();");
    }
    
    public void goReporte() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/py.travelware/repviaje?id="+viajeSelected.getViaId().toString());
        } catch (IOException e) {
            System.out.println("Error en servlet repViaje - "+e);
        }
        
    }
    
    public void onRowSelect(SelectEvent event){
        this.viajeSelected = (ViaViajes) event.getObject();
        RequestContext.getCurrentInstance().update("viaje-form:dtViaje");
    }

    public void handleFileUpload(FileUploadEvent event) {
         ExternalContext extContext = javax.faces.context.FacesContext.getCurrentInstance().getExternalContext();
         Boolean existeDir; 
         File targetFolder;
         File result;
            try {
                targetFolder = new File("/opt/py.travelware/viaImg/");
                result = new File("/opt/py.travelware/viaImg/"+event.getFile().getFileName());
                existeDir = targetFolder.canRead();
                if (!existeDir){
                    targetFolder.mkdirs();
                }
                //this.getClass().getResource("/").getPath(); 
                
                System.out.println("/opt/py.travelware/viaImg/" + event.getFile().getFileName()+" subido");
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

    public List<ViaViajes> getViajes() {
        return viajes;
    }

    public void setViajes(List<ViaViajes> viajes) {
        this.viajes = viajes;
    }

    public List<PgeMonedas> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<PgeMonedas> monedas) {
        this.monedas = monedas;
    }

    public ViaViajes getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(ViaViajes viajeSelected) {
        this.viajeSelected = viajeSelected;
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

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
<<<<<<< HEAD

    public boolean isDisponible(ViaViajes viaje){
        return viajeEJB.isDisponible(viaje);
    }
=======
>>>>>>> ed7c8ab95f4e17d7d3de0c1366b38e8d1895c766
}
