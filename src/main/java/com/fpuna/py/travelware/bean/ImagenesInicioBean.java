/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author eencina
 */
@Named(value = "imagenesInicioBean")
@SessionScoped
public class ImagenesInicioBean implements Serializable{
    private List<String> images;
    
    @PostConstruct
    public void init() {
        images = new ArrayList<String>();
        for (int i = 1; i <= 5; i++) {
            images.add("sanrafael" + i + ".jpg");
        }
    }

    public List<String> getImages() {
        return images;
    }
    
    
}
