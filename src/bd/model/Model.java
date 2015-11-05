/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.*;
import bd.exceptions.SystemException;

/**
 *
 * @author Réouven KIDOUCHIM
 */

public class Model implements IModel {

    private Context ctx;
    private String dbName;
    private String path;

    public Model(String db, String pth) {
        this.ctx = new Context();
        this.dbName = db;
        this.path = pth;
    }

    @Override
    public void deleteDb(String db) throws SystemException {

        try {
            if (db.isEmpty()) {
                db = dbName;
            }
            new DropDB(db).execute(ctx);
        } catch (BaseXException be) {
            System.out.println(be.getMessage());
        }
    }

    @Override
    public String executeQuery(String query) throws SystemException {

        try {
            return new XQuery(query).execute(ctx);
        } catch (BaseXException ex) {
            throw new SystemException("Query Exception");
        }
    }

    @Override
    public String getDatabases() throws SystemException {
        try {
            return new List().execute(ctx);
        } catch (BaseXException ex) {
            throw new SystemException("Error on getting List");
        }
    }

    @Override
    public String getElementsInCollection(String collectionName) throws SystemException {
        try {
            return new XQuery(
                    "for $doc in collection('" + collectionName + "')"
                    + "return <doc path='{ base-uri($doc) }'/>"
            ).execute(ctx);
        } catch (BaseXException ex) {
            throw new SystemException("Query Exception");
        }
    }

    @Override
    public void openDb(String dbName) throws SystemException {
        try {
            new Open(dbName).execute(ctx);
            //return dbName;
        } catch (BaseXException ex) {
            throw new SystemException("Error on opening DB");
        }
    }

    @Override
    public String createDatabase() throws SystemException {
        try {
            new CreateDB(dbName).execute(ctx);
            return dbName;
        } catch (BaseXException ex) {
            throw new SystemException("Error on opening Database");
        }
    }

    @Override
    public void removeXML(String file) throws SystemException {
        try {
            new Delete(file).execute(ctx);
        } catch (BaseXException ex) {
            throw new SystemException("Error on removing a file");
        }
    }

    @Override
    public String addXMLToDb(String path) throws SystemException {
        try {
            /* add all the xml files that are in the path*/
            new Add("", path).execute(ctx);
            //new Optimize().execute(ctx);
            return path;
        } catch (BaseXException ex) {
            throw new SystemException("Error on adding files");
        }
    }

    @Override
    public void refreshDb() throws SystemException {
        addXMLToDb(path);
        removeFiles(path);
    }

    @Override
    public String useDefaultDb() throws SystemException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void removeFiles(String path) {
        File f = new File(path);
        if (f.exists()) {
            File[] files = f.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    removeFiles(f + "\\" + file);
                }
                file.delete();
            }

        }
    }

    @Override
    public void createXml(String title, String cont,String cont2, String path) throws SystemException {

        try {
            File file = new File(path + "/" + title + ".xml");
            PrintWriter write = new PrintWriter(new FileWriter(file, false));
            String prolog;
            prolog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            write.println(prolog);
            write.println("<serie>");
            write.println("<nom>" + title + "</nom>");
            write.println(cont2);
            write.println("<episodes>");
            write.println(" " + cont);
            write.println("<episodes>");
            write.println("<serie>");
            write.close();
        } catch (IOException eo) {
            throw new SystemException("Error on creating files");
        }

    }

    @Override
    public void createXml(String query, String path) throws SystemException {
        try {
            File file = new File(path + "/omdb.xml");
            PrintWriter write = new PrintWriter(new FileWriter(file, false));
            write.print(query);
            write.close();
        } catch (IOException eo) {
            throw new SystemException("Error on creating files");
        }
    }

    @Override
    public String omdb(String titre) throws SystemException {
         String res=null;
        try {
            //http://omdbapi.com/
            /* paramètre t pour le titre r pour indiquer que l'on veut du xml */
           
            String titre1 = titre.replaceAll(" ", "%20");
            //System.out.println(titre1);
            String urlStr = "http://www.omdbapi.com/?t="+titre1+"&plot=short&r=xml";

            // transformation du String en url
            URL url = new URL(urlStr);
            //connexion
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //si la connexion n'a pas pu avoir lieu, on lance une exception
            //200 est le code pour dire que la connexio est OK (404 pour dire que la page est introuvable)
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }

            //Initialisation du BufferReader pour lire la réponse HTTP
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //initialisation du constructeur de chaîne de caractère
            StringBuilder sb = new StringBuilder();
            String line;
            //on transforme les lignes en chaînes de caractère
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // on clos la connexion
            br.close();
            conn.disconnect();
            res = sb.toString();
        }
        catch (MalformedURLException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }
    

}