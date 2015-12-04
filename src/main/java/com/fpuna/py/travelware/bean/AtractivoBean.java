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
@SessionScoped
public class AtractivoBean implements Serializable{
    private int atrId;
    private int ciuId;
    private String atrDesc;
    private byte[] atrImg;
    private String atrUbi;
    private List<PgeAtractivos> atractivos;
    private List<PgeCiudades> ciudades;
    private PgeAtractivos atractivoSelected;
    @EJB
    private AtractivoDao atractivoEJB;
    @EJB
    private CiudadDao ciudadEJB;
    private LoginBean loginBean;
    private PgeCiudades ciudad;
    
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
    }
    public void buttonAction(ActionEvent actionEvent) {
        atractivoSelected = new PgeAtractivos();
        
    }
    
    
    private void clean() {
        this.atrDesc = null;
        this.atrImg = null;
        this.atrUbi = null;
        this.atractivoSelected = new PgeAtractivos();

    }
    
    public void addAtractivo(){
        FacesContext context = FacesContext.getCurrentInstance();
            for (PgeAtractivos atr:atractivos){
                if (atr.getAtrDesc().toUpperCase().equals(this.atractivoSelected.getAtrDesc().toUpperCase()) && (this.atractivoSelected.getAtrId()!=null && !Objects.equals(this.atractivoSelected.getAtrId(), atr.getAtrId()))){
                    context.addMessage(null, new FacesMessage("Advertencia", "El atractivo "+this.atractivoSelected.getAtrDesc()+" ya existe"));
                    this.clean();
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
            context.addMessage("Mensaje", new FacesMessage("Felicidades!", atractivo.getAtrDesc()+" fue actualizado con éxito!"));
        }
        else{
            context.addMessage("Mensaje", new FacesMessage("Felicidades!", atractivo.getAtrDesc()+" fue creado con éxito!"));
        }
        atractivos = atractivoEJB.getAll();
        this.clean();
    }
    
    public void deleteAtractivo(){
        atractivoEJB.delete(this.atractivoSelected);
        atractivos = atractivoEJB.getAll();
        RequestContext.getCurrentInstance().update("atractivo-form:dtAtractivo");
    }
    
//    public void editCiudad(){
//        if(this.atractivoSelected.getAtrId() == null){
//            addAtractivo();
//            return;
//        }
//        
//        PgeAtractivos atractivo = atractivoEJB.getById(this.atractivoSelected.getAtrId());
//        FacesContext context =  FacesContext.getCurrentInstance();
//        atractivo.setAtrDesc(this.atractivoSelected.getAtrDesc());
//        atractivo.setAtrImg(this.atractivoSelected.getAtrImg());
//        atractivo.setAtrUbi(this.atractivoSelected.getAtrUbi());
//        atractivo.setAtrUsuMod(loginBean.getUsername());
//        atractivo.setAtrFecMod(new Date());
//        atractivoEJB.update(atractivo);
//        context.addMessage(null, new FacesMessage("Felicidades!", this.atractivoSelected.getAtrDesc()+" fue actualizado con éxito!"));
//        atractivos = atractivoEJB.getAll();
//        this.clean();
//    }
    
    public void onRowSelect(SelectEvent event){
        this.atractivoSelected= (PgeAtractivos) event.getObject();
        RequestContext.getCurrentInstance().update("atractivo-form:dtAtractivo");
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        try{
            this.atractivoSelected.setAtrImg(event.getFile().getContents());
        }
        catch (Exception e){
            FacesMessage message = new FacesMessage("Error!", event.getFile().getFileName() + e);
            return;
        }
        FacesMessage message = new FacesMessage("Felicidades!", event.getFile().getFileName() + " fue subido con éxito.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public int getAtrId() {
        return atrId;
    }

    public void setAtrId(int atrId) {
        this.atrId = atrId;
    }

    public int getCiuId() {
        return ciuId;
    }

    public void setCiuId(int ciuId) {
        this.ciuId = ciuId;
    }

    public String getAtrDesc() {
        return atrDesc;
    }

    public void setAtrDesc(String atrDesc) {
        this.atrDesc = atrDesc;
    }

    public byte[] getAtrImg() {
        return atrImg;
    }

    public void setAtrImg(byte[] atrImg) {
        this.atrImg = atrImg;
    }

    public String getAtrUbi() {
        return atrUbi;
    }

    public void setAtrUbi(String atrUbi) {
        this.atrUbi = atrUbi;
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
    
    
}
