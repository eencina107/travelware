/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.FacturaDetDao;
import com.fpuna.py.travelware.model.ComFacturasDet;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author damia_000
 */
@FacesConverter(value = "facturaDetConverter")
public class FacturaDetConverter implements Converter{
    @EJB
    private FacturaDetDao facturaDetEJB;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            ComFacturasDet facturaDet = facturaDetEJB.getByName(value);
            return facturaDet;
        } catch (Exception e) {
            System.out.println("CLASS "+this.getClass().getName()+" METHOD: getAsObject "+ e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value == ""){
            return null;
        }
        try {
            ComFacturasDet facturaDet= (ComFacturasDet) value;
            return facturaDet.getFadDesc();
        } catch (Exception e) {
            System.out.println("CLASS "+this.getClass().getName()+" METHOD: getAsString "+ e);
            return null;
        }
    }
   
}
