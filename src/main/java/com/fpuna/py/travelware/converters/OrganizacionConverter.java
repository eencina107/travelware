/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.OrganizacionDao;
import com.fpuna.py.travelware.model.PgeOrganizaciones;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author damia_000
 */
@FacesConverter(value = "organizacionConverter")
public class OrganizacionConverter implements Converter{
    @EJB
    private OrganizacionDao organizacionEJB;
    
    final static Logger logger= Logger.getLogger(OrganizacionConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        
        try {
            PgeOrganizaciones organizacion = organizacionEJB.getByName(value);
            return organizacion;
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsObject ", e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            PgeOrganizaciones organizacion = (PgeOrganizaciones) value;
            return organizacion.getOrgDesc();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
