/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.classInference;

import de.citec.sc.evaluation.Statistics;
import de.citec.sc.evaluation.TransactionTableForEvaluation;
import de.citec.sc.evaluation.FISMiningForEvaluation;
import de.citec.sc.evaluation.ConstructGoldStandard;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sherzod
 */
public class Main {
    private static final Map<String, String> PARAMETERS = new HashMap<>();

    private static final String PARAMETER_PREFIX = "-";
    private static final String PARAM_RUN = "-r";
    
    public static void main(String[] args) {
//        args = new String[2];
//        args[0] ="-r";
//        args[1] ="induceEvaluation";
        
        readParamsFromCommandLine(args);
        
        if (PARAMETERS.get(PARAM_RUN).equals("transactionEvaluation")) {
            try {
                TransactionTableForEvaluation.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (PARAMETERS.get(PARAM_RUN).equals("induceEvaluation")) {
            try {
                FISMiningForEvaluation.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (PARAMETERS.get(PARAM_RUN).equals("goldStandard")) {
            try {
                ConstructGoldStandard.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (PARAMETERS.get(PARAM_RUN).equals("statistics")) {
            try {
                Statistics.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static void readParamsFromCommandLine(String[] args) {
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith(PARAMETER_PREFIX)) {
                    PARAMETERS.put(args[i], args[i++ + 1]); // Skip value
                }
            }
        }
    }
}
