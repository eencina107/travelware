/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.ModuloDao;
import com.fpuna.py.travelware.model.PgeModulos;
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
@FacesConverter(value = "moduloConverter")
public class ModuloConverter implements Converter{
    @EJB
    private ModuloDao moduloEJB;
    
    final static Logger logger = Logger.getLogger(ModuloConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            PgeModulos modulo= moduloEJB.getByName(value);
            return modulo;
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
            PgeModulos modulo = (PgeModulos) value;
            return modulo.getModDesc();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
