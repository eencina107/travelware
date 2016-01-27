/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ModuloDao;
import com.fpuna.py.travelware.dao.PermisoDao;
import com.fpuna.py.travelware.dao.RolDao;
import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.model.PgeMenus;
import com.fpuna.py.travelware.model.PgeModulos;
import com.fpuna.py.travelware.model.PgeRoles;
import com.fpuna.py.travelware.model.PgeUsuarios;
import com.fpuna.py.travelware.util.Config;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author eencina
 */
@ManagedBean(name="loginBean")
@SessionScoped
public class LoginBean implements Serializable{
    private String username;
    private String password;
    private String newPassword;
    private boolean loggedIn;
    @EJB
    private UsuarioDao usuarioEjb;
    @EJB
    private RolDao rolDaoEJB;
    @EJB
    private PermisoDao permisoEJB;
    @EJB
    private ModuloDao moduloEJB;
    
    private  PgeUsuarios usuario;
    private List<PgeRoles> roles;
    private MenuModel model= new DefaultMenuModel();

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }
    private List<PgeMenus> menus;
    
     private static final Logger logger = Logger.getLogger(LoginBean.class);
    
    /**
     * Método para cerrar Sesión
     *
     * @return
     */
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        logger.info("Cerró la sessión");
        loggedIn = false;
        return "login";
    }
    
    public String login() throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
        usuario = usuarioEjb.autenticate(username, password);
        String contextPath=null;
        loggedIn = usuario != null;
        
        if (loggedIn) {
            logger.info("Se inició sesión como " + username);
            roles = rolDaoEJB.getRolesByUsuario(usuario);
            model=new DefaultMenuModel();
            cargarMenu();
            try {
                contextPath =FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
                FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath+"/secure/index.xhtml");
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "home";
            } else {
            loggedIn = false;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error de acceso", "El usuario y la contraseña no coinciden");
            FacesContext.getCurrentInstance().addMessage(null, message);
            logger.info("Se intentó ingresar como " + username);
            logger.error("Se intentó ingresar como " + username);
            username = null;
            password = null;
            RequestContext.getCurrentInstance().update("loginPage");
            RequestContext.getCurrentInstance().update("mensajes");
            return "login";
        }
    }
    
    /**
     * Método que redirecciona al home de la aplicación en caso de que el
     * usuario este correctamente autenticado, de lo contrario la regla de
     * acceso aplicada a travez de loginFilter hace que se redireccione al
     * login.xhtml
     *
     * @throws IOException
     */
    public void redirect() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/secure/index.xhtml");
        
    }
    
    public boolean getPermisoPantalla(List<String> roles) {
        //revisar 
        return true;
    }
    
    public void cargarMenu() throws IOException{
        ModuloDao modulos = null;
        menus = usuario.getPgeMenus();
        Collections.sort(menus);
        Iterator i = menus.iterator();
        String serverip=null;
        try{
            serverip= Config.getProperties("serverip");
        }
        catch(Exception e){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
                                                    "Error.", 
                                                    "No se encontró el Archivo de Configuración del Sistema."+
                                                            "Excepcion:"+e);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        
        PgeMenus menu;
        DefaultSubMenu submenu = null;
        DefaultMenuItem item = null;
        int mod=0;//bandera del numero de modulo
        
        if (menus!=null){
            while(i.hasNext()){
                menu=(PgeMenus) i.next();
                if ((null == submenu) || (mod!= menu.getMenId())){
                    if (submenu!=null){
                        model.addElement(submenu);
                    }
                    mod=menu.getMenId();
                    submenu=new DefaultSubMenu(moduloEJB.getById(menu.getMenId()).getModDesc());
                }
                item=new DefaultMenuItem(menu.getMenDescripcion(),"",serverip+menu.getMenUbicacion());
                submenu.addElement(item);
            }
            model.addElement(submenu);
        }
        

    }
    
    public void cambiarConstrasenha(){
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            PgeUsuarios userAutenticate = usuarioEjb.autenticate(username, password);
            if (userAutenticate == null){
                context.addMessage(null, new FacesMessage("Advertencia", "Usuario o Contraseña Incorrectos."));
            }
            else
            {
                userAutenticate = usuarioEjb.changePass(username, newPassword);
                if (userAutenticate == null) {
                    context.addMessage(null, new FacesMessage("Advertencia", "Ocurrió un error al cambiar la contraseña"));
                }
                else
                {
                    context.addMessage(null, new FacesMessage("Felicidades!", "Contraseña cambiada con éxito"));
                }
            }
            
        } catch (UnsupportedEncodingException ex) {
            System.out.println("ERROR. CLASS: "+this.getClass().getName()+" METHOD: cambiarContrasenha "+ex);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("ERROR. CLASS: "+this.getClass().getName()+" METHOD: cambiarContrasenha "+ex);
        }
    }
    
    //Getters && Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<PgeRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<PgeRoles> rol) {
        this.roles = rol;
    }

    public PgeUsuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(PgeUsuarios usuario) {
        this.usuario = usuario;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    
}
