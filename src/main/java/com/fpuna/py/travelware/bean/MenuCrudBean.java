/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.MenuDao;
import com.fpuna.py.travelware.dao.ModuloDao;
import com.fpuna.py.travelware.model.PgeMenus;
import com.fpuna.py.travelware.model.PgeModulos;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
@Named(value = "menuCrudBean")
@SessionScoped
public class MenuCrudBean implements Serializable{
    private int menuId;
    private int menSubId;
    private String menDesc;
    private Character menTipo;
    private String menUbi;
    private List<PgeMenus> menus;
    private List<PgeModulos> modulos;
    private PgeMenus menuSelected;
    private PgeMenus menuNuevo;
    @EJB
    private MenuDao menuEJB;
    @EJB
    private ModuloDao moduloEJB;
    private LoginBean loginBean;
    
    private boolean habilitado;

    //crea una nueva instancia de MenuCrudBean
    public MenuCrudBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        menuSelected = new PgeMenus();
        menuNuevo = new PgeMenus();
        menus= menuEJB.getAll();
        modulos= moduloEJB.getAll();
        habilitado = true;
    }

    private void clean() {
        this.menDesc = null;
        this.menUbi = null;
        this.menTipo = null;
        this.menuNuevo = new PgeMenus();
        this.menuSelected = new PgeMenus();
    }
    
    public void addMenu(){
        FacesContext context = FacesContext.getCurrentInstance();
        for (PgeMenus men:menus){
            if (men.getMenDescripcion().toUpperCase().equals(this.menuNuevo.getMenDescripcion())){
                context.addMessage(null, new FacesMessage("Advertencia. El menú "+this.menuNuevo.getMenDescripcion()+" ya existe"));
                this.clean();
                return;
            }
            if (men.getMenUbicacion().toUpperCase().equals(this.menuNuevo.getMenUbicacion())){
                context.addMessage(null, new FacesMessage("Advertencia. Ya hay un menu que utiliza la ubicación "+this.menuNuevo.getMenUbicacion()));
                this.clean();
                return;
            }
            if (men.getMenId() == this.menuNuevo.getMenId() && men.getMenSubId()== this.menuNuevo.getMenSubId()){
                context.addMessage(null, new FacesMessage("Advertencia. El menú "+this.menuNuevo.getMenId()+"-"+this.menuNuevo.getMenSubId()+" ya existe."));
                this.clean();
                return;
            }
        }
        
        PgeMenus menu = new PgeMenus();
        menu.setMenDescripcion(this.menuNuevo.getMenDescripcion());
        menu.setMenUbicacion(this.menuNuevo.getMenUbicacion());
        menu.setMenTipo(this.menuNuevo.getMenTipo());
        menu.setMenId(this.menuNuevo.getMenId());
        menu.setMenSubId(this.menuNuevo.getMenSubId());
        menu.setMenUsuIns(this.loginBean.getUsername());
        menu.setMenFecIns(new Date());
        menuEJB.create(menu);
        context.addMessage("Mensaje", new FacesMessage("Felicidades! "+ menu.getMenDescripcion()+" fue creado con éxito!"));
        menus= menuEJB.getAll();
        this.clean();
    }
    
    public void deleteMenu(){
        menuEJB.delete(this.menuSelected);
        menus= menuEJB.getAll();
        RequestContext.getCurrentInstance().update("menu-form:dtMenu");
    }
    
    public void editMenu(){
        if (this.menuSelected.getMenCod() == null){
            addMenu();
            return;
        }
        
        PgeMenus menu= menuEJB.getById(this.menuSelected.getMenCod());
        FacesContext context = FacesContext.getCurrentInstance();
        menu.setMenId(this.menuSelected.getMenId());
        menu.setMenSubId(this.menuSelected.getMenSubId());
        menu.setMenDescripcion(this.menuSelected.getMenDescripcion());
        menu.setMenTipo(this.menuSelected.getMenTipo());
        menu.setMenUbicacion(this.menuSelected.getMenUbicacion());
        menu.setMenUsuMod(loginBean.getUsername());
        menu.setMenFecMod(new Date());
        menuEJB.update(menu);
        context.addMessage(null, new FacesMessage("Felicidades! "+ this.menuSelected.getMenDescripcion()+" fue modificado con éxito!"));
        menus = menuEJB.getAll();
        this.clean();
    }
    
    public void onRowSelect(SelectEvent event){
        this.menuSelected = (PgeMenus) event.getObject();
        RequestContext.getCurrentInstance().update("menu-form:dtMenu");
    }
    
    public void onCancel(){
        
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getMenSubId() {
        return menSubId;
    }

    public void setMenSubId(int menSubId) {
        this.menSubId = menSubId;
    }

    public String getMenDesc() {
        return menDesc;
    }

    public void setMenDesc(String menDesc) {
        this.menDesc = menDesc;
    }

    public Character getMenTipo() {
        return menTipo;
    }

    public void setMenTipo(Character menTipo) {
        this.menTipo = menTipo;
    }

    public String getMenUbi() {
        return menUbi;
    }

    public void setMenUbi(String menUbi) {
        this.menUbi = menUbi;
    }

    public List<PgeMenus> getMenus() {
        return menus;
    }

    public void setMenus(List<PgeMenus> menus) {
        this.menus = menus;
    }

    public PgeMenus getMenuSelected() {
        return menuSelected;
    }

    public void setMenuSelected(PgeMenus menuSelected) {
        this.menuSelected = menuSelected;
    }

    public PgeMenus getMenuNuevo() {
        return menuNuevo;
    }

    public void setMenuNuevo(PgeMenus menuNuevo) {
        this.menuNuevo = menuNuevo;
    }

    public MenuDao getMenuEJB() {
        return menuEJB;
    }

    public void setMenuEJB(MenuDao menuEJB) {
        this.menuEJB = menuEJB;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   
}

