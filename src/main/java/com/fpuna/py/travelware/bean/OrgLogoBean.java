/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.OrganizacionDao;
import com.fpuna.py.travelware.model.PgeOrganizaciones;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
public class OrgLogoBean implements Serializable{
    private StreamedContent image;

    
    private String orgId;

    @EJB
    private OrganizacionDao orgEJB;
    
    PgeOrganizaciones org;
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
            orgId = (context.getExternalContext().getRequestParameterMap().get("orgId")) ;
            org= orgEJB.getById(Integer.parseInt(orgId));
            nombreArch = org.getOrgLogo();
             InputStream stream = null;
            try {
                stream = new FileInputStream(new File("/opt/py.travelware/orgLogo/"+org.getOrgLogo()));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OrgLogoBean.class.getName()).log(Level.SEVERE, null, ex);
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Error. No se encuentra el archivo"));
            }
            image = new DefaultStreamedContent(stream);
        }
    }

    
    public StreamedContent getImage() {
        return image;
    }


}
