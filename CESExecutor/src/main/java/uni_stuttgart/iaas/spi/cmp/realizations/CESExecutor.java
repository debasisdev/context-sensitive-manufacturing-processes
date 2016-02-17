package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/**
 * A generic implementation for Context-sensitive Execution Step (CES) task.
 * @author Debasis Kar
 */

public class CESExecutor {
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(CESExecutor.class);
	
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
	
	/**Parameterized constructor of {@link CESExecutor}
	 * @author Debasis Kar
	 * @param cesDefinition
	 * */
	public CESExecutor(TTaskCESDefinition cesDefinition){
		this.cesDefinition = cesDefinition;
		log.info("Camel Routing Starts...");
	}
	
	/**
	 * This method facilitates message exchanges among various analyzers and finally completes by transferring the
	 * output to the result queue.
	 * @author Debasis Kar
	 * @param void
	 * @return void
	 */
	public void runCESExecutor(){ 
      	try{
      		//Initializing Camel Context, RabbitMQ Factory and others for Content-based message Routing
    		CamelContext camelCon = new DefaultCamelContext();
          	ConnectionFactory conFac = new ConnectionFactory();
          	conFac.setHost(CESExecutorConfig.RABBIT_SERVER);
	      	Connection connection = conFac.newConnection();
		    Channel channel = connection.createChannel();
		    this.cleanAndCreateChannel(channel);
		    //Routing details and rules
		    CamelRoutingCore messageRoute = new CamelRoutingCore(this.cesDefinition);
	        camelCon.addRoutes(messageRoute);
	        //Start Camel Context
	        camelCon.start();   
	        //Start the Context by sending some dummy message and header information
	        ProducerTemplate template = camelCon.createProducerTemplate();
	        Map<String, Object> headerData = new HashMap<>();
	        headerData.put(RabbitMQConstants.ROUTING_KEY, CESExecutorConfig.RABBIT_SEND_SIGNAL);
	        headerData.put(CESExecutorConfig.RABBIT_STATUS, CESExecutorConfig.RABBIT_MSG_CESACTIVATE);
	        template.sendBodyAndHeaders(CESExecutorConfig.RABBIT_MAIN_QUEUE, CESExecutorConfig.RABBIT_MSG_START, headerData);
	        //Stop Camel Context and close connection/channel objects
	        camelCon.stop();	        
	        channel.close();
	        connection.close();
	        this.success = true;
      	} catch(Exception e) {
      		this.success = false;
      		log.error("CESEX00: Unknown Exception has Occurred - " + e);
      	} finally{
			log.info("CES Task Completed!!");
      	}
	}
	
	/**
	 * This method cleans previously residing message queues on server and creates fresh message exchanges and queues.
	 * @author Debasis Kar
	 * @param channel
	 * @return void
	 */
	private void cleanAndCreateChannel(Channel channel){
	    try {
			//Removing Previously existing queues that may create deadlocks, e.g., RESOURCE-LOCKED
			channel.queueDelete(CESExecutorConfig.RABBIT_MAIN_QUEUE_NAME);
		    channel.queueDelete(CESExecutorConfig.RABBIT_RESULT_QUEUE_NAME);
	        //Creating two queues for message exchange and result retrieval
		    channel.queueDeclare(CESExecutorConfig.RABBIT_MAIN_QUEUE_NAME, false, false, false, null); 
		    channel.queueDeclare(CESExecutorConfig.RABBIT_RESULT_QUEUE_NAME, false, false, false, null);
		    //Creating an Exchange for selected topic based message forwarding.
		    channel.exchangeDeclare(CESExecutorConfig.RABBIT_EXCHANGE_NAME, CESExecutorConfig.RABBIT_EXCHANGE_TYPE, false, false, false, null);
		    //Binding already declared Queues with the Exchange using a routing key (3rd parameter)
		    channel.queueBind(CESExecutorConfig.RABBIT_MAIN_QUEUE_NAME, CESExecutorConfig.RABBIT_EXCHANGE_NAME, CESExecutorConfig.RABBIT_SEND_SIGNAL); 
		    channel.queueBind(CESExecutorConfig.RABBIT_RESULT_QUEUE_NAME, CESExecutorConfig.RABBIT_EXCHANGE_NAME, CESExecutorConfig.RABBIT_STOP_SIGNAL);
		} catch (IOException e) {
			log.error("CESEX11: IOException has Occurred.");
		} catch(Exception e) {
      		log.error("CESEX10: Unknown Exception has Occurred - " + e);
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
