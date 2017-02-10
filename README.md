# SchemaInduction

To run the code build the project

1) To generate gold standard for object and datatype properties from DBpedia
<p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r goldStandard</b></p>

2) To create transaction tables <p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r transaction </b></p>

3) To induce itemsets from transaction tables
<p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r induce </b></p>

4) To print statistics about DBpedia object & datatype properties
<p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r statistics</b></p>

To download the incuded data look under <a href="https://github.com/ag-sc/SchemaInduction/tree/master/induction" class="js-navigation-open" title="transactionTables">Induced Data</a>


<a href="https://pub.uni-bielefeld.de/publication/2904967" class="js-navigation-open" title="publication">Ell, B., Hakimov, S., & Cimiano, P. (Accepted). Statistical Induction of Coupled Domain/Range Restrictions from RDF Knowledge Bases. Proceedings of the 15th International Semantic Web Conference</a>

