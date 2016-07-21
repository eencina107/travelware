/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.model.PgePersonas;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author eencina
 */
@ManagedBean
@RequestScoped
public class PerDocBean implements Serializable{
    private StreamedContent image;
    
    private String perId;
    
    @EJB
    private PersonaDao perEJB;
    
    PgePersonas per;
    String nombreArch;
    
    @PostConstruct
    public void init() {
        if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            image = new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            FacesContext context= javax.faces.context.FacesContext.getCurrentInstance();
            perId = (context.getExternalContext().getRequestParameterMap().get("perId")) ;
            per= perEJB.getById(Integer.parseInt(perId));
            nombreArch = per.getPerDoc();
            if (nombreArch==null || nombreArch.equals("")){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mensaje. No se encontro archivo asociado"));
                System.out.println("No se encontro nombre de archivo para la persona con ci:"+per.getPerNroDoc());
            }
            else
            {
               InputStream stream = null;
               try {
                   stream = new FileInputStream(new File("/opt/py.travelware/perDoc/"+per.getPerDoc()));
               } catch (FileNotFoundException ex) {
                   Logger.getLogger(OrgLogoBean.class.getName()).log(Level.SEVERE, null, ex);
                   FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Error. No se encuentra el archivo"));
               }
               image = new DefaultStreamedContent(stream);
            }
        }
    }

    
    public StreamedContent getImage() {
        return image;
    }
    
}
