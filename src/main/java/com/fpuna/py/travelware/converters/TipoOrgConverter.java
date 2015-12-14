/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.TipoOrgDao;
import com.fpuna.py.travelware.model.PgeTipoOrg;
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
@FacesConverter(value = "tipoOrgConverter")
public class TipoOrgConverter implements Converter{
    @EJB
    private TipoOrgDao tipoOrgEJB;
    final static Logger logger = Logger.getLogger(TipoOrgConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            PgeTipoOrg tipoOrg = tipoOrgEJB.getByName(value);
            return tipoOrg;
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
        PgeTipoOrg tipoOrg= (PgeTipoOrg) value;
        try {
            return tipoOrg.getTipDescripcion();
        } catch (Exception e) {
            logger.error("CLASS "+this.getClass().getName()+" METHOD: getAsString ", e);
            return null;
        }
    }
    
}
