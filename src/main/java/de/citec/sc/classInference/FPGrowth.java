/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.classInference;

import java.io.IOException;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPMax;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import de.citec.sc.evaluation.Evaluator;
import de.citec.sc.helper.DBpediaEndpoint;
import de.citec.sc.helper.DocumentUtils;
import de.citec.sc.helper.SortUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class FPGrowth {

    public static void main(String[] arg) throws IOException {

        HashMap<Integer, String> globalMap = new LinkedHashMap<>();

        //load the index of classes
        Set<String> indexOfClasses = DocumentUtils.readFile(new File("transactionTables/index.txt"));

        for (String s : indexOfClasses) {
            globalMap.put(Integer.parseInt(s.split(" ")[1]), s.split(" ")[0]);
        }

        Set<String> objProperties = DocumentUtils.readFile(new File("objectProperties.txt"));
        Set<String> properties = DocumentUtils.readFile(new File("goldStandard.txt"));

        int c = 0;

        System.out.println(properties.size());

        //delete old entries
        File f1 = new File("onlyRangeTable.txt");
        f1.delete();

        File f2 = new File("onlyDomainTable.txt");
        f2.delete();

        File f3 = new File("domainRangeTable.txt");
        f3.delete();

        String header = "Property;Induced;Confidence;T;GoldStandard;Same;MoreSpecific;LessSpecific\n";
        String header2 = "Property;InducedDomain;InducedRange;Confidence;T;GoldStandardDomain;GoldStandardRange;DomainSame;DomainMoreSpecific;DomainLessSpecific;RangeSame;RangeMoreSpecific;RangeLessSpecific\n";

        DocumentUtils.writeListToFile(f1.getName(), header, true);
        DocumentUtils.writeListToFile(f2.getName(), header, true);
        DocumentUtils.writeListToFile(f3.getName(), header2, true);

        for (String property : properties) {

//            if (!objProperties.contains(property.split("\t")[0])) {
//                continue;
//            }

//            String header = "Precision_Domain;Precision_Range;Precision_DomainRange;Recall_Domain;Recall_Range;Recall_DomainRange;F1_Domain;F1_Range;F1_DomainRange;T;Domain;Range;Confidence;GoldDomain;GoldRange";
            String domainClass = property.split("\t")[1].replace("http://dbpedia.org/ontology/", "");
            String rangeClass = property.split("\t")[2].replace("http://dbpedia.org/ontology/", "");
            property = property.split("\t")[0].replace("http://dbpedia.org/ontology/", "");

//            if (!property.contains("composer")) {
//                continue;
//            }
            System.out.println(property + " " + c);
            c++;

            String inputRange = "transactionTables/range/" + property + ".txt";
            String inputDomain = "transactionTables/domain/" + property + ".txt";
            String inputDomainRange = "transactionTables/domainRange/" + property + ".txt";

            for (double t = 1.0; t >= 0.1; t = t - 0.1) {

                String data = "";
                // the minimum support threshold
                double minsup = t; // means a minsup of 2 transaction (we used a relative support)

                // Applying the FPGROWTH algorithmMainTestFPGrowth.java
                AlgoFPGrowth algo = new AlgoFPGrowth();
//                AlgoFPMax algoMax = new AlgoFPMax();

                // Run the algorithm
                // Note that here we use "null" as output file path because we want to keep the results into memory instead of saving to a file
                Itemsets i1 = algo.runAlgorithm(inputRange, null, minsup);
                algo = new AlgoFPGrowth();
                Itemsets i2 = algo.runAlgorithm(inputDomain, null, minsup);
                algo = new AlgoFPGrowth();
                Itemsets i3 = algo.runAlgorithm(inputDomainRange, null, minsup);

                //get all itemsets for 3 different approaches
                HashMap<ItemSet, Integer> rangePatterns = processPatterns(i1.getLevels(), globalMap);
                rangePatterns = SortUtils.sortByValue(rangePatterns);
                HashMap<ItemSet, Integer> domainPatterns = processPatterns(i2.getLevels(), globalMap);
                domainPatterns = SortUtils.sortByValue(domainPatterns);
                HashMap<ItemSet, Integer> domainRangePatterns = processPatterns(i3.getLevels(), globalMap);
                domainRangePatterns = SortUtils.sortByValue(domainRangePatterns);

                Set<String> goldDomain = new HashSet<>();
                goldDomain.add(domainClass);

                Set<String> goldRange = new HashSet<>();
                goldRange.add(rangeClass);

                processDomainItemsets(property, domainPatterns, goldDomain, f2.getName(), round(t, 2));
                processRangeItemsets(property, rangePatterns, goldRange, f1.getName(), round(t, 2));
                processDomainRangeItemsets(property, domainRangePatterns, goldDomain, goldRange, f3.getName(), round(t, 2));

            }
        }
    }

    private static void processDomainRangeItemsets(String property, HashMap<ItemSet, Integer> domainRangePatterns, Set<String> goldDomain, Set<String> goldRange, String fileName, double t) {
        //String data = "Property;Itemset;ItemSetConfidence;T;GoldStandard;Same;MoreSpecific;LessSpecific";
        String data = "";

        for (ItemSet i : domainRangePatterns.keySet()) {
            if (i.getDomainClasses().isEmpty() || i.getRangeClasses().isEmpty()) {
                continue;
            }

            String inducedRanges = "";

            //concatenate all classes with "=" sign between them
            int c = 0;
            for (String r1 : i.getRangeClasses()) {

                if (c > 0) {
                    inducedRanges += "=" + r1;
                } else {
                    inducedRanges += r1;
                }
                c++;
            }

            String inducedDomains = "";

            //concatenate all classes with "=" sign between them
            c = 0;
            for (String r1 : i.getDomainClasses()) {

                if (c > 0) {
                    inducedDomains += "=" + r1;
                } else {
                    inducedDomains += r1;
                }
                c++;
            }

            String goldStandardDomains = "";
            //concatenate all classes with "=" sign between them
            c = 0;
            for (String r1 : goldDomain) {

                if (c > 0) {
                    goldStandardDomains += "=" + r1;
                } else {
                    goldStandardDomains += r1;
                }
                c++;
            }

            String goldStandardRanges = "";
            //concatenate all classes with "=" sign between them
            c = 0;
            for (String r1 : goldRange) {

                if (c > 0) {
                    goldStandardRanges += "=" + r1;
                } else {
                    goldStandardRanges += r1;
                }
                c++;
            }

            int confidence = domainRangePatterns.get(i);

            //data = "Property;ItemsetDomain;ItemsetRange;ItemSetConfidence;T;GoldStandardDomain;GoldStandardRange;Same;MoreSpecific;LessSpecific";
            //Evaluate induced ones against gold Standard
            boolean sameDomain = Evaluator.isSame(i.getDomainClasses(), goldDomain);
            boolean sameRange = Evaluator.isSame(i.getRangeClasses(), goldRange);
            boolean moreSpecificDomain = Evaluator.isInducedMoreSpecific(i.getDomainClasses(), goldDomain);
            boolean moreSpecificRange = Evaluator.isInducedMoreSpecific(i.getRangeClasses(), goldRange);
            boolean lessSpecificDomain = Evaluator.isInducedLessSpecific(i.getDomainClasses(), goldDomain);
            boolean lessSpecificRange = Evaluator.isInducedLessSpecific(i.getRangeClasses(), goldRange);

            data += property + ";" + inducedDomains + ";" + inducedRanges + ";" + confidence + ";" + t + ";" + goldStandardDomains + ";" + goldStandardRanges;

            boolean domainAdded = false;
            boolean rangeAdded = false;

            //compare domain
            if (sameDomain) {
                data += ";1;0;0";
                domainAdded = true;
            }

            if (!domainAdded) {
                if (moreSpecificDomain) {
                    data += ";0;1;0";
                    domainAdded = true;
                }
            }
            if (!domainAdded) {
                if (lessSpecificDomain) {
                    data += ";0;0;1";
                    domainAdded = true;
                }
            }

            //wrong induced domain
            if (!domainAdded) {
                data += ";0;0;0";
                domainAdded = true;
            }

            //compare range
            if (sameRange) {
                data += ";1;0;0\n";
                rangeAdded = true;
            }

            if (!rangeAdded) {
                if (moreSpecificRange) {
                    data += ";0;1;0\n";
                    rangeAdded = true;
                }
            }
            if (!rangeAdded) {
                if (lessSpecificRange) {
                    data += ";0;0;1\n";
                    rangeAdded = true;
                }
            }

            //wrong range
            if (!rangeAdded) {

                data += ";0;0;0\n";
                rangeAdded = true;

            }

            if (domainAdded && rangeAdded) {
                continue;
            }
        }

//        data = data.trim();
        if (!data.equals("")) {
            DocumentUtils.writeListToFile(fileName, data, true);
        }
    }

    private static void processDomainItemsets(String property, HashMap<ItemSet, Integer> domainPatterns, Set<String> goldDomain, String fileName, double t) {

        String data = "";

        for (ItemSet i : domainPatterns.keySet()) {
            if (i.getDomainClasses().isEmpty()) {
                continue;
            }

            String inducedDomains = "";

            //concatenate all classes with "=" sign between them
            int c = 0;
            for (String r1 : i.getDomainClasses()) {

                if (c > 0) {
                    inducedDomains += "=" + r1;
                } else {
                    inducedDomains += r1;
                }

                c++;
            }

            String goldStandardDomains = "";
            //concatenate all classes with "=" sign between them
            c = 0;
            for (String r1 : goldDomain) {

                if (c > 0) {
                    goldStandardDomains += "=" + r1;
                } else {
                    goldStandardDomains += r1;
                }
                c++;
            }

            int confidence = domainPatterns.get(i);

            //data = "Property;Itemset;ItemSetConfidence;T;GoldStandard;Same;MoreSpecific;LessSpecific"
            //Evaluate induced ones against gold Standard
            boolean same = Evaluator.isSame(i.getDomainClasses(), goldDomain);
            if (same) {
                data += property + ";" + inducedDomains + ";" + confidence + ";" + t + ";" + goldStandardDomains + ";1;0;0\n";
                continue;
            }

            boolean moreSpecific = Evaluator.isInducedMoreSpecific(i.getDomainClasses(), goldDomain);
            if (moreSpecific) {
                data += property + ";" + inducedDomains + ";" + confidence + ";" + t + ";" + goldStandardDomains + ";0;1;0\n";
                continue;
            }

            boolean lessSpecific = Evaluator.isInducedLessSpecific(i.getDomainClasses(), goldDomain);
            if (lessSpecific) {
                data += property + ";" + inducedDomains + ";" + confidence + ";" + t + ";" + goldStandardDomains + ";0;0;1\n";
                continue;
            }
            //if none of the above
            data += property + ";" + inducedDomains + ";" + confidence + ";" + t + ";" + goldStandardDomains + ";0;0;0\n";

//            data = data.trim();
        }

//        data = data.trim();
        if (!data.equals("")) {
            DocumentUtils.writeListToFile(fileName, data, true);
        }

    }

    private static void processRangeItemsets(String property, HashMap<ItemSet, Integer> rangePatterns, Set<String> goldRange, String fileName, double t) {
        //String data = "Property;Itemset;ItemSetConfidence;T;GoldStandard;Same;MoreSpecific;LessSpecific";
        String data = "";

        for (ItemSet i : rangePatterns.keySet()) {
            if (i.getRangeClasses().isEmpty()) {
                continue;
            }

            String inducedRanges = "";

            //concatenate all classes with "=" sign between them
            int c = 0;
            for (String r1 : i.getRangeClasses()) {

                if (c > 0) {
                    inducedRanges += "=" + r1;
                } else {
                    inducedRanges += r1;
                }
                c++;
            }

            String goldStandardRanges = "";
            //concatenate all classes with "=" sign between them
            c = 0;
            for (String r1 : goldRange) {
                if (c > 0) {
                    goldStandardRanges += "=" + r1;
                } else {
                    goldStandardRanges += r1;
                }
                c++;
            }

            int confidence = rangePatterns.get(i);

            //data = "Property;Itemset;ItemSetConfidence;T;GoldStandard;Same;MoreSpecific;LessSpecific";
            //Evaluate induced ones against gold Standard
            boolean same = Evaluator.isSame(i.getRangeClasses(), goldRange);
            if (same) {
                data += property + ";" + inducedRanges + ";" + confidence + ";" + t + ";" + goldStandardRanges + ";1;0;0\n";
                continue;
            }

            boolean moreSpecific = Evaluator.isInducedMoreSpecific(i.getRangeClasses(), goldRange);
            if (moreSpecific) {
                data += property + ";" + inducedRanges + ";" + confidence + ";" + t + ";" + goldStandardRanges + ";0;1;0\n";
                continue;
            }

            boolean lessSpecific = Evaluator.isInducedLessSpecific(i.getRangeClasses(), goldRange);
            if (lessSpecific) {
                data += property + ";" + inducedRanges + ";" + confidence + ";" + t + ";" + goldStandardRanges + ";0;0;1\n";
                continue;
            }

            data += property + ";" + inducedRanges + ";" + confidence + ";" + t + ";" + goldStandardRanges + ";0;0;0\n";

//            data = data.trim();
        }

//        data = data.trim();
        if (!data.equals("")) {
            DocumentUtils.writeListToFile(fileName, data, true);
        }

    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private static HashMap<ItemSet, Integer> processPatterns(List<List<Itemset>> items, HashMap<Integer, String> globalMap) {
        HashMap<ItemSet, Integer> processedPatterns = new HashMap<>();

        for (List<Itemset> list : items) {

            for (Itemset i : list) {

                Set<String> domainclasses = new HashSet<>();
                Set<String> rangeclasses = new HashSet<>();

                for (int j = 0; j < i.itemset.length; j++) {

                    String className = globalMap.get(i.itemset[j]);

                    if (className.contains("_range")) {
                        String inducedClass = className.replace("_range", "");
                        if (inducedClass.equals("Location")) {
                            inducedClass = "Place";
                        }
                        rangeclasses = reduceClasses(rangeclasses, inducedClass);
                    }
                    if (className.contains("_domain")) {
                        String inducedClass = className.replace("_domain", "");
                        if (inducedClass.equals("Location")) {
                            inducedClass = "Place";
                        }
                        domainclasses = reduceClasses(domainclasses, inducedClass);

                    }
                }

                ItemSet i1 = new ItemSet(domainclasses, rangeclasses);
                if (processedPatterns.containsKey(i1)) {
                    if (processedPatterns.get(i1) < i.support) {
                        processedPatterns.put(i1, i.support);
                    }
                } else {
                    processedPatterns.put(i1, i.support);
                }

            }
        }

        return processedPatterns;
    }

    private static Set<String> reduceClasses(Set<String> old, String newClass) {
        Set<String> newClasses = new HashSet<>();

        boolean isAdded = false;
        for (String c : old) {
            //if old class c is superClass of newClass then add the new one

            if (!isAdded) {
                if (isSubClass(c, newClass)) {
                    newClasses.add(newClass);
                    isAdded = true;
                }
                //if old class c is subClass of newClass then add the old
                if (isSubClass(newClass, c)) {
                    newClasses.add(c);
                    isAdded = true;
                }

                //if not add both since they are not related
                if (!newClasses.contains(c) && !newClasses.contains(newClass) && !isAdded) {
                    newClasses.add(c);
                    newClasses.add(newClass);
                }
            }

            else {
                newClasses.add(c);
            }
        }

        if (newClasses.contains("Artist") && newClasses.contains("MusicalArtist")) {
            System.out.println(newClasses);
        }

//        if(old.contains("Artist") && newClass.equals("MusicalArtist")){
//            System.out.println(newClasses);
//        }
//        if(old.contains("MusicalArtist") && newClass.equals("Artist")){
//            System.out.println(newClasses);
//        }
        if (old.isEmpty()) {
            newClasses.add(newClass);
        }

        return newClasses;
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

}
