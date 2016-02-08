package uni_stuttgart.iaas.spi.cmp.utils;

import java.io.File;

/**
 * A Helper Class to All Filters that stores all the Constants/Strings/URIs used throughout the project.
 * @author Debasis Kar
 */

public final class CESConfigurations {
	
	public static final String MIDDLEWARE_DATABASE_ADDRESS = "127.0.0.1";
	public static final String MIDDLEWARE_DATABASE_PORT = "27017";
	public static final String MIDDLEWARE_DATABASE_NAME = "contextdb";
	public static final String MIDDLEWARE_DATABASE_COLLECTION_NAME = "packagecontext";
	
	public static final String CONTEXT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v1/packaging";
	public static final String XPATH_NAMESPACE = "http://www.w3.org/TR/xpath";
	public static final String SELECTION_WEIGHT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/weight-based";
	public static final String SELECTION_RANDOM_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/random";
	public static final String SELECTION_MOSTUSED_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/most-used";
	public static final String SERVICE_NAMESPACE = "http://service.cmp.spi.iaas.uni_stuttgart/";
	public static final String IPSM_NAMESPACE = "http://www.uni-stuttgart.de/iaas/ipsm/v0.2/";
	public static final String CMP_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v0.1/";
	public static final String TOSCA_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12";
	public static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/2.0/";
	public static final String BPEL_NAMESPACE = "http://docs.oasis-open.org/wsbpel/2.0/";
	public static final String ACTIVITI_NAMESPACE = "http://www.activiti.org/";
	public static final String MONGO_NAMESPACE = "https://www.mongodb.org/";
	
	public static final String SOAPSERVICE_URI = "http://localhost:8080/CESExecutorService/services/cesexecutorservice";
	public static final String AUTOSERVICE_URI = "http://localhost:8080/AutomatedDummyService/services/automatedwebservicemain";
	
	public static final String RABBIT_SERVER = "localhost";
	public static final String RABBIT_EXCHANGE_NAME = "cmp_messages";
	public static final String RABBIT_EXCHANGE_TYPE = "direct";
	public static final String RABBIT_MAIN_QUEUE_NAME = "main_queue";
	public static final String RABBIT_RESULT_QUEUE_NAME = "result_queue";
	public static final String RABBIT_CONTENT_ROUTER = "rabbitmq://localhost/cmp_messages?autoDelete=false&durable=false&skipQueueDeclare=true";
	public static final String RABBIT_RESULT_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=StopRabbit&autoDelete=false&durable=false&queue=result_queue&prefetchEnabled=true";
	public static final String RABBIT_MAIN_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=StartRabbit&autoDelete=false&durable=false&queue=main_queue";
	public static final String RABBIT_CONSOLE_OUT = "stream:out";
	public static final String RABBIT_SEND_SIGNAL = "StartRabbit";
	public static final String RABBIT_STOP_SIGNAL = "StopRabbit";
	public static final String RABBIT_MSG_CESACTIVATE = "CESActivated";
	public static final String RABBIT_MSG_QUERYMANAGER = "ContextReceived";
	public static final String RABBIT_MSG_CONTEXTANALYZER = "ContextAnalyzed";
	public static final String RABBIT_MSG_INTENTIONANALYZER = "IntentionAnalyzed";
	public static final String RABBIT_MSG_PROCESSSELECTOR = "ProcessSelected";
	public static final String RABBIT_MSG_PROCESSOPTIMIZER = "ProcessOptimized";
	public static final String RABBIT_MSG_PROCESSDISPATCHER = "ProcessDeployed";
	public static final String RABBIT_STATUS = "Status";
	public static final String RABBIT_MSG_START = "Start Packing Blankets";

	public static final String SPRING_BEAN = "META-INF/beans.xml";
	public static final String XML_EXTENSION = "xml";
	
	public static final String CONTEXTDATA_FILENAME = "ContextData";
	public static final String CONTEXTDATA_FILETYPE = ".xml";
	public static final String CONTEXTDATA_FILEPATH = "src/main/resources";
	public static final String SPLIT_EXPRESSION = "\\|";
	public static final String BLANK_STRING = "";
	public static final String COMMA_STRING = ",";
	public static final String EQUAL_STRING = "=";
	public static final String REGEX1 = "[^a-z0-9 ]";
	public static final String TIME_FORMAT = "E MMM dd HH:mm:ss Z yyyy";
	
	public static final String SOAP_FIELD_INTENTION = "Intention";
	public static final String SOAP_FIELD_DATALIST = "DataList";
	public static final String SOAP_FIELD_OUTPUT = "OutputVariable";
	public static final String SOAP_FIELD_INPUT = "InputData";
	public static final String SOAP_FIELD_NAME = "name";
	public static final String SOAP_FIELD_VALUE = "value";
	public static final String SOAP_FIELD_SUBINTENTION = "SubIntention";
	public static final String SOAP_FIELD_SUBINTENTIONS = "SubIntentions";
	public static final String SOAP_FIELD_SUBINTENTIONRELATIONS = "SubIntentionRelations";
	public static final String SOAP_FIELD_CONTEXT = "Context";
	public static final String SOAP_FIELD_REQUIREDCONTEXTS = "RequiredContexts";
	public static final String SOAP_FIELD_PROCESSREPOS = "DomainKnowHowRepository";
	public static final String SOAP_FIELD_OPTIMIATIONREQUIRED = "OptimizationRequired";
	public static final String SOAP_FIELD_CESDEFINITION = "CESDefinition";
	public static final String SOAP_FIELD_CESEXECUTOR = "CESExecutor";
	public static final String SOAP_FIELD_COMMANDACTION = "isCommandAction";
	public static final String SOAP_FIELD_EVENTDRIVEN = "isEventDriven";
	public static final String SOAP_FIELD_TARGETNAMESPACE = "targetNamespace";
	public static final String SOAP_FIELD_SER = "ser";
	public static final String SOAP_FIELD_V0 = "v0";
	public static final String SOAP_FIELD_NS = "ns";
	public static final String SOAP_FIELD_V01 = "v01";
	
	public static final File TEST_DUMP = new File("D:/dump.xml");
	public static final String PROCESSREPOSITORY_FILEPATH = "D:\\MyWorkThesis\\SPIExtension\\src\\main\\resources\\dataRepository\\ProcessRepository.xml";
	
	public static final String MONGO_FIELD_ORDERID = "orderid";
	public static final String MONGO_FIELD_DELIVERYDATE = "deliverydate";
	public static final String MONGO_FIELD_LANGUAGE = "language";
	public static final String MONGO_FIELD_MACHINE = "by";
	public static final String MONGO_FIELD_NAMESPACE = "url";
	public static final String MONGO_FIELD_TIMESTAMP = "timestamp";
	public static final String MONGO_FIELD_LONGITUDE = "longitude";
	public static final String MONGO_FIELD_LATITUDE = "latitude";
	public static final String MONGO_FIELD_SENSORDATALIST = "sensordata";
	
	public static final String REPOSITORY_FIELD_MAINMODEL = "ModelPath";
	public static final String REPOSITORY_FIELD_OPTIMIZERMODEL = "OptimizerDefinition";
	public static final String REPOSITORY_FIELD_COMPLEMENTARYMODEL = "complementMainProcess";
	public static final String REPOSITORY_FIELD_WEIGHT = "Weight";
	
	public static final String PACKAGING_INTENTION_UNITS = "unitsOrdered";
	public static final String PACKAGING_INTENTION_WORKERS = "availableWorkers";
	public static final String PACKAGING_INTENTION_SENSOR1 = "shockDetectorStatus";
	public static final String PACKAGING_INTENTION_SENSOR2 = "infraredSensorStatus";
}

/**
 * Mongo Schema
	db.packagecontext.insert([
      {
         orderid: 'DE37464358BY',	  
         deliverydate: new Date(2015,12,10),
   	   	 language: 'en_US',
         by: 'Sealing Machine: SMEX207',
         url: 'http://www.uni-stuttgart.de/iaas/cmp/v1/packaging',
         timestamp: new Timestamp(),
	   	 latitude: 48.145198,
	   	 longitude: 11.5765667,
	   	 unitsOrdered: 1000,
	   	 sensordata: [	
            {
   				availableWorkers: 4,
              	infraredSensorStatus: 'Malfunctioned',
   				shockDetectorStatus: 'Okay'
            }
         ]
      }
	])
*/
