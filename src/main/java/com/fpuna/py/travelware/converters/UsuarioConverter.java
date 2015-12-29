/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.UsuarioDao;
import com.fpuna.py.travelware.model.PgeUsuarios;
import org.apache.log4j.Logger;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author eencina
 */
@FacesConverter(value = "usuarioConverter")
public class UsuarioConverter implements Converter{
    @EJB
    private UsuarioDao usuarioEJB;
    
    final static Logger logger = Logger.getLogger(UsuarioConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            PgeUsuarios usu= usuarioEJB.getByName(value);
            return usu;
        }
        catch(Exception e){
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsObject ", e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        PgeUsuarios usu = (PgeUsuarios) value;
        try {
            return usu.getUsuCod();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
