/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.classInference;

import de.citec.sc.helper.DBpediaEndpoint;
import de.citec.sc.helper.DocumentUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sherzod
 */
public class CreatePropertyList {

    public static void main(String[] args) {
//        getObjectProperties();
//        getDataProperties();
        getRDFProperties();

    }

    public static void getRDFProperties() {
        try {
            String query = getPropertiesQuery("rdf:Property", "dbpedia.org/property");
            List<String> properties = DBpediaEndpoint.runQuery(query);

            int resourceLimit = 1000;

            File dir = new File("properties");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File f1 = new File(dir.getPath() + "/rdfProperties.txt");

            if (f1.exists()) {
                f1.delete();
            }
            f1.createNewFile();
            
            int z =0;

            for (String p : properties) {
                z++;
                System.out.println(z+" size:"+properties.size());
                String resourceQuery = getResourcesQuery(p, resourceLimit);
                List<String> resources = DBpediaEndpoint.runQuery(resourceQuery);
                
                if (resources.size() == resourceLimit) {
                    DocumentUtils.writeListToFile(f1.getPath(), p + "\n", true);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CreatePropertyList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getDataProperties() {
        try {
            String query = getPropertiesQuery("owl:DatatypeProperty", "dbpedia.org/ontology");
            List<String> properties = DBpediaEndpoint.runQuery(query);

            int resourceLimit = 100;

            File dir = new File("properties");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File f1 = new File(dir.getPath() + "/datatypeProperties.txt");

            if (f1.exists()) {
                f1.delete();
            }
            f1.createNewFile();

            for (String p : properties) {
                String resourceQuery = getResourcesQuery(p, resourceLimit);
                List<String> resources = DBpediaEndpoint.runQuery(resourceQuery);
                if (resources.size() == resourceLimit) {
                    DocumentUtils.writeListToFile(f1.getPath(), p + "\n", true);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CreatePropertyList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getObjectProperties() {
        try {
            String query = getPropertiesQuery("owl:ObjectProperty", "dbpedia.org/ontology");
            List<String> properties = DBpediaEndpoint.runQuery(query);

            int resourceLimit = 100;

            File dir = new File("properties");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File f1 = new File(dir.getPath() + "/objectProperties.txt");

            if (f1.exists()) {
                f1.delete();
            }
            f1.createNewFile();

            for (String p : properties) {
                String resourceQuery = getResourcesQuery(p, resourceLimit);
                List<String> resources = DBpediaEndpoint.runQuery(resourceQuery);
                
                
                if (resources.size() == resourceLimit) {
                    DocumentUtils.writeListToFile(f1.getPath(), p + "\n", true);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CreatePropertyList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getResourcesQuery(String property, int resourceLimit) {

        String q = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX yago: <http://dbpedia.org/class/yago/> \n"
                + "";

        q += "SELECT DISTINCT ?s ?o WHERE { ?s <" + property + "> ?o. }  LIMIT " + resourceLimit;

        return q;
    }

    private static String getPropertiesQuery(String propertyType, String namespace) {

        String q = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX yago: <http://dbpedia.org/class/yago/> \n"
                + "";

        q += "SELECT DISTINCT ?s WHERE { ?s rdf:type " + propertyType + ".  FILTER (regex(?s , \""+namespace+"\")) . "
                + "} ";

        return q;
    }
}
