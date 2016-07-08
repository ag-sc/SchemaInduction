/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.evaluation;

import de.citec.sc.helper.DBpediaEndpoint;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class Statistics {

    public static void main(String[] args) {
        System.out.println("Statistics about Object Properties");

        String property = "ObjectProperty";
        getObjectProp(property);
        
        System.out.println("\n\nStatistics about Data Properties");

        property = "DatatypeProperty";
        getObjectProp(property);
    }

    private static void getObjectProp(String type) {
        String queryPrp = "SELECT DISTINCT ?p "
                + "WHERE { "
                + "?p a <http://www.w3.org/2002/07/owl#"+type+"> . "
                + "}";

        List<String> results = DBpediaEndpoint.runQuery(queryPrp);
        System.out.println("Number of Properties: " + results.size());

        int missingDomain = 0, missingRange = 0, missingBoth = 0, haveBoth = 0;
        
        for (String p : results) {

            String queryDomain = "SELECT DISTINCT ?domain "
                    + "WHERE { "
                    + "<"+p+"> <http://www.w3.org/2000/01/rdf-schema#domain> ?domain. "
                    + "}";
            
            String queryRange = "SELECT DISTINCT ?range "
                    + "WHERE { "
                    + "<"+p+"> <http://www.w3.org/2000/01/rdf-schema#range> ?range. "
                    + "}";
            
            List<String> resultDom = DBpediaEndpoint.runQuery(queryDomain);
            List<String> resultRan = DBpediaEndpoint.runQuery(queryRange);
            
            if(resultDom.isEmpty() && !resultRan.isEmpty()){
                missingDomain ++;
            }
            if(resultRan.isEmpty() && !resultDom.isEmpty()){
                missingRange++;
            }
            if(resultDom.isEmpty() && resultRan.isEmpty()){
                missingBoth ++;
            }
            if(!resultDom.isEmpty() && !resultRan.isEmpty()){
                haveBoth ++;
            }
        }
        
        System.out.println("Missing only domain : "+missingDomain);
        System.out.println("Missing only range : "+missingRange);
        System.out.println("Missing domain & range : "+missingBoth);
        System.out.println("Have both domain & range : "+haveBoth);
    }
}
