/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.ProfesionDao;
import com.fpuna.py.travelware.model.PgeProfesiones;
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
@FacesConverter(value = "profesionConverter")
public class ProfesionConverter implements Converter{
    @EJB
    private ProfesionDao profesionEJB;
    final static Logger logger = Logger.getLogger(ProfesionConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        
        try {
            PgeProfesiones profesion = profesionEJB.getByName(value);
            return profesion;
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
        PgeProfesiones profesion = (PgeProfesiones) value;
        try {
            return profesion.getPrfDesc();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
