/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.CiudadDao;
import com.fpuna.py.travelware.model.PgeCiudades;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author eencina
 */
@FacesConverter(value = "ciudadConverter")
public class CiudadConverter implements Converter{
    @EJB
    private CiudadDao ciudadEJB;
    final static Logger logger = Logger.getLogger(CiudadConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value== null || value.equals("")){
        return null;
        }
        try {
            PgeCiudades ciudad = ciudadEJB.getByName(value);
            return ciudad;
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsObject ", e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null){
            return null;
        }
        PgeCiudades ciudad= (PgeCiudades) value;
        try {
            return ciudad.getCiuDesc();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
