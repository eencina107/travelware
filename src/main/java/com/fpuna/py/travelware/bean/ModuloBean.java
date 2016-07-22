/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ModuloDao;
import com.fpuna.py.travelware.model.PgeModulos;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javassist.bytecode.Bytecode;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "moduloBean")
@SessionScoped
public class ModuloBean implements Serializable{
    private int modId;
    private String modDesc;
    private String modAbr;
    private Character modEst;
    private List<PgeModulos> modulos;
    private PgeModulos moduloSelected;
    private PgeModulos moduloNuevo;
    @EJB
    private ModuloDao moduloEJB;
    private LoginBean loginBean;

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
    }

    public String getModDesc() {
        return modDesc;
    }

    public void setModDesc(String modDesc) {
        this.modDesc = modDesc;
    }

    public String getModAbr() {
        return modAbr;
    }

    public void setModAbr(String modAbr) {
        this.modAbr = modAbr;
    }

    public Character getModEst() {
        return modEst;
    }

    public void setModEst(Character modEst) {
        this.modEst = modEst;
    }

    public List<PgeModulos> getModulos() {
        return modulos;
    }

    public void setModulos(List<PgeModulos> modulos) {
        this.modulos = modulos;
    }

    public PgeModulos getModuloSelected() {
        return moduloSelected;
    }

    public void setModuloSelected(PgeModulos moduloSelected) {
        this.moduloSelected = moduloSelected;
    }

    public PgeModulos getModuloNuevo() {
        return moduloNuevo;
    }

    public void setModuloNuevo(PgeModulos moduloNuevo) {
        this.moduloNuevo = moduloNuevo;
    }
    
    //crea una nueva instancia de ModuloBean
    public ModuloBean(){
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context= javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session= (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        moduloSelected= new PgeModulos();
        modulos=moduloEJB.getAll();
    }
    
    public void clean(){
        this.modDesc= null;
        this.modAbr= null;
        this.modEst = null;
        this.moduloSelected=null;
        this.moduloNuevo= new PgeModulos();        
    }
    
    public void addModulo(){
        FacesContext context= FacesContext.getCurrentInstance();
        for (PgeModulos mod: modulos){
            if(mod.getModDesc().equals(this.moduloNuevo.getModDesc())){
                context.addMessage(null, new FacesMessage("Advertencia. El módulo "+this.moduloNuevo.getModDesc()+" ya existe"));
                this.clean();
                return;
            }
            if(mod.getModAbr().equals(this.moduloNuevo.getModAbr())){
                context.addMessage(null, new FacesMessage("Advertencia. El módulo "+this.moduloNuevo.getModAbr()+" ya existe"));
                this.clean();
                return;
            }
        }
        this.moduloNuevo.setModEstado(Character.toUpperCase(this.moduloNuevo.getModEstado()));
        this.moduloNuevo.setModAbr(this.moduloNuevo.getModAbr().toUpperCase());
        if ((!this.moduloNuevo.getModEstado().toString().equals("A")) && (!this.moduloNuevo.getModEstado().toString().equals("I"))){
            context.addMessage(null, new FacesMessage("Advertencia. Estado inválido, introduzca A para activo o I para inactivo"));
            this.clean();
            return;
        }
        
        
        PgeModulos modulo= new PgeModulos();
        modulo.setModAbr(this.moduloNuevo.getModAbr());
        modulo.setModDesc(this.moduloNuevo.getModDesc());
        modulo.setModEstado(this.moduloNuevo.getModEstado());
        modulo.setModUsuIns(loginBean.getUsername());
        modulo.setModFecIns(new Date());
        moduloEJB.create(modulo);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! "+ modulo.getModDesc() + " fue creado con éxito"));
        modulos = moduloEJB.getAll();
        this.clean();
    }
    
    public void deleteModulo(){
        moduloEJB.delete(this.getModuloSelected());
        modulos= moduloEJB.getAll();
    }
    
    public void editModulo(){
        if (null==this.moduloSelected.getModId()){
            addModulo();
            return;
        }
        
        PgeModulos modulo = moduloEJB.getById(this.moduloSelected.getModId());
        FacesContext context= FacesContext.getCurrentInstance();
        modulo.setModDesc(this.moduloSelected.getModDesc());
        modulo.setModAbr(this.moduloSelected.getModAbr());
        modulo.setModEstado(this.moduloSelected.getModEstado());
        modulo.setModFecMod(new Date());
        modulo.setModUsuMod(loginBean.getUsername());
        moduloEJB.update(modulo);
        context.addMessage(null, new FacesMessage("Felicidades! "+ this.moduloSelected.getModDesc()+" fue modificado con éxito"));
        modulos= moduloEJB.getAll();
        this.clean();
    }
    
    public PgeModulos prepareCreate(){
        this.moduloSelected = new PgeModulos();
        return this.moduloSelected;
    }
    
    public void onCancel(){
        
    }
    
    public void onRowSelect(SelectEvent event){
        this.moduloSelected= (PgeModulos) event.getObject();
        RequestContext.getCurrentInstance().update("modulo-form:dtModulo");
    }
    
}
