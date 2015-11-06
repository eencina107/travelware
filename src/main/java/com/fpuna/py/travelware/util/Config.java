/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.util;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author eencina
 */
public class Config {
    public static String getProperties(String clave) throws IOException{
        Properties prop = new Properties();
        prop.load(Config.class.getResourceAsStream("/src/main/DatosGenerales.properties"));
        return prop.getProperty(clave);
    }
    
    public static String setProperties(String clave, String valor) throws IOException{
        Properties prop= new Properties();
        prop.load(Config.class.getResourceAsStream("/src/main/DatosGenerales.properties"));
        prop.setProperty(clave, valor);
        return prop.getProperty(clave);
    }
}

