/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PermisoDao;
import com.fpuna.py.travelware.dao.RolDao;
import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.model.PgeRoles;
import com.fpuna.py.travelware.model.PgeUsuarios;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author eencina
 */
public class LoginBean implements Serializable{
    private String username;
    private String password;
    private boolean loggedIn;
    @EJB
    private UsuarioDao usuarioEjb;
    @EJB
    private RolDao rolDaoEJB;
    @EJB
    private PermisoDao permisoEJB;
    
    private  PgeUsuarios usuario;
    private List<PgeRoles> roles;
    
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
        return "index";
    }
    
    public String login() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        usuario = usuarioEjb.autenticate(username, password);
        
        loggedIn = usuario != null;
        
        if (loggedIn) {
            logger.info("Se inició sesión como " + username);
            roles = rolDaoEJB.getRolesByUsuario(usuario);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("secure/home.xhtml");
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
     * login.jsf
     *
     * @throws IOException
     */
    public void redirect() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("secure/index.html");
    }
}
