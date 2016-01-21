package uni_stuttgart.iaas.spi.cmp.archdev;

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

/**
 * A Generic Implementation for Context-sensitive Execution Step (CES) Task.
 * @author Debasis Kar
 */

public class CESExecutor {
	
	/**Local Log Writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(CESExecutor.class.getName()); 
	
	/**Default Constructor of CESExecutor
	 * @author Debasis Kar
	 * */
	public CESExecutor(){
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
          	conFac.setHost("localhost");
	      	Connection connection = conFac.newConnection();
		    Channel channel = connection.createChannel();
		    
		    //Removing Previously existing Queues that may create deadlocks, e.g., RESOURCE-LOCKED
		    channel.queueDelete("prodis_queue");	//Process Dispatcher Queue
		    channel.queueDelete("proopt_queue");	//Process Optimizer Queue
		    channel.queueDelete("intana_queue");	//Intention Analyzer Queue
	        channel.queueDelete("conana_queue");	//Context Analyzer Queue
	        channel.queueDelete("queman_queue");	//Query Manager Queue
	        
	        //Creating the basic Queues for CES Task Execution and Message Exchange
		    channel.queueDeclare("queman_queue", false, false, false, null); 
		    channel.queueDeclare("conana_queue", false, false, false, null); 
		    channel.queueDeclare("intana_queue", false, false, false, null); 
		    channel.queueDeclare("proopt_queue", false, false, false, null); 
		    channel.queueDeclare("prodis_queue", false, false, false, null); 
		    
		    //Creating a multi-purpose Exchange for selected topic based message forwarding.
		    channel.exchangeDeclare("cmp_messages", "direct", false, false, false, null);
		    //Creating a fan-out Exchange for multi-casting.
		    channel.exchangeDeclare("cmp_process", "fanout", false, false, false, null);
		    
		    //Binding already declared Queues with either of the Exchanges using a Routing Key (3rd Parameter)
		    channel.queueBind("queman_queue", "cmp_messages", "infoqm"); 
		    channel.queueBind("conana_queue", "cmp_messages", "infoca"); 
		    channel.queueBind("intana_queue", "cmp_messages", "infoia");
		    channel.queueBind("proopt_queue", "cmp_process", "infopo");
		    channel.queueBind("prodis_queue", "cmp_process", "infopd");
		    
		    /**
		     * Routing Details and Rules*/
	        camelCon.addRoutes(new RouteBuilder() {
	            public void configure() {
	            	
	            	//Query Manager Invocation
	                from("direct:start")
	                	//Set Respective Routing Key
	                	.process(new Processor() {
	            			public void process(Exchange exchange) throws Exception {
	            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "infoqm");
	            			}})
	                	//Activate the Query Manager and Unicast to Queue whether Context data is available or not.
	                	.bean(new QueryManager(cesDefinition),"getSerializedOutput")
	                	.to("rabbitmq://localhost/cmp_messages?routingKey=infoqm&autoDelete=false"
	                		+ "&durable=false&queue=queman_queue");
	                
	                //Context Analyzer Invocation - Filter 1
	                from("rabbitmq://localhost/cmp_messages?routingKey=infoqm&autoDelete=false"
	                		+ "&durable=false&queue=queman_queue")
	                		//Set Respective Routing Key
		                	.process(new Processor() {
	                			public void process(Exchange exchange) throws Exception {
	                				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "infoca");
	                			}})
		                	//Activate Context Analyzer and Unicast the satisfying TProcessDefinitions to the queue
	                		.bean(new ContextAnalyzer(cesDefinition),"getSerializedOutput")
	                		.to("rabbitmq://localhost/cmp_messages?routingKey=infoca&autoDelete=false"
	                				+ "&durable=false&queue=conana_queue");
	                
	                //Intention Analyzer Invocation - Filter 2
	                from("rabbitmq://localhost/cmp_messages?routingKey=infoca&autoDelete=false"
	                		+ "&durable=false&queue=conana_queue")
	                		//Set Respective Routing Key
			                .process(new Processor() {
		            			public void process(Exchange exchange) throws Exception {
		            				exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "infoia");
		            				System.out.println("debasis the don");
		            			}})
			                //Activate Intention Analyzer and Unicast the satisfying TProcessDefinitions to the queue
	                		.bean(new IntentionAnalyzer(cesDefinition),"getSerializedOutput")
	                		.to("rabbitmq://localhost/cmp_messages?routingKey=infoia&autoDelete=false"
		                				+ "&durable=false&queue=intana_queue");
	                
	                //Process Selector Invocation - Filter 3
	                from("rabbitmq://localhost/cmp_messages?routingKey=infoia&autoDelete=false"
	                		+ "&durable=false&queue=intana_queue")
	                		//Set Exchange Name for Multi-casting TProcessDefinition to Optimizer and Dispatcher
		                	.process(new Processor() {
		            			public void process(Exchange exchange) throws Exception {
		            				exchange.getIn().setHeader(RabbitMQConstants.EXCHANGE_NAME, "cmp_process");
		            			}})
		                	//Activate Process Selector and broadcast the TProcessDefinition to the queues of Optimizer and Dispatcher
	                		.bean(new ProcessSelector(cesDefinition),"getSerializedOutput")
	                		.to("rabbitmq://localhost/cmp_process?autoDelete=false&durable=false&"
	                				+ "exchangeType=fanout&skipQueueDeclare=true");
	                
	                //Process Optimizer Invocation
	                from("rabbitmq://localhost/cmp_process?autoDelete=false&durable=false&"
	                		+ "exchangeType=fanout&queue=proopt_queue")
			                //Perform Optimization and flush the queue to the Console Output
	                		.bean(new ProcessOptimizer(cesDefinition),"getSerializedOutput")
	                		.to("stream:out");
	                
	                //Process Dispatcher Invocation
	                from("rabbitmq://localhost/cmp_process?autoDelete=false&durable=false&"
	                		+ "exchangeType=fanout&queue=prodis_queue")
		                	//Perform Optimization and Unicast the result to the specific queue
	                		.bean(new ProcessDispatcher(cesDefinition),"getSerializedOutput")
	                		.to("stream:out"); 
	            }
	        });
	        
	        //Start Camel Context
	        camelCon.start();
	        
	        //Start the Context by sending some dummy message
	        ProducerTemplate template = camelCon.createProducerTemplate();
	        template.sendBody("direct:start", "Start Packaging of E-Blankets");
	        
	        //Stop Camel Context and Close Connection/Channel Objects
	        camelCon.stop();	        
	        channel.close();
	        connection.close();
	        
      	} catch(Exception e) {
      		log.severe("CESEX00: Unknown Exception has Occurred - " + e);
      	} finally{
      		log.info("CES Task Execution Complete.");
      	}
	}

}
