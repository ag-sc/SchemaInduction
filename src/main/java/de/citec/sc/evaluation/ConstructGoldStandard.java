/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.evaluation;

import de.citec.sc.helper.DBpediaEndpoint;
import de.citec.sc.helper.DocumentUtils;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class ConstructGoldStandard {
    public static void main(String[] args) {
        
        
        List<String> classes = DBpediaEndpoint.runQuery(getQuery("owl:ObjectProperty"));
        String f = "";
        
        for(String c : classes){
            f+=c+"\n";
        }
        
        f = f.trim();
        DocumentUtils.writeListToFile("goldStandardObjectProperties.txt", f, false);
        System.out.println(classes.size());
        
        List<String> classes2 = DBpediaEndpoint.runQuery(getQuery("owl:DatatypeProperty"));
        String f2 = "";
        
        for(String c : classes2){
            f2+=c+"\n";
        }
        
        f2 = f2.trim();
        DocumentUtils.writeListToFile("goldStandardDatatypeProperties.txt", f2, false);
        System.out.println(classes2.size());
    }
    
    private static String getQuery(String propertyType) {

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

        q += "SELECT DISTINCT ?s ?d ?r WHERE { ?s rdf:type "+propertyType+". "
                + "?s rdfs:domain ?d. "
                + "?s rdfs:range ?r."
                + "} ";

        return q;
    }
}
