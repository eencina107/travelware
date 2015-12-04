/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.OrganizacionDao;
import com.fpuna.py.travelware.model.PgeOrganizaciones;
import java.io.ByteArrayInputStream;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
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
@ApplicationScoped
public class OrgLogoBean {
    @EJB
    private OrganizacionDao organizacionEJB;
    
    public StreamedContent getImage(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE){
            return new DefaultStreamedContent();
        }
        else
        {
            Integer orgId= Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("orgId"));
            PgeOrganizaciones org= organizacionEJB.getById(orgId);
            return new DefaultStreamedContent( new ByteArrayInputStream(org.getOrgLogo()));
        }
    }
}
