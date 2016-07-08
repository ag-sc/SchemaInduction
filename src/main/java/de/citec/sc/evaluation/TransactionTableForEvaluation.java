/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.evaluation;

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
public class TransactionTableForEvaluation {

    public static void main(String[] args) {

        System.out.println("Running transaction table generation : Object Properties");
        
        getProperties("goldStandardObjectProperties.txt", "object");
        
        System.out.println("Running transaction table generation : Datatype Properties");
        getProperties("goldStandardDatatypeProperties.txt", "datatype");

    }

    public static void getProperties(String filePath, String type) {
        Set<String> properties = DocumentUtils.readFile(new File(filePath));

        HashMap<String, Integer> globalMap = new LinkedHashMap<>();

        File dir1 = new File("transactionTables/" + type + "/domainRange/");
        File dir2 = new File("transactionTables/" + type + "/range/");
        File dir3 = new File("transactionTables/" + type + "/domain/");

        if (!dir1.exists()) {
            dir1.mkdirs();
        }
        if (!dir2.exists()) {
            dir2.mkdirs();
        }
        if (!dir3.exists()) {
            dir3.mkdirs();
        }

        System.out.println(properties.size());
        
        int z=0;
        for (String property : properties) {

            z++;

            System.out.println(property+"\t"+z);

            String domainClass = property.split("\t")[1];
            String rangeClass = property.split("\t")[2];
            property = property.split("\t")[0];

            int resourceLimit = 10000;

            List<String> resources = DBpediaEndpoint.runQuery(getQueryForResources(property, resourceLimit));

            String fileContent1 = "";
            String fileContent2 = "";
            String fileContent3 = "";
            String fileName = property.replace("http://dbpedia.org/ontology/", "") + "";
            
            if(fileName.contains("/")){
                fileName = fileName.replace("/", "--");
                        
            }
            

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

            try {
                DocumentUtils.writeListToFile(dir1.getPath() + "/" + fileName + ".txt", fileContent1, false);
                DocumentUtils.writeListToFile(dir2.getPath() + "/" + fileName + ".txt", fileContent3, false);
                DocumentUtils.writeListToFile(dir3.getPath() + "/" + fileName + ".txt", fileContent2, false);
            } catch (Exception e) {
                int z2 = 1;
            }
        }

        String f = "";
        for (String k : globalMap.keySet()) {
            f += k + " " + globalMap.get(k) + "\n";
        }

        f = f.trim();
        DocumentUtils.writeListToFile("transactionTables/index" + type + ".txt", f, false);
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
