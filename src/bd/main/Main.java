/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.main;

import java.util.logging.Level;
import java.util.logging.Logger;
import bd.config.Config;
import bd.controller.Controller;
import bd.exceptions.SystemException;
import bd.model.Model;

/**
 *
 * @author Réouven KIDOUCHIM
 */

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SystemException  {
       
       
        
        Config conf = new Config(Config.CONF_PTH);
        Model m = new Model(conf.getProp(Config.DATABASE), conf.getProp(Config.DB_PATH));
        String path = conf.getProp(Config.DB_PATH);
       
        //m.executeQuery(req);
        try {
   
            Controller ctr = new Controller(path,m,conf);
            ctr.exec();
            Thread th = new Thread(ctr);
            th.start();
            
         
            Thread.sleep(10000);
            Thread.interrupted();}
            
        catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }

}
