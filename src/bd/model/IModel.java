/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.model;

import bd.exceptions.SystemException;

/**
 *
 * @author pulpito
 */
public interface IModel {
    /*
        suppression d'une base de donnees
    */
    void deleteDb(String db) throws SystemException;
    /*
        Renvoie le resultat de la requête en paramètre
    */
    String executeQuery(String query) throws SystemException;
    /*
        Renvoie toutes les base de donnees
    */
    String getDatabases() throws SystemException;

    String getElementsInCollection(String collectionName) throws SystemException;
    
    /*
        Renvoie le nom de la base de donnees ouverte
    */
    void openDb(String dbName) throws SystemException;
    /*
       Renvoie le  nom de la BD créee
    */
    String createDatabase() throws SystemException;
    /*
       Suppression du xml précisé en paramêtre
    */
    void removeXML(String file) throws SystemException;

    String addXMLToDb(String path) throws SystemException;

    void refreshDb() throws SystemException;

    /**
     *
     * @return
     * @throws SystemException
     */
    String useDefaultDb() throws SystemException;
    
    /**
     *
     * @param title
     * @param content
     * @param content2
     * @param path
     * @throws SystemException
     */
    void createXml(String title,String content,String content2,String path) throws SystemException;
    /**
     *
     * @param query
     * @param path
     * @throws SystemException
     */
    void createXml(String query,String path) throws SystemException;
    String omdb(String titre) throws SystemException;
}
