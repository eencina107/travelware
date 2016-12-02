/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.model.PgePersonas;
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
@FacesConverter(value = "personaConverter")
public class PersonaConverter implements Converter{
    @EJB
    private PersonaDao personaEJB;
    
    final static Logger logger= Logger.getLogger(PersonaConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        
        try {
            PgePersonas persona = personaEJB.getByNroDoc(Integer.parseInt(value));
            return persona;
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
            PgePersonas persona = (PgePersonas) value;
            return persona.getPerNroDoc();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
