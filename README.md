TEFEN - Commitigo ESI is a large and highly successful international company engaged in financial management.
The company employs a manual rules system called Logist to define the behavior of its operations.
This system allows rules to be articulated in a semi-natural language, without requiring a predefined order above a data dictionary.

However, creating and defining the data dictionary within the Logist system is a complex task.
Rule writers are expected to have a comprehensive understanding of all the fields and topics existing in the system, as well as the relationships between them.
This implies a preliminary necessity to define the complete data dictionary before proceeding to write the semi-natural rules that reference these fields.
While this sequence is imperative for the compilation of rules into executable code, it can be laborious, time-consuming, and prone to errors.

The primary objective of this project is to empower rule writers to compose rules more flexibly, without the upfront requirement of defining the data dictionary.
The envisaged tools will possess the capability to identify field and object names, along with relevant topics, within intricate Hebrew textual templates.
These templates comprise of strings, operators, lists of values, and other elements. Furthermore, the tools will incorporate functionalities
such as bias checking, morphology analysis, pragmatic inference for missing topics, identification and correction of spelling errors,
differentiation between existing and obsolete fields, translation of Hebrew field names into meaningful English counterparts for the database data dictionary, as well as inference of the correct field type.

The tools will also address conflicts arising from conflicting type determinations and recursive scenarios that involve defining new dictionary fields during value assignments.
The automation of the data dictionary creation process is particularly significant.
This is due to a novel feature introduced by the company.

This feature allows the formulation of rules through decision tree diagrams, which must subsequently be translated into Logist rules.
To facilitate this process, the decision tree diagrams should automatically extract the required data dictionary information from the textual descriptions.

The execution of this project will heavily rely on natural language processing (NLP) algorithms for text analysis.
NLP algorithms are potent tools applicable for various purposes, including identifying spelling errors, language translation, and sentiment analysis.
Within this project, NLP algorithms will be adapted to handle intricate Hebrew textual patterns that involve elements like strings, operators, and lists of values. They will proficiently identify spelling errors, infer data field types, and facilitate database entry.
The successful implementation of this project could potentially result in substantial enhancements in the efficiency and effectiveness of the Logist system.
To interface with the existing Logist system, the project will be developed using the Java programming language. The Stanfordnlp library will be employed to identify sentence components and perform linguistic analysis. Data generated will be stored in a Microsoft SQL Server database.

---------------


First, to start, the following jar must be installed for connecting to SQL: sqljdbc.jar.

After that, run the project on a rules file:

In the TestTheProject file, insert the updated navigation into a txt file that contains rules in the desired format, and then run the file.

For the purpose of finding fields and subjects, they must be inserted into a txt file and take care of the main change in the TestTheProject file so that it contains access to the new sentences file instead of sentences1.txt.

Then run the Main function in the TestTheProject.java file.

---------------

For any question or comment, you can contact:
Odeya Sadoun: https://www.linkedin.com/in/odeya-sadoun/

## good luck!
