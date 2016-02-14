# README #

This would normally put forward the steps that are necessary to get this application up and running. Manufacturing processes need to be updated regularly to stay competitive in the market. With the emergence of Internet of Things (IoT), the manufacturing processes can be made smarter so that they can possibly adapt themselves to the execution context.

### What is this repository for? ###

* Quick summary

Contains 7 distinct Projects, i.e., AutomatedDummyService, CESDelegateForActiviti, CESExecutor, CESExecutorService, CESExtensionForActiviti, CESHelperModule, and CESTaskDummyProcess.

* Version: 0.0.1-SNAPSHOT

### How do I get set up? ###

* **AutomatedDummyService**:

This is a dummy SOAP based Web Service that is used for demonstrating the simple tasks running inside a factory, e.g., packing, sealing, shipping, etc. This project 
needs to be deployed inside a Tomcat Server container for a naive demonstration of whole CES task execution process.

* **CESDelegateForActiviti**: This is the starting point of the CES Task executiin, since the CESTaskDelegate class inside this project is searched and executed at the very moment
when the control gets transferred to a CES Task. This delegate class designed for Activiti BPMN Engine (http://www.activiti.org/userguide/) which facilitates the execution of CES Task by calling either **CESExecutor** or 
**CESExecutorService** projects.

* **CESExecutor**: This contains all the realization classes related to the architecture of the CES Task, interfaces for the generic execution, and few utility classes for our specific scenario
such that out of given process models, the process model according to our context data received gets deployed on a process engine.

* **CESExecutorService**: This is an asynchronous implementation of **CESExecutor** project that is Web-Service based. Thus, any CES Task delegate would have to request this webservice for the execution of a CES Task and get
the output back as response of this Web-service.

* **CESExtensionForActiviti**: This is the modeling tool extension designed for Activiti Designer to be used along with Activiti BPMN Engine (http://www.activiti.org/userguide/).
Execute `<mvn install>` command and get a JAR container generated. Start Activiti Designer and from the menu, select **Window > Preferences**.
In the preferences screen, type **user** as keyword. You should see an option to access the User Libraries in Eclipse in the Java section.
Add the JAR as external library inside **Activiti Designer Extensions** group. This group is is automatically added to new Activiti projects you create. 

* **CESHelperModule**: These are the helper classes, i.e., scenarios specific classes those facilitate Service or Script based tasks of BPMN process models need to be in the classpath for the successful execution
of process models defined in process repository.

* **CESTaskDummyProcess**: This project contains test cases that tries to simulate the execution of a business process modeled with our proposed CES task through **CESExecutor** or 
**CESExecutorService** projects.

### Directory Details ###

* **/doc**: This contains the related master thesis report describing the abstract concepts realized in this project.

* **/domainknowhow**: This contins all the BPMN/BPEL process models along with the Process Repsoitory file. It acts like a local repository whoses path needs to be given in the CES Task
GUI for the Executor's knowledge of path of the process repository.

### Contribution guidelines ###
Make self-descriptive commit messages while contributing to the code or data inside project.

### Who do I talk to? ###

* Debasis Kar
* C. Timurhan Sungur
