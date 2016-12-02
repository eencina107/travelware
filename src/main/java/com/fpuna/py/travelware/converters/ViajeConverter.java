/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.model.ViaViajes;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author eencina
 */
@FacesConverter(value = "viajeConverter")
public class ViajeConverter implements Converter{
    @EJB
    private ViajeDao viajeEJB;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            ViaViajes viaje = viajeEJB.getByName(value);
            return viaje;
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
            ViaViajes viaje= (ViaViajes) value;
            return viaje.getViaDesc();
        } catch (Exception e) {
            System.out.println("CLASS "+this.getClass().getName()+" METHOD: getAsString "+ e);
            return null;
        }
    }
   
}
