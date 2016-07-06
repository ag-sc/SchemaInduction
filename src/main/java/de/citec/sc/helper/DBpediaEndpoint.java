/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.helper;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * @author sherzod
 */
public class DBpediaEndpoint {

    public static List<String> runQuery(String query) {

        List<String> results = new ArrayList<>();

        try {
            Query sparqlQuery = QueryFactory.create(query);

            //QueryExecution qq = QueryExecutionFactory.create(query);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://purpur-v11:8890/sparql", sparqlQuery);

            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP) qexec).addParam("timeout", "10000");
            
            if(query.contains("ASK")){
                boolean b = qexec.execAsk();
                results.add(b+"");
                return results;
            }

            // Execute.
            ResultSet rs = qexec.execSelect();

            List<String> vars = rs.getResultVars();

            while (rs.hasNext()) {

                QuerySolution s = rs.next();

                String r = "";
                for (String v : vars) {
                    RDFNode node = s.get(v);

                    r += node.toString() + "\t";
                }

                results.add(r.trim());
            }
            
            qexec.close();
        } catch (Exception e) {

        }

        return results;
    }

}
