/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ModuloDao;
import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.model.PgeMenus;
import com.fpuna.py.travelware.model.PgeModulos;
import com.fpuna.py.travelware.model.PgePermisos;
import com.fpuna.py.travelware.model.PgeUsuarios;
import com.fpuna.py.travelware.util.Config;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author eencina
 */
@ManagedBean
@SessionScoped
public class MenuBean {
    private MenuModel model= new DefaultMenuModel();
    List<PgeMenus> menus;
    PgeUsuarios usuario;
    
    public MenuBean(){
        
    }
    
    public MenuBean(PgeUsuarios user) throws IOException{
        
        ModuloDao modulos = null;
        PgeModulos modulo= null;
        menus = user.getPgeMenus();
        int n=1; //numero de modulo, al cambiar se crea un nuevo submenu
        Collections.sort(menus);
        Iterator i = menus.iterator();
        String serverip= Config.getProperties("serverip");
        PgeMenus menu;
        DefaultSubMenu submenu = null;
        DefaultMenuItem item;
        
        while (i.hasNext()){
            menu= (PgeMenus) i.next();
            if (menu.getPgeMenusPK().getMenId()!= n){
                model.addElement(submenu);
                n=menu.getPgeMenusPK().getMenId();
                modulo= modulos.getById(menu.getPgeMenusPK().getMenId());
                submenu=new DefaultSubMenu(modulo.getModDesc());
                submenu.setId(String.valueOf(modulo.getModId()));
            }
            item=new DefaultMenuItem(menu.getMenDescripcion());
            item.setId(String.valueOf(menu.getPgeMenusPK().getMenId())+"-"+String.valueOf(menu.getPgeMenusPK().getMenSubId()));
            item.setUrl(serverip+menu.getMenUbicacion());
            submenu.addElement(item);   
        }
        model.addElement(submenu);
    }
    
    public MenuModel getMenuModel(){
        return model;
    }
    
    public void setMenuModel(MenuModel mmd){
        this.model= mmd;
    }
}
