package uni_stuttgart.iaas.spi.cmp.helper;

/**
 * A Helper Class to All Filters that stores all the Constants/Strings/URIs used throughout the project.
 * @author Debasis Kar
 */

public final class CESConfig {
	
	public static final String MIDDLEWARE_DATABASE_ADDRESS = "127.0.0.1";
	public static final String MIDDLEWARE_DATABASE_PORT = "27017";
	public static final String MIDDLEWARE_DATABASE_NAME = "contextdb";
	public static final String MIDDLEWARE_DATABASE_COLLECTION_NAME = "packagecontext";
	
	public static final String CONTEXT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v1/packaging";
	public static final String XPATH_NAMESPACE = "http://www.w3.org/TR/xpath";
	public static final String WEIGHT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/weight-based";
	public static final String SERVICE_NAMESPACE = "http://service.cmp.spi.iaas.uni_stuttgart/";
	public static final String IPSM_NAMESPACE = "http://www.uni-stuttgart.de/iaas/ipsm/v0.2/";
	public static final String CMP_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v0.1/";
	public static final String TOSCA_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12";
	public static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/2.0/";
	public static final String ACTIVITI_NAMESPACE = "http://www.activiti.org/cmp-bpmn";
	
	public static final String SOAPSERVICE_URI = "http://localhost:8080/CESExecutorService/services/cesexecutorservice";
	
	public static final String RABBIT_SERVER = "localhost";
	public static final String RABBIT_CONTENT_ROUTER = "rabbitmq://localhost/cmp_messages?autoDelete=false&durable=false&skipQueueDeclare=true";
	public static final String RABBIT_RESULT_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=ProcessDispatched&autoDelete=false&durable=false&queue=result_queue&prefetchEnabled=true";
	public static final String RABBIT_QUERY_MANAGER_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=CESActivated&autoDelete=false&durable=false&queue=queman_queue";
	public static final String RABBIT_CONTEXT_ANALYZER_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=ContextReceived&autoDelete=false&durable=false&queue=conana_queue";
	public static final String RABBIT_INTENTION_ANALYZER_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=ContextAnalyzed&autoDelete=false&durable=false&queue=intana_queue";
	public static final String RABBIT_PROCESS_SELECTOR_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=IntentionAnalyzed&autoDelete=false&durable=false&queue=prosel_queue";
	public static final String RABBIT_OPTIMIZER_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=ProcessSelected&autoDelete=false&durable=false&queue=proopt_queue";
	public static final String RABBIT_DISPATCHER_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=ProcessForwarded&autoDelete=false&durable=false&queue=prodis_queue";

	public static final String TEST_DUMP = "D:/dev.xml";
	
	public static final String MONGO_FIELD_ORDERID = "orderid";
	public static final String MONGO_FIELD_DELIVERYDATE = "deliverydate";
	public static final String MONGO_FIELD_LANGUAGE = "language";
	public static final String MONGO_FIELD_MACHINE = "by";
	public static final String MONGO_FIELD_NAMESPACE = "url";
	public static final String MONGO_FIELD_TIMESTAMP = "timestamp";
	public static final String MONGO_FIELD_LONGITUDE = "longitude";
	public static final String MONGO_FIELD_LATITUDE = "latitude";
	public static final String MONGO_FIELD_SENSORDATALIST = "sensordata";
	
}

/*Mongo Schema
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
])*/
