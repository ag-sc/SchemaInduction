/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.classInference;

import de.citec.sc.helper.DBpediaEndpoint;
import de.citec.sc.helper.DocumentUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class FileGenerator {

    public static void main(String[] args) {
        getObjectProperties();
        getDataProperties();
//        System.out.println(DBpediaEndpoint.runQuery("SELECT DISTINCT ?s ?o WHERE { ?s <http://dbpedia.org/ontology/birthDate> ?o. }  LIMIT 10"));
    }

    public static void getDataProperties() {
        Set<String> properties = DocumentUtils.readFile(new File("dataProperties.txt"));

        for (String property : properties) {
            
            System.out.println(property);
            int resourceLimit = 1000;

            List<String> resources = DBpediaEndpoint.runQuery(getQueryForResourcesDataProperty(property, resourceLimit));

            String fileContent1 = "";
            String fileContent2 = "";
            String fileName = property.replace("http://dbpedia.org/ontology/", "") + "";

            for (String r1 : resources) {
                String subject = r1.split("\t")[0];
                String object = r1.split("\t")[1].substring(r1.split("\t")[1].indexOf("http://www.w3.org/2001/XMLSchema"));

                List<String> subjectClassesOnt = getClasses(subject, true);
//            List<String> subjectClassesNotOnt = getClasses(subject, false);
//            List<String> objectClassesNotOnt = getClasses(object, false);

                for (String c1 : subjectClassesOnt) {
                    fileContent1 += c1 + "\n";
                }
                fileContent1 += "-\n";
                
                fileContent1 += object + "\n";

                fileContent1 += "=\n";

            }
            fileContent1 = fileContent1.trim();

            DocumentUtils.writeListToFile("/home/sherzod/Dropbox/DomainRangeData/DataProperties/"+fileName + ".txt", fileContent1, false);
        }
    }
    
    public static void getObjectProperties() {
        Set<String> properties = DocumentUtils.readFile(new File("objectProperties.txt"));

        for (String property : properties) {
            System.out.println(property);
            
            int resourceLimit = 1000;

            List<String> resources = DBpediaEndpoint.runQuery(getQueryForResources(property, resourceLimit));

            String fileContent1 = "";
            String fileContent2 = "";
            String fileName = property.replace("http://dbpedia.org/ontology/", "") + "";

            for (String r1 : resources) {
                String subject = r1.split("\t")[0];
                String object = r1.split("\t")[1];

                List<String> subjectClassesOnt = getClasses(subject, true);
                List<String> objectClassesOnt = getClasses(object, true);
//            List<String> subjectClassesNotOnt = getClasses(subject, false);
//            List<String> objectClassesNotOnt = getClasses(object, false);

                for (String c1 : subjectClassesOnt) {
                    fileContent1 += c1 + "\n";
                }
                fileContent1 += "-\n";

                for (String c1 : objectClassesOnt) {
                    fileContent1 += c1 + "\n";
                }
                fileContent1 += "=\n";

//            for (String c1 : subjectClassesNotOnt) {
//                fileContent2 += c1 + "\n";
//            }
//            fileContent2 += "-\n";
//
//            for (String c1 : objectClassesNotOnt) {
//                fileContent2 += c1 + "\n";
//            }
//            fileContent2 += "=\n";
            }
            fileContent1 = fileContent1.trim();
//        fileContent2 = fileContent2.trim();

            DocumentUtils.writeListToFile("/home/sherzod/Dropbox/DomainRangeData/ObjectProperties/"+fileName + ".txt", fileContent1, false);
//        DocumentUtils.writeListToFile(fileName + "NotOntology.txt", fileContent2, false);
        }
    }

    private static String getQueryForResources(String property, int resourceLimit) {

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
    
    private static String getQueryForResourcesDataProperty(String property, int resourceLimit) {

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

    private static List<String> getClasses(String resource, boolean onlyOntology) {
        List<String> sClasses = DBpediaEndpoint.runQuery(getQueryForClasses(resource, onlyOntology));

        Set<String> uniqueClasses = new HashSet<>();

        for (String s : sClasses) {
            String c1 = s.split("\t")[0];
            String c2 = s.split("\t")[1];

            uniqueClasses.add(c1);
            uniqueClasses.add(c2);

        }
        return new ArrayList<>(uniqueClasses);
    }

    private static String getQueryForClasses(String resource, boolean onlyOntology) {

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

        q += "SELECT DISTINCT ?c ?p WHERE { <" + resource + "> rdf:type ?c.  ?c <http://www.w3.org/2000/01/rdf-schema#subClassOf>* ?p.  ";

        if (onlyOntology) {
            q += "FILTER (regex(?c , \"dbpedia.org/ontology\")). "
                    + "FILTER (regex(?p , \"dbpedia.org/ontology\")). "
                    + "}";
        }

        return q;
    }
}
