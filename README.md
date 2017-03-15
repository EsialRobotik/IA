# IA : IA du robot

##How to compile
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
```



