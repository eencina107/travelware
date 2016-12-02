/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.converters;

import com.fpuna.py.travelware.dao.ConceptoDao;
import com.fpuna.py.travelware.model.ViaConceptos;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author damia_000
 */
@FacesConverter(value = "conceptoConverter")
public class ConceptoConverter implements Converter{
    @EJB
    private ConceptoDao conceptoEJB;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.equals("")){
            return null;
        }
        try {
            ViaConceptos concepto = conceptoEJB.getByName(value);
            return concepto;
        } catch (Exception e) {
            System.out.println("CLASS "+this.getClass().getName()+" METHOD: getAsObject "+ e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value == " " || value == ""){
            return null;
        }

        ViaConceptos concepto = (ViaConceptos) value;
        try {
            return concepto.getConDesc();
        } catch (Exception e) {
            System.out.println("CLASS "+this.getClass().getName()+" METHOD: getAsString "+ e);
            return null;
        }
    }
    
}
