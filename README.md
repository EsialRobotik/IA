# IA : IA du robot

## How to compile

To compile this project you will need to have [maven](https://maven.apache.org/install.html) install.

Then you will need to install the dependency to the project [API](https://github.com/EsialRobotik/API) of EsialRobotik. 
There is no available maven repository for this project so you will need to clone/compile/install
it.

```bash
git clone https://github.com/EsialRobotik/API.git
cd API
mvn install
```

Once done execute in the following in the cloned repository.

```bash
mvn compile
mvn install // Génère le jar contenant tout dans core/core-1.0-jar-with-dependencies.jar
```

## Génération du fichier de table
* Ecrire le fichier table.json dans pathFinding pour définir les zones interdites
* Lancer le main dans pathFinding/../Table.json pour générer le fichier table.tbl


