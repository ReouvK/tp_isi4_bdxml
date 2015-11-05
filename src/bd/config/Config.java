/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Réouven KIDOUCHIM
 */

public class Config {

    private Properties pr;
    public final static String CONF_PTH = "src/xquery/ressources/config.properties";
    public final static String DATABASE = "dbname";
    public final static String DB_PATH = "path";
    public final static String FLX_RSS = "rss";
    
    public Config(String path) {
        try {
            pr = new Properties();
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            pr.load(inputStream);
        } catch (IOException ie) {
            System.out.println(ie.getMessage());
        }
    }
    public String getProp(String prop) {
        return pr.getProperty(prop);
    }
}
