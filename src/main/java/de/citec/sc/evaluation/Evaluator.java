/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.evaluation;

import de.citec.sc.helper.DBpediaEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class Evaluator {

    public static boolean isSame(Set<String> induced, Set<String> goldStandard) {

        int r = 0;

        for (String s : induced) {
            if (goldStandard.contains(s)) {
                r++;
            }
        }

        double precision = (double) r / (double) induced.size();

        if (Double.isNaN(precision)) {
            precision = 0;
        }

        return precision == 1.0;
    }

    public static boolean isInducedMoreSpecific(Set<String> induced, Set<String> goldStandard) {

        int r = 0;

        for (String s : induced) {

            List<String> g = new ArrayList<>(goldStandard);

            //first argument is parent, second child
            if (isSubClass(g.get(0), s)) {
                r++;
            }

        }

        double precision = (double) r / (double) induced.size();

        if (Double.isNaN(precision)) {
            precision = 0;
        }

        return precision == 1.0;
    }

    public static boolean isInducedLessSpecific(Set<String> induced, Set<String> goldStandard) {

        int r = 0;

        for (String s : induced) {

            List<String> g = new ArrayList<>(goldStandard);

            //first argument is parent, second child
            if (isSubClass(s, g.get(0))) {
                r++;
            }

        }

        double precision = (double) r / (double) induced.size();

        if (Double.isNaN(precision)) {
            precision = 0;
        }

        return precision == 1.0;
    }

    public static double getPrecision(Set<String> a, Set<String> b) {

        int r = 0;

        for (String s : a) {
            if (b.contains(s)) {
                r++;
            } else {
                List<String> g = new ArrayList<>(b);

                if (isSubClass(s, g.get(0))) {
                    r++;
                } else if (isSubClass(g.get(0), s)) {
                    r++;
                }
            }
        }

//        if(a.isEmpty() && b.isEmpty()){
//            return 1.0;
//            
//        }
        double precision = (double) r / (double) a.size();

        if (Double.isNaN(precision)) {

            precision = 0;
        }

        return precision;

    }

    private static boolean isSubClass(String parent, String child) {
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

        q += "ASK WHERE { dbo:" + child + " <http://www.w3.org/2000/01/rdf-schema#subClassOf>* dbo:" + parent + ".  }";

        List<String> r = DBpediaEndpoint.runQuery(q);

        if (!r.isEmpty()) {
            if (r.contains("true")) {
                return true;
            }
        }

        return false;
    }

    public static double getRecall(Set<String> a, Set<String> b) {

        int r = 0;

        for (String s : a) {
            if (b.contains(s)) {
                r++;
            } else {
                List<String> g = new ArrayList<>(b);

                if (isSubClass(s, g.get(0))) {
                    r++;
                } else if (isSubClass(g.get(0), s)) {
                    r++;
                }
            }
        }

//        if(a.isEmpty() && b.isEmpty()){
//            return 1.0;
//            
//        }
        double recall = (double) r / (double) b.size();

        if (Double.isNaN(recall)) {

            recall = 0;
        }

        return recall;

    }

    public static double getF1(Set<String> a, Set<String> b) {

        int r = 0;

        for (String s : a) {
            if (b.contains(s)) {
                r++;
            } else {
                List<String> g = new ArrayList<>(b);

                if (isSubClass(s, g.get(0))) {
                    r++;
                } else if (isSubClass(g.get(0), s)) {
                    r++;
                }
            }
        }

//        if(a.isEmpty() && b.isEmpty()){
//            return 1.0;
//            
//        }
        double recall = (double) r / (double) b.size();
        double precision = (double) r / (double) a.size();

        double f1 = (2 * precision * recall) / (precision + recall);

        if (Double.isNaN(f1)) {

            f1 = 0;
        }

        return f1;

    }
}
