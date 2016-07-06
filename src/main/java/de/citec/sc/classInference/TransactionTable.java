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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class TransactionTable {

    public static void main(String[] args) {
        getObjectProperties();
//        getDataProperties();
//        System.out.println(DBpediaEndpoint.runQuery("SELECT DISTINCT ?s ?o WHERE { ?s <http://dbpedia.org/ontology/birthDate> ?o. }  LIMIT 10"));
    }

//    public static void getDataProperties() {
//        Set<String> properties = DocumentUtils.readFile(new File("dataProperties.txt"));
//
//        for (String property : properties) {
//
//            System.out.println(property);
//            int resourceLimit = 10000;
//
//            List<String> resources = DBpediaEndpoint.runQuery(getQueryForResourcesDataProperty(property, resourceLimit));
//
//            String fileContent1 = "";
//            String fileContent2 = "";
//            String fileName = property.replace("http://dbpedia.org/ontology/", "") + "";
//
//            for (String r1 : resources) {
//                String subject = r1.split("\t")[0];
//                String object = r1.split("\t")[1].substring(r1.split("\t")[1].indexOf("http://www.w3.org/2001/XMLSchema"));
//
//                List<String> subjectClassesOnt = getClasses(subject, true);
////            List<String> subjectClassesNotOnt = getClasses(subject, false);
////            List<String> objectClassesNotOnt = getClasses(object, false);
//
//                for (String c1 : subjectClassesOnt) {
//                    fileContent1 += c1 + "\n";
//                }
//                fileContent1 += "-\n";
//
//                fileContent1 += object + "\n";
//
//                fileContent1 += "=\n";
//
//            }
//            fileContent1 = fileContent1.trim();
//
//            DocumentUtils.writeListToFile("/home/sherzod/Dropbox/DomainRangeData/DataProperties/" + fileName + ".txt", fileContent1, false);
//        }
//    }
    public static void getObjectProperties() {
//        Set<String> properties = DocumentUtils.readFile(new File("objectProperties.txt"));
        Set<String> properties = DocumentUtils.readFile(new File("goldStandard.txt"));

        HashMap<String, Integer> globalMap = new LinkedHashMap<>();

        for (String property : properties) {
            System.out.println(property);

            String domainClass = property.split("\t")[1];
            String rangeClass = property.split("\t")[2];
            property = property.split("\t")[0];

            int resourceLimit = 10000;

            List<String> resources = DBpediaEndpoint.runQuery(getQueryForResources(property, resourceLimit));

            String fileContent1 = "";
            String fileContent2 = "";
            String fileContent3 = "";
            String fileName = property.replace("http://dbpedia.org/ontology/", "") + "";

            for (String r1 : resources) {
                String subject = r1.split("\t")[0];
                String object = r1.split("\t")[1];

                List<String> subjectClassesOnt = getClasses(subject, true, domainClass);

                List<String> objectClassesOnt = getClasses(object, true, rangeClass);
                objectClassesOnt.add(rangeClass);

                boolean domainAdded = false;
                boolean rangeAdded = false;
                for (String c1 : subjectClassesOnt) {
                    String c = c1.replace("http://dbpedia.org/ontology/", "") + "_domain";

                    int index = -1;
                    if (globalMap.containsKey(c)) {
                        index = globalMap.get(c);
                    } else {
                        globalMap.put(c, globalMap.size() + 1);
                        index = globalMap.size();
                    }

                    fileContent1 += index + " ";
                    fileContent2 += index + " ";

                    domainAdded = true;
                }

                for (String c1 : objectClassesOnt) {
                    String c = c1.replace("http://dbpedia.org/ontology/", "") + "_range";

                    int index = -1;
                    if (globalMap.containsKey(c)) {
                        index = globalMap.get(c);
                    } else {
                        globalMap.put(c, globalMap.size() + 1);
                        index = globalMap.size();
                    }

                    fileContent1 += index + " ";
                    fileContent3 += index + " ";

                    rangeAdded = true;
                }

                if (resources.indexOf(r1) != resources.size() - 1) {
                    if (domainAdded) {
                        fileContent2 += "\n";
                    }
                    if (rangeAdded) {
                        fileContent3 += "\n";
                    }

                    if (domainAdded || rangeAdded) {
                        fileContent1 += "\n";
                    }

                }

            }

            DocumentUtils.writeListToFile("transactionTables/domainRange/" + fileName + ".txt", fileContent1, false);
            DocumentUtils.writeListToFile("transactionTables/range/" + fileName + ".txt", fileContent3, false);
            DocumentUtils.writeListToFile("transactionTables/domain/" + fileName + ".txt", fileContent2, false);
        }

        String f = "";
        for (String k : globalMap.keySet()) {
            f += k + " " + globalMap.get(k) + "\n";
        }

        f = f.trim();
        DocumentUtils.writeListToFile("transactionTables/index.txt", f, false);
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

    private static List<String> getClasses(String resource, boolean onlyOntology, String goldClass) {
        List<String> sClassesFromResource = DBpediaEndpoint.runQuery(getQueryForClasses(resource, onlyOntology));
        
        List<String> sClassesFromGoldClass = DBpediaEndpoint.runQuery(getQueryForGoldClasses(onlyOntology, goldClass));

        Set<String> uniqueClasses = new HashSet<>();

        for (String s : sClassesFromResource) {
            String c1 = s.split("\t")[0];
            String c2 = s.split("\t")[1];

            uniqueClasses.add(c1);
            uniqueClasses.add(c2);

        }
        
        for (String s : sClassesFromGoldClass) {
            uniqueClasses.add(s);
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

        q += "SELECT DISTINCT ?c ?p WHERE { <" + resource + "> rdf:type ?c.  OPTIONAL { ?c <http://www.w3.org/2000/01/rdf-schema#subClassOf>* ?p. } "
                + "";

        if (onlyOntology) {
            q += "FILTER (regex(?c , \"dbpedia.org/ontology\")). "
                    + "FILTER (regex(?p , \"dbpedia.org/ontology\")). "
                    + "}";
        }

        return q;
    }

    private static String getQueryForGoldClasses(boolean onlyOntology, String goldClass) {

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

        q += "SELECT DISTINCT ?c WHERE { <" + goldClass + "> rdfs:subClassOf*  ?c. "
                + "";

        if (onlyOntology) {
            q += "FILTER (regex(?c , \"dbpedia.org/ontology\")). "
                    + "}";
        }

        return q;
    }
}
