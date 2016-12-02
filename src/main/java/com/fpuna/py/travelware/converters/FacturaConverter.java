/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.FacturaDao;
import com.fpuna.py.travelware.model.ComFacturas;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author damia_000
 */
@FacesConverter(value = "facturaConverter")
public class FacturaConverter implements Converter{
    @EJB
    private FacturaDao facturaEJB;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            ComFacturas factura = facturaEJB.getByName(value);
            return factura;
        } catch (Exception e) {
            System.out.println("CLASS "+this.getClass().getName()+" METHOD: getAsObject "+ e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null){
            return null;
        }
        try {
            ComFacturas factura= (ComFacturas) value;
            return factura.getFacDesc();
        } catch (Exception e) {
            System.out.println("CLASS "+this.getClass().getName()+" METHOD: getAsString "+ e);
            return null;
        }
    }
   
}
