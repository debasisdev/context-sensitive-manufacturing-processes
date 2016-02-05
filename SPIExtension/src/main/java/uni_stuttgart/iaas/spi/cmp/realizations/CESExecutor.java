package uni_stuttgart.iaas.spi.cmp.realizations;

import java.util.logging.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
 * A Generic Implementation for Context-sensitive Execution Step (CES) Task.
 * @author Debasis Kar
 */

public class CESExecutor {
	
	/**Local Log Writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(CESExecutor.class.getName());
	
	/**Variable to Know whether the CES Executor has Executed without any Exceptions or Not 
	 * @author Debasis Kar
	 * */
	private boolean success;
	
	/**Default Constructor of CESExecutor
	 * @author Debasis Kar
	 * */
	public CESExecutor(){
		this.success = false;
		log.info("Missing Parameters. #Exiting#");
	}
	
	/**Parameterized Constructor of CESExecutor that will Invoke other Analyzer and Handlers
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * */
	public CESExecutor(TTaskCESDefinition cesDefinitionReceived){
		final TTaskCESDefinition cesDefinition = cesDefinitionReceived;
		log.info("Camel Routing Starts...");
		
		//Camel Integration
      	try{
      		//Initializing Camel Context, RabbitMQ Factory and Others for Message-Filter Based Integration
    		CamelContext camelCon = new DefaultCamelContext();
          	ConnectionFactory conFac = new ConnectionFactory();
          	conFac.setHost(CESConfigurations.RABBIT_SERVER);
	      	Connection connection = conFac.newConnection();
		    Channel channel = connection.createChannel();
		    
		    //Removing Previously existing Queues that may create deadlocks, e.g., RESOURCE-LOCKED
		    channel.queueDelete("queman_queue");	//Query Manager Queue
		    channel.queueDelete("conana_queue");	//Context Analyzer Queue
		    channel.queueDelete("intana_queue");	//Intention Analyzer Queue
		    channel.queueDelete("prosel_queue");	//Process Selector Queue
		    channel.queueDelete("proopt_queue");	//Process Dispatcher Queue
		    channel.queueDelete("prodis_queue");	//Process Optimizer Queue
		    channel.queueDelete("result_queue");	//Result Queue
	        
	        //Creating the basic Queues for CES Task Execution and Message Exchange
		    channel.queueDeclare("queman_queue", false, false, false, null); 
		    channel.queueDeclare("conana_queue", false, false, false, null); 
		    channel.queueDeclare("intana_queue", false, false, false, null); 
		    channel.queueDeclare("prosel_queue", false, false, false, null);
		    channel.queueDeclare("proopt_queue", false, false, false, null); 
		    channel.queueDeclare("prodis_queue", false, false, false, null);
		    channel.queueDeclare("result_queue", false, false, false, null);
		    
		    //Creating a multi-purpose Exchange for selected topic based message forwarding.
		    channel.exchangeDeclare("cmp_messages", "direct", false, false, false, null);
		    
		    //Binding already declared Queues with either of the Exchanges using a Routing Key (3rd Parameter)
		    channel.queueBind("queman_queue", "cmp_messages", "CESActivated"); 
		    channel.queueBind("conana_queue", "cmp_messages", "ContextReceived"); 
		    channel.queueBind("intana_queue", "cmp_messages", "ContextAnalyzed");
		    channel.queueBind("prosel_queue", "cmp_messages", "IntentionAnalyzed");
		    channel.queueBind("proopt_queue", "cmp_messages", "ProcessSelected");
		    channel.queueBind("prodis_queue", "cmp_messages", "ProcessForwarded");
		    channel.queueBind("result_queue", "cmp_messages", "ProcessDispatched");
		    
		    /**
		     * Routing Details and Rules*/
	        camelCon.addRoutes(new RouteBuilder() {
	            public void configure() {
	            	
	            	//Invoke Query Manager
	                from("direct:start")
	                	//Set Respective Routing Key
	                	.process(new Processor() {
	            			public void process(Exchange exchange) throws Exception {
	            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "CESActivated");
	            			}})
	                	//Activate the Query Manager
	                	.bean(new QueryManager(cesDefinition),"getSerializedOutput")
	                	.to(CESConfigurations.RABBIT_QUERY_MANAGER_QUEUE);
	                
	                //Forward Result to the Content Based Router
	                from(CESConfigurations.RABBIT_QUERY_MANAGER_QUEUE)
	            		//Set Respective Routing Key
	                	.process(new Processor() {
	            			public void process(Exchange exchange) throws Exception {
	            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "ContextReceived");
	            			}})
	            		.to(CESConfigurations.RABBIT_CONTENT_ROUTER);
	                
	                //Analyze Context and Submit Results to the Content Based Router
	                from(CESConfigurations.RABBIT_CONTEXT_ANALYZER_QUEUE)
	                		//Set Respective Routing Key
		                	.process(new Processor() {
	                			public void process(Exchange exchange) throws Exception {
	                				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "ContextAnalyzed");
	                			}})
		                	//Activate Context Analyzer
	                		.bean(new ContextAnalyzer(cesDefinition),"getSerializedOutput")
	                		.to(CESConfigurations.RABBIT_CONTENT_ROUTER);
	                
	                //Analyze Intention and Submit Results to the Content Based Router
	                from(CESConfigurations.RABBIT_INTENTION_ANALYZER_QUEUE)
	                		//Set Respective Routing Key
			                .process(new Processor() {
		            			public void process(Exchange exchange) throws Exception {
		            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "IntentionAnalyzed");
		            			}})
			                //Activate Intention Analyzer
	                		.bean(new IntentionAnalyzer(cesDefinition),"getSerializedOutput")
	                		.to(CESConfigurations.RABBIT_CONTENT_ROUTER);
	                
	                //Invoke Process Selector and Submit the final Process Definition to the Content Based Router
	                from(CESConfigurations.RABBIT_PROCESS_SELECTOR_QUEUE)
	                		//Set Respective Routing Key
		                	.process(new Processor() {
		            			public void process(Exchange exchange) throws Exception {
		            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "ProcessSelected");
		            			}})
		                	//Activate Process Selector
	                		.bean(new ProcessSelector(cesDefinition),"getSerializedOutput")
	                		.to(CESConfigurations.RABBIT_CONTENT_ROUTER);
	                
	                //Invoke Process Optimizer and reroute the Process Definition back to the Content Based Router
	                from(CESConfigurations.RABBIT_OPTIMIZER_QUEUE)
	                		//Set Respective Routing Key
			                .process(new Processor() {
		            			public void process(Exchange exchange) throws Exception {
		            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "ProcessForwarded");
		            			}})
			                //Perform Optimization
	                		.bean(new ProcessOptimizer(cesDefinition),"getSerializedOutput")
	                		.to(CESConfigurations.RABBIT_CONTENT_ROUTER);
	                
	                //Invoke Process Dispatcher and Submit the final result to the Content Based Router
	                from(CESConfigurations.RABBIT_DISPATCHER_QUEUE)
			              	//Set Respective Routing Key
		                	.process(new Processor() {
		            			public void process(Exchange exchange) throws Exception {
		            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "ProcessDispatched");
		            			}})
		                	//Perform Deployment/Dispatching
	                		.bean(new ProcessDispatcher(cesDefinition),"getSerializedOutput")
	                		.to(CESConfigurations.RABBIT_CONTENT_ROUTER); 
	            }
	        });
	        
	        //Start Camel Context
	        camelCon.start();
	        
	        //Start the Context by sending some dummy message
	        ProducerTemplate template = camelCon.createProducerTemplate();
	        template.sendBody("direct:start", "Start Packing E-Blankets");
	        
	        //Stop Camel Context and Close Connection/Channel Objects
	        camelCon.stop();	        
	        channel.close();
	        connection.close();
	        this.success = true;
      	} catch(Exception e) {
      		log.severe("CESEX00: Unknown Exception has Occurred - " + e);
      	} finally{
      		log.info("CES Task Execution Complete.");
      	}
	}

	public boolean isSuccess() {
		return success;
	}

}
