/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.AtractivoDao;
import com.fpuna.py.travelware.dao.CiudadDao;
import com.fpuna.py.travelware.model.PgeAtractivos;
import com.fpuna.py.travelware.model.PgeCiudades;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "atractivoBean")
@ViewScoped
public class AtractivoBean implements Serializable{
    private List<PgeAtractivos> atractivos;
    private List<PgeCiudades> ciudades;
    private PgeAtractivos atractivoSelected;
    @EJB
    private AtractivoDao atractivoEJB;
    @EJB
    private CiudadDao ciudadEJB;
    private LoginBean loginBean;
    private PgeCiudades ciudad;
    
    private boolean habilitado;

    //crea una nueva instancia de atractivo
    public AtractivoBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        atractivoSelected = new PgeAtractivos();
        ciudad = new PgeCiudades();
        atractivoSelected.setCiuId(ciudad);
        atractivos = atractivoEJB.getAll();
        ciudades = ciudadEJB.getAll();
        habilitado = true;
    }
    public void buttonAction(ActionEvent actionEvent) {
        atractivoSelected = new PgeAtractivos();
        habilitado = true;
    }
    
    
    private void clean() {
        this.atractivoSelected = new PgeAtractivos();
    }
    
    public void addAtractivo(){
        FacesContext context = FacesContext.getCurrentInstance();
            for (PgeAtractivos atr:atractivos){
                if (atr.getAtrDesc().toUpperCase().equals(this.atractivoSelected.getAtrDesc().toUpperCase()) && (this.atractivoSelected.getAtrId()!=null && !Objects.equals(this.atractivoSelected.getAtrId(), atr.getAtrId()))){
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. El atractivo "+this.atractivoSelected.getAtrDesc()+" ya existe.", ""));
                    //this.clean();
                    return;
                }
            }
        
        PgeAtractivos atractivo= new PgeAtractivos();
        if (this.atractivoSelected.getAtrId()!=null){
            atractivo.setAtrId(this.atractivoSelected.getAtrId());
        }
        atractivo.setAtrDesc(this.atractivoSelected.getAtrDesc());
        atractivo.setCiuId(this.atractivoSelected.getCiuId());
        atractivo.setAtrImg(this.atractivoSelected.getAtrImg());
        atractivo.setAtrUbi(this.atractivoSelected.getAtrUbi());
        atractivo.setAtrFecIns(new Date());
        atractivo.setAtrUsuIns(loginBean.getUsername());
        atractivoEJB.update(atractivo);
        if (this.atractivoSelected.getAtrId()!=null){
            context.addMessage("Mensaje", new FacesMessage("Felicidades! " + atractivo.getAtrDesc() + " fue modificado con éxito.", ""));
        }
        else{
            context.addMessage("Mensaje", new FacesMessage("Felicidades! " + atractivo.getAtrDesc() + " fue creado con éxito.", ""));
        }
        atractivos = atractivoEJB.getAll();
        RequestContext.getCurrentInstance().update("atractivo-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgAtractivoAdd').hide();");
    }
    
    public void deleteAtractivo(){
        FacesContext context = FacesContext.getCurrentInstance();
        atractivoEJB.delete(this.atractivoSelected);
        context.addMessage(null, new FacesMessage("Felicidades! " + this.atractivoSelected.getAtrDesc() + " fue borrado con éxito.", ""));
        atractivos = atractivoEJB.getAll();
        RequestContext.getCurrentInstance().update("atractivo-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgAtractivoAdd').hide();");
    }
    
    public void onRowSelect(SelectEvent event){
        this.atractivoSelected= (PgeAtractivos) event.getObject();
        RequestContext.getCurrentInstance().update("atractivo-form:dtAtractivo");
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        try{
            this.atractivoSelected.setAtrImg(event.getFile().getContents());
        }
        catch (Exception e){
            FacesMessage message = new FacesMessage("Error! " + event.getFile().getFileName() + e, "");
            return;
        }
        FacesMessage message = new FacesMessage("Felicidades " + event.getFile().getFileName() + " fue subido con éxito.", "");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<PgeAtractivos> getAtractivos() {
        return atractivos;
    }

    public void setAtractivos(List<PgeAtractivos> atractivos) {
        this.atractivos = atractivos;
    }

    public List<PgeCiudades> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<PgeCiudades> ciudades) {
        this.ciudades = ciudades;
    }

    public PgeAtractivos getAtractivoSelected() {
        return atractivoSelected;
    }

    public void setAtractivoSelected(PgeAtractivos atractivoSelected) {
        this.atractivoSelected = atractivoSelected;
    }

    public AtractivoDao getAtractivoEJB() {
        return atractivoEJB;
    }

    public void setAtractivoEJB(AtractivoDao atractivoEJB) {
        this.atractivoEJB = atractivoEJB;
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

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}
