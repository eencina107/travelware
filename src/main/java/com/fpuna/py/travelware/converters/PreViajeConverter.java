/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.PrecioViajeDao;
import com.fpuna.py.travelware.model.ViaPreViajes;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author eencina
 */
@FacesConverter(value = "precViajeConverter")
public class PreViajeConverter implements Converter{
    @EJB
    private PrecioViajeDao precioViajeEJB;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            ViaPreViajes precViaje = precioViajeEJB.getByName(value);
            return precViaje;
        } catch (Exception e) {
            System.out.println("ERROR: "+"CLASS "+this.getClass().getName()+" METHOD: getAsObject "+ e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        ViaPreViajes precViaje= (ViaPreViajes) value;
        try {
            return precViaje.getPreDescripcion();
        } catch (Exception e) {
            System.out.println("ERROR: "+"CLASS "+this.getClass().getName()+" METHOD: getAsString "+ e);
            return null;
        }
    }
    
}
