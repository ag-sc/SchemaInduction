# SchemaInduction

To run the code build the project

1) To generate gold standard for object and datatype properties from DBpedia
<p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r goldStandard</b></p>

2) To create transaction tables <p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r transaction </b></p>

3) To induce itemsets from transaction tables
<p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r induce </b></p>

4) To print statistics about DBpedia object & datatype properties
<p><b>java -jar SchemaInduction-1.0-jar-with-dependencies.jar -r statistics</b></p>

To download the incuded data look under <a href="/ag-sc/SchemaInduction/tree/master/induction" class="js-navigation-open" id="59c7dfd682f57e303dedae6d1eadffc8-dec3658eba8b6c9097b6f942aea326b2d0cbe383" title="transactionTables">Induced Data</a>
