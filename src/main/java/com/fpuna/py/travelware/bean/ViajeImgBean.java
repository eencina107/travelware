/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.ViaViajes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author olimp
 */
@ManagedBean
@RequestScoped
public class ViajeImgBean {
    private StreamedContent image;

    
    private String viaId;

    @EJB
    private ViajeDao viajeEJB;
    
    ViaViajes viaje;
    String nombreArch = null;

    @PostConstruct 
    public void init() {
        if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            image = new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            FacesContext context= javax.faces.context.FacesContext.getCurrentInstance();
            viaId = (context.getExternalContext().getRequestParameterMap().get("viaId")) ;
            viaje= viajeEJB.getById(Integer.parseInt(viaId));
            nombreArch = viaje.getViaImg();
            InputStream stream = null;
            if (nombreArch != null) { 
                try {
                    stream = new FileInputStream(new File("/opt/py.travelware/viaImg/"+nombreArch));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ViajeImgBean.class.getName()).log(Priority.ERROR, null, ex);
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