/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.model.PgePermisos;
import com.fpuna.py.travelware.model.PgeUsuarios;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author eencina
 */
@ManagedBean
@SessionScoped
public class MenuBean {
    MenuModel model;
    List<PgePermisos> permisos;
    UsuarioDao usuario;
    
    public MenuBean(PgeUsuarios usuario){
        model= new DefaultMenuModel();
        permisos = usuario.getPgePermisos();
        Iterator i = permisos.iterator();
        while (i.hasNext()){
            
        }
    }
}
