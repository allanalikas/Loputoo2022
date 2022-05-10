# Privaatsust tagavad anonüümimistarkvarad 

Lõputöö eesmärk on võrrelda üldistamisel põhinevaid anonüümimistarkvarasid, et võtta neist sobivaim kasutusele Health Sense projektis. Võrdlusse kaasati kolm tarkvara: ARX, Amnesia ja Anonimatron. Võrdlus viidi läbi 10 hindamiskriteeriumi alusel, millest osad pärinevad ISO-9421 standardist ja osad tulenevad Health Sense projekti vajadustest. Võrdluse läbiviimiseks loodi 3 komplekti testandmeid, mida kasutati anonüümimisel sisendina. Sobivaimaks tarkvaraks osutus ARX, mis integreeriti Health Sense projektis valmivasse tarkvarakomplekti.

## Lähtekood ja testandmed

Lähtekoodi on võimalik vaadelda [src/main/java](https://github.com/allanalikas/Loputoo2022/tree/main/src/main/java). Testandmed ja kõik vajalikud lisafailid, et jooksutada anonüümimisprotsessi testandmete peal on võimalik leida sellest [/input](https://github.com/allanalikas/Loputoo2022/tree/main/input) kaustast

## Vajalikud tehnoloogiad

Selleks, et jooksutada käsureaprogrammi on vaja, et arvutis on installeeritud Java programmeerimiskeele versiooni 13.

## Parameetrid

- -i -input : Sisendandmete CSV faili teeviit 
- -h -hierarchy : üldistushierarhiate kausta teeviit;
- -fc -dataFieldClassification : andmeväljade klassifikatsioonide faili teeviit; 
- -o -output : väljundandmete kausta teeviit;
- -c -config : ARXi konfiguratsiooni sisaldava faili teeviit;

## Installeerimine ja kasutamine

Kasutades [IntelliJ IDEA](https://www.jetbrains.com/idea/) koodiredaktorit JetBrainsi poolt.

1. Lisada uus artefact kasutades "Project Structure" -> "Artifacts" kasutades "JAR from modules with dependencies" versiooni. Peamiseks klassiks on CLI.java
1. Genereerida artefact kasutades "Build" -> "Build Artifacts"
1. Genereeritud JAR fail peaks olema projekti out/artifacts kaustas juhul kui pole muudetud standard seadistusi IntelliJ või artefakti loomisel.
1. Genereeritud JAR faili kasutamiseks kasutage käsklust "java -jar CLI.jar < parameetrid >"

# Privacy preserving anonymization software

The aim of this thesis is to compare generalization-based anonymization software in order to implement the most suitable one in the Health Sense project. Three software were included in the comparison: ARX, Amnesia, and Anonimatron. The comparison was based on 10 evaluation criteria, some of which were derived from the ISO-9421 standard, and others were based on the requirements of the Health Sense project. For the comparison, three sets of test data were compiled and used in the anonymization process. The most suitable software was ARX which was integrated into the software suite that is being developed in the Health Sense project. 

## Application code and test data

The source code can be found under [src/main/java](https://github.com/allanalikas/Loputoo2022/tree/main/src/main/java). The test data that was created during this thesis and all accompanying metadata can be found under the [/input](https://github.com/allanalikas/Loputoo2022/tree/main/input) folder.

## Prerequisites

To work on or to run the code Java 13 needs to be installed in the host computer.

## Parameters

- -i -input : Input CSV file path; 
- -h -hierarchy : Folder path containing Generalization hierarchies;
- -fc -dataFieldClassification : Data field classification file path; 
- -o -output : Output data folder path;
- -c -config : ARX configuration file path; 

## Installation and running the code

Using IntelliJ IDE by JetBrains

1. Add a new artifact in Project Structure -> Artifacts using the "JAR from modules with dependencies" version. Main class is CLI.java
1. Build the artifact using Build -> Build Artifacts
1. The built JAR file should be located in the folder out/artifacts (Could be different depending on configuration when adding a new artifacts and IntelliJ settings)
1. Using the JAR file is to use the command "java -jar CLI.jar < parameters >"
