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
@Named(value = "moduloBean")
@ViewScoped
public class ModuloBean implements Serializable{
    private int modId;
    private List<PgeModulos> modulos;
    private PgeModulos moduloSelected;
    private PgeModulos moduloNuevo;
    @EJB
    private ModuloDao moduloEJB;
    private LoginBean loginBean;

    private boolean habilitado;

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
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

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
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
        habilitado = true;
    }
    
    public void clean(){
        this.moduloSelected=null;
        this.moduloNuevo= new PgeModulos();        
    }
    
    public void buttonAction(ActionEvent actionEvent){
        moduloNuevo = new PgeModulos();
        habilitado = true;
    }

    public void addModulo(){
        FacesContext context= FacesContext.getCurrentInstance();
        for (PgeModulos mod: modulos){
            if(mod.getModDesc().equals(this.moduloNuevo.getModDesc())){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. El módulo "+this.moduloNuevo.getModDesc()+" ya existe.", ""));
                //this.clean();
                return;
            }
            if(mod.getModAbr().equals(this.moduloNuevo.getModAbr())){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. El módulo "+this.moduloNuevo.getModAbr()+" ya existe.", ""));
                //this.clean();
                return;
            }
        }
        this.moduloNuevo.setModEstado(Character.toUpperCase(this.moduloNuevo.getModEstado()));
        this.moduloNuevo.setModAbr(this.moduloNuevo.getModAbr().toUpperCase());        
        
        PgeModulos modulo= new PgeModulos();
        modulo.setModAbr(this.moduloNuevo.getModAbr());
        modulo.setModDesc(this.moduloNuevo.getModDesc());
        modulo.setModEstado(this.moduloNuevo.getModEstado());
        modulo.setModUsuIns(loginBean.getUsername());
        modulo.setModFecIns(new Date());
        moduloEJB.create(modulo);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El módulo " + modulo.getModDesc() + " fue creado con éxito.", ""));
        modulos = moduloEJB.getAll();
        RequestContext.getCurrentInstance().update("modulo-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgModuloAdd').hide();");
    }
    
    public void deleteModulo(){
        FacesContext context = FacesContext.getCurrentInstance();
        moduloEJB.delete(this.getModuloSelected());
        context.addMessage(null, new FacesMessage("Felicidades! El módulo " + this.getModuloSelected().getModDesc() + " fue borrado con éxito.", ""));
        modulos= moduloEJB.getAll();
        RequestContext.getCurrentInstance().update("modulo-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgModuloUpd').hide();");
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
        context.addMessage(null, new FacesMessage("Felicidades! El módulo " + this.moduloSelected.getModDesc()+" fue modificado con éxito.", ""));
        modulos= moduloEJB.getAll();
        RequestContext.getCurrentInstance().update("modulo-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgModuloUpd').hide();");
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
