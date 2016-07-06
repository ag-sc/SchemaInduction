/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.helper;

import de.citec.sc.classInference.ItemSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sherzod
 */
public class SortUtils {
   

    public static HashMap<ItemSet, Integer> sortByValue(HashMap<ItemSet, Integer> unsortMap) {
        if (unsortMap == null) {
            return new HashMap<>();
        }

        List<Map.Entry<ItemSet, Integer>> list = new LinkedList<Map.Entry<ItemSet, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<ItemSet, Integer>>() {
            public int compare(Map.Entry<ItemSet, Integer> o1,
                    Map.Entry<ItemSet, Integer> o2) {

                return o2.getValue().compareTo(o1.getValue());

            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<ItemSet, Integer> sortedMap = new LinkedHashMap<ItemSet, Integer>();
        for (Map.Entry<ItemSet, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
