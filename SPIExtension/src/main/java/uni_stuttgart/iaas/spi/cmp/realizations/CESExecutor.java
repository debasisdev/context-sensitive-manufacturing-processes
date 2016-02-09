package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.impl.DefaultCamelContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import uni_stuttgart.iaas.spi.cmp.utils.CESConfigurations;

/**
 * A generic implementation for Context-sensitive Execution Step (CES) task.
 * @author Debasis Kar
 */

public class CESExecutor {
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(CESExecutor.class.getName());
	
	/**Variable to know whether the {@link CESExecutor} has executed without any exceptions or not 
	 * @author Debasis Kar
	 * */
	private boolean success;
	
	/**Variable to store {@link TTaskCESDefinition} 
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Default constructor of {@link CESExecutor}
	 * @author Debasis Kar
	 * */
	public CESExecutor(){
		this.success = false;
		this.cesDefinition = null;
		log.info("Missing Parameters. #Exiting#");
	}
	
	/**Parameterized constructor of {@link CESExecutor} that will invoke other analyzers
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * */
	public CESExecutor(TTaskCESDefinition cesDefinition){
		this.cesDefinition = cesDefinition;
		log.info("Camel Routing Starts...");
	}
	
	public void runCESExecutor(){ 
      	try{
      		//Initializing Camel Context, RabbitMQ Factory and others for Content-based message Routing
    		CamelContext camelCon = new DefaultCamelContext();
          	ConnectionFactory conFac = new ConnectionFactory();
          	conFac.setHost(CESConfigurations.RABBIT_SERVER);
	      	Connection connection = conFac.newConnection();
		    Channel channel = connection.createChannel();
		    this.cleanAndCreateChannel(channel);
		    
		    //Routing details and rules
	        camelCon.addRoutes(new RouteBuilder() {
	            public void configure() {
	                from(CESConfigurations.RABBIT_MAIN_QUEUE)
		                .choice()
		                	//Activate Query Manager
		                	.when(header(CESConfigurations.RABBIT_STATUS).isEqualTo(CESConfigurations.RABBIT_MSG_CESACTIVATE))
		                		.process(new QueryManager(cesDefinition))
	                				.to(CESConfigurations.RABBIT_CONTENT_ROUTER)
	                		//Activate Context Analyzer
	                		.when(header(CESConfigurations.RABBIT_STATUS).isEqualTo(CESConfigurations.RABBIT_MSG_QUERYMANAGER))
		                		.process(new ContextAnalyzer(cesDefinition))
	                				.to(CESConfigurations.RABBIT_CONTENT_ROUTER)
	                		//Activate Intention Analyzer
	                		.when(header(CESConfigurations.RABBIT_STATUS).isEqualTo(CESConfigurations.RABBIT_MSG_CONTEXTANALYZER))
		                		.process(new IntentionAnalyzer(cesDefinition))
	                				.to(CESConfigurations.RABBIT_CONTENT_ROUTER)
	                		//Activate Process Selector
	                		.when(header(CESConfigurations.RABBIT_STATUS).isEqualTo(CESConfigurations.RABBIT_MSG_INTENTIONANALYZER))
		                		.process(new ProcessSelector(cesDefinition))
	                				.to(CESConfigurations.RABBIT_CONTENT_ROUTER)
	                		//Activate Process Optimizer
	                		.when(header(CESConfigurations.RABBIT_STATUS).isEqualTo(CESConfigurations.RABBIT_MSG_PROCESSSELECTOR))
		                		.process(new ProcessOptimizer(cesDefinition))
	                				.to(CESConfigurations.RABBIT_CONTENT_ROUTER)
	                		//Activate Process Dispatcher
	                		.when(header(CESConfigurations.RABBIT_STATUS).isEqualTo(CESConfigurations.RABBIT_MSG_PROCESSOPTIMIZER))
		                		.process(new ProcessDispatcher(cesDefinition))
	                				.to(CESConfigurations.RABBIT_CONTENT_ROUTER)
	                		//Route Output to the Result queue
	                		.when(header(CESConfigurations.RABBIT_STATUS).isEqualTo(CESConfigurations.RABBIT_MSG_PROCESSDISPATCHER))
	                				.to(CESConfigurations.RABBIT_CONTENT_ROUTER)
	                		//Route Failure to Standard output
	                		.otherwise()
	                				.to(CESConfigurations.RABBIT_CONSOLE_OUT);        
	            }
	        });
	        //Start Camel Context
	        camelCon.start();   
	        //Start the Context by sending some dummy message and header information
	        ProducerTemplate template = camelCon.createProducerTemplate();
	        Map<String, Object> headerData = new HashMap<>();
	        headerData.put(RabbitMQConstants.ROUTING_KEY, CESConfigurations.RABBIT_SEND_SIGNAL);
	        headerData.put(CESConfigurations.RABBIT_STATUS, CESConfigurations.RABBIT_MSG_CESACTIVATE);
	        template.sendBodyAndHeaders(CESConfigurations.RABBIT_MAIN_QUEUE, CESConfigurations.RABBIT_MSG_START, headerData);
	        //Stop Camel Context and close connection/channel objects
	        camelCon.stop();	        
	        channel.close();
	        connection.close();
	        this.success = true;
      	} catch(Exception e) {
      		this.success = false;
      		log.severe("CESEX00: Unknown Exception has Occurred - " + e);
      	} finally{
			log.info("CES Task Completed!!");
      	}
	}
	
	private void cleanAndCreateChannel(Channel channel){
	    try {
			//Removing Previously existing queues that may create deadlocks, e.g., RESOURCE-LOCKED
			channel.queueDelete(CESConfigurations.RABBIT_MAIN_QUEUE_NAME);
		    channel.queueDelete(CESConfigurations.RABBIT_RESULT_QUEUE_NAME);
	        //Creating two queues for message exchange and result retrieval
		    channel.queueDeclare(CESConfigurations.RABBIT_MAIN_QUEUE_NAME, false, false, false, null); 
		    channel.queueDeclare(CESConfigurations.RABBIT_RESULT_QUEUE_NAME, false, false, false, null);
		    //Creating an Exchange for selected topic based message forwarding.
		    channel.exchangeDeclare(CESConfigurations.RABBIT_EXCHANGE_NAME, CESConfigurations.RABBIT_EXCHANGE_TYPE, false, false, false, null);
		    //Binding already declared Queues with the Exchange using a routing key (3rd parameter)
		    channel.queueBind(CESConfigurations.RABBIT_MAIN_QUEUE_NAME, CESConfigurations.RABBIT_EXCHANGE_NAME, CESConfigurations.RABBIT_SEND_SIGNAL); 
		    channel.queueBind(CESConfigurations.RABBIT_RESULT_QUEUE_NAME, CESConfigurations.RABBIT_EXCHANGE_NAME, CESConfigurations.RABBIT_STOP_SIGNAL);
		} catch (IOException e) {
			log.severe("CESEX11: IOException has Occurred.");
		} catch(Exception e) {
      		log.severe("CESEX10: Unknown Exception has Occurred - " + e);
      	} finally{
      		log.info("Camel Context is Ready.");
      	}
	}
	
	/**
	 * This is a getter method to know whether the CES task executed successfully or not.
	 * @author Debasis Kar
	 * @param void
	 * @return boolean
	 */
	public boolean isSuccess() {
		return this.success;
	}

}
