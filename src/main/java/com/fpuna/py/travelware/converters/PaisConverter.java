/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.PaisDao;
import com.fpuna.py.travelware.model.PgePaises;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.jboss.logging.Logger;

/**
 *
 * @author eencina
 */
@FacesConverter(value = "paisConverter")
public class PaisConverter implements Converter{
    @EJB
    private PaisDao paisEJB;
    final static Logger logger = Logger.getLogger(PaisConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        
        try {
            PgePaises pais = paisEJB.getByName(value);
            return pais;
        }
        catch (Exception e){
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsObject ", e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if( value == null){
            return null;
        }
        PgePaises pais = (PgePaises) value;
        try {
            return pais.getPaiDesc();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
