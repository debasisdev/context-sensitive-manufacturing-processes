package uni_stuttgart.iaas.spi.cmp.helper;

public final class CESConfig {
	public static final String MIDDLEWARE_DATABASE_ADDRESS = "127.0.0.1";
	public static final String MIDDLEWARE_DATABASE_PORT = "27017";
	public static final String MIDDLEWARE_DATABASE_NAME = "contextdb";
	public static final String MIDDLEWARE_DATABASE_COLLECTION_NAME = "packagecontext";
	
	public static final String CONTEXT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v1/packaging";
	public static final String XPATH_NAMESPACE = "http://www.w3.org/TR/xpath";
	public static final String WEIGHT_NAMESPACE = "http://www.uni-stuttgart.de/ipsm/intention/selections/weight-based";
	public static final String SERVICE_NAMESPACE = "http://service.cmp.spi.iaas.uni_stuttgart/";
	public static final String IPSM_NAMESPACE = "http://www.uni-stuttgart.de/iaas/ipsm/v0.2/";
	public static final String CMP_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v0.1/";
	public static final String TOSCA_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12";
	
	
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
}
