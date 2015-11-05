/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import bd.config.Config;
import bd.exceptions.SystemException;
import bd.model.Model;

/**
 *
 * @author Réouven KIDOUCHIM
 */

public class Controller implements Runnable {

    private Path path;
    private Model model;
    private Config conf;

    public Controller(String pth, Model m, Config conf) {
        this.path = Paths.get(pth);
        this.model = m;
        this.conf = conf;
        System.out.println("Fichier :" + pth);
    }

    @Override
    public void run() {
        WatchService ws;
        try {
            ws = this.path.getFileSystem().newWatchService();
            this.path.register(ws, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            WatchKey watchKey;

            while (!Thread.interrupted()) {
                watchKey = ws.take();

                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    String fileName = event.context().toString();
                    if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                        System.out.println("new file create " + fileName);
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
                        if (fileName.equals("rss.xml")) {
                            this.exec();
                        }
                        System.out.println(fileName + " has been modified");
                    } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
                        System.out.println(fileName + " has been deleted");
                    } else if (StandardWatchEventKinds.OVERFLOW.equals(event.kind())) {
                        System.out.println("Strange event");
                        continue;
                    }
                }
                watchKey.reset();
            }
        } catch (InterruptedException e) {

        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exec() {
        String rss = conf.getProp(Config.FLX_RSS);
        String query = "for $title in /series/nom/text()\n"
                + "for $title2 in distinct-values(//title/text())\n"
                + "where contains($title2,$title)\n"
                + "return concat($title,';')";

        String pth = path.toString();
        try {

            String db = model.createDatabase();

            model.openDb(db);

            model.addXMLToDb(pth + "/" + rss);
            model.addXMLToDb(pth + "/series.xml");
            System.out.println(model.getDatabases());
            String titre = model.executeQuery(query);
            String[] tabTitre = titre.split(";");
            ArrayList<String> mapTitre = this.clean(tabTitre);
            
            /*
            On demande de parcourir le tableau pour créer des fichiers
            avec leur nom et leur contenu
             */
            
            for (String unTitre : mapTitre) {

                //suppression des espaces
                unTitre = unTitre.trim();
                
                //System.out.println(unTitre);
                //recuperation du contenu omdb
                String contenuOmbd = model.omdb(unTitre);
                
                //System.out.println(contenuOmbd);
                String req_desc = "for $mdb in  /root/movie\n"
                        + "let $desc := distinct-values($mdb/@plot)\n"
                        + "let $title := $mdb/@title\n"
                        + "where contains($title,'" + unTitre + "')\n"
                        + "return\n"
                        + "<description>{(string($desc))}</description>";

                String req_avis = "for $mdb in  /root/movie\n"
                        + "let $desc := distinct-values($mdb/@plot)\n"
                        + "let $title := $mdb/@title\n"
                        + "let $avis  := $mdb/@imdbRating\n"
                        + "where contains($title,'" + unTitre + "')\n"
                        + "return\n"
                        + "<avis>{(string($avis))}</avis>";

                model.createXml(contenuOmbd, pth);
                model.addXMLToDb(pth + "/omdb.xml");

                //requête renvoyant le nom de l'épisode et le lien 
                String q2 = "for $item in //item\n"
                        + "where contains($item/title/text(),'" + unTitre + "')\n"
                        + "return\n"
                        + "<episode>\n"
                        + "   <nom>{$item/title/text()}</nom>\n"
                        + "   <lien>{$item/link/text()}</lien>\n"
                        + "</episode>";

                String res = model.executeQuery(q2);
                String res_desc = model.executeQuery(req_desc);
                String res_avis = model.executeQuery(req_avis);
                String res_omdb =res_desc+"\n"+res_avis;
                model.createXml(unTitre, res,res_omdb, pth);

                model.removeXML(pth + "/omdb.xml");

            }

        } catch (SystemException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private ArrayList<String> clean(String[] tab) {
        ArrayList<String> map = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < tab.length; i++) {
            if (!map.contains(tab[i].trim())) {
                map.add(tab[i]);
            }
        }
        return map;
    }

}
