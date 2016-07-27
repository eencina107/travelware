/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.CiudadDao;
import com.fpuna.py.travelware.dao.OrganizacionDao;
import com.fpuna.py.travelware.dao.TipoOrgDao;
import com.fpuna.py.travelware.model.PgeCiudades;
import com.fpuna.py.travelware.model.PgeOrganizaciones;
import com.fpuna.py.travelware.model.PgeTipoOrg;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import static org.hibernate.internal.util.io.StreamCopier.BUFFER_SIZE;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "organizacionBean")
@SessionScoped
public class OrganizacionBean implements Serializable{
    private List<PgeOrganizaciones> organizaciones;
    private List<PgeCiudades> ciudades;
    private List<PgeTipoOrg> tiposOrg;
    private PgeOrganizaciones organizacionSelected;
    @EJB
    private OrganizacionDao organizacionEJB;
    @EJB
    private CiudadDao ciudadEJB;
    @EJB
    private TipoOrgDao tipoOrgEJB;
    private LoginBean loginBean;
    private PgeCiudades ciudad;
    private PgeTipoOrg tipoOrg;
    String nombreArchivo = "";
    String nombreCarpetaImg;
    
    //Crea una nueva instancia
    public OrganizacionBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context= javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session= (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        organizacionSelected = new PgeOrganizaciones();
        ciudad = new PgeCiudades();
        tipoOrg = new PgeTipoOrg();
        organizacionSelected.setCiuId(ciudad);
        organizaciones = organizacionEJB.getAll();
        ciudades = ciudadEJB.getAll();
        tiposOrg = tipoOrgEJB.getAll();
        
    }

    private void clean() {
        organizacionSelected = new PgeOrganizaciones();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        organizacionSelected = new PgeOrganizaciones();
    }
    
    public void addOrganizacion(){
        FacesContext context = FacesContext.getCurrentInstance();
        String mensaje;
        for (PgeOrganizaciones org:organizaciones){
            if (org.getOrgDesc().toUpperCase().equals(this.organizacionSelected.getOrgDesc().toUpperCase()) && !Objects.equals(org.getOrgId(), this.organizacionSelected.getOrgId())){
                context.addMessage(null, new FacesMessage("Advertencia. La organización "+this.organizacionSelected.getOrgDesc()+" ya existe"));
                this.clean();
                return;
            }
        }
        
        PgeOrganizaciones organizacion = new PgeOrganizaciones();
        organizacion.setOrgDesc(this.organizacionSelected.getOrgDesc());
        System.out.print("Archivo Logo: " + this.nombreArchivo);
        if (this.organizacionSelected.getOrgId()!=null){ //modificacion
            if (this.nombreArchivo != "")
                organizacion.setOrgLogo(this.nombreArchivo);
            else
                organizacion.setOrgLogo(this.organizacionSelected.getOrgLogo());
            organizacion.setOrgFecMod(new Date());
            organizacion.setOrgUsuMod(loginBean.getUsername());
            organizacion.setOrgUsuIns(this.organizacionSelected.getOrgUsuIns());
            organizacion.setOrgFecIns(this.organizacionSelected.getOrgFecIns());
            organizacion.setOrgId(this.organizacionSelected.getOrgId());
            mensaje = organizacion.getOrgDesc()+" fue modificado con éxito!";
        }
        else{ //insercion
            organizacion.setOrgLogo(this.nombreArchivo);
            organizacion.setOrgFecIns(new Date());
            organizacion.setOrgUsuIns(loginBean.getUsername());
            mensaje = organizacion.getOrgDesc()+" fue creado con éxito!";
        }
        organizacion.setCiuId(this.organizacionSelected.getCiuId());
        organizacion.setOrgDir(this.organizacionSelected.getOrgDir());
        organizacion.setOrgPagWeb(this.organizacionSelected.getOrgPagWeb());
        organizacion.setOrgSubTipo("NN");
        organizacion.setOrgTel(this.organizacionSelected.getOrgTel());
        organizacion.setTipOrgId(this.organizacionSelected.getTipOrgId());
        organizacion.setOrgUbi(this.organizacionSelected.getOrgUbi());
        organizacionEJB.update(organizacion);
        context.addMessage("Mensaje", new FacesMessage("Felicidades!"+mensaje));
        organizaciones = organizacionEJB.getAll();
        RequestContext.getCurrentInstance().update("organizacion-form:dtOrganizacion");
        this.clean();        
    }
    
    public void deleteOrganizacion(){
        organizacionEJB.delete(this.organizacionSelected);
        organizaciones = organizacionEJB.getAll();
        RequestContext.getCurrentInstance().update("organizacion-form:dtOrganizacion");
    }
    
    public void onRowSelect(SelectEvent event){
        this.organizacionSelected = (PgeOrganizaciones) event.getObject();
        RequestContext.getCurrentInstance().update("organizacion-form:dtOrganizacion");
    }
    
    public void handleFileUpload(FileUploadEvent event) {
         ExternalContext extContext = javax.faces.context.FacesContext.getCurrentInstance().getExternalContext();
         Boolean existeDir; 
         File targetFolder;
         File result;
            try {
                targetFolder = new File("/opt/py.travelware/orgLogo/");
                result = new File("/opt/py.travelware/orgLogo/"+event.getFile().getFileName());
                existeDir = targetFolder.canRead();
                if (!existeDir){
                    targetFolder.mkdirs();
                }
                //this.getClass().getResource("/").getPath(); 
                
                System.out.println("/opt/py.travelware/orgLogo/" + event.getFile().getFileName()+" subido");
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
                                    " Subido correctamente.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                this.nombreArchivo = event.getFile().getFileName();

            } catch (IOException e) {
                e.printStackTrace();

                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                               "Error al subir", "");
                FacesContext.getCurrentInstance().addMessage(null, error);
            }     
    }

    public List<PgeOrganizaciones> getOrganizaciones() {
        return organizaciones;
    }

    public void setOrganizaciones(List<PgeOrganizaciones> organizaciones) {
        this.organizaciones = organizaciones;
    }

    public List<PgeCiudades> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<PgeCiudades> ciudades) {
        this.ciudades = ciudades;
    }

    public PgeOrganizaciones getOrganizacionSelected() {
        return organizacionSelected;
    }

    public void setOrganizacionSelected(PgeOrganizaciones organizacionSelected) {
        this.organizacionSelected = organizacionSelected;
         RequestContext.getCurrentInstance().update("organizacion-form:dtOrganizacion");
    }

    public OrganizacionDao getOrganizacionEJB() {
        return organizacionEJB;
    }

    public void setOrganizacionEJB(OrganizacionDao organizacionEJB) {
        this.organizacionEJB = organizacionEJB;
    }

    public CiudadDao getCiudadEJB() {
        return ciudadEJB;
    }

    public void setCiudadEJB(CiudadDao ciudadEJB) {
        this.ciudadEJB = ciudadEJB;
    }

    public PgeCiudades getCiudad() {
        return ciudad;
    }

    public void setCiudad(PgeCiudades ciudad) {
        this.ciudad = ciudad;
    }

    public String getNombreCarpetaImg(String nombreArchivo) {
        return nombreCarpetaImg+nombreArchivo;
    }

    public List<PgeTipoOrg> getTiposOrg() {
        return tiposOrg;
    }

    public void setTiposOrg(List<PgeTipoOrg> tiposOrg) {
        this.tiposOrg = tiposOrg;
    }

    public PgeTipoOrg getTipoOrg() {
        return tipoOrg;
    }

    public void setTipoOrg(PgeTipoOrg tipoOrg) {
        this.tipoOrg = tipoOrg;
    }
    
    
    
}
