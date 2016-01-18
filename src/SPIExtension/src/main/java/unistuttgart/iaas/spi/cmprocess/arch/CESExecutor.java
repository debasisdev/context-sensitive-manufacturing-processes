package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.logging.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.interfaces.ICESExecutor;

public class CESExecutor implements ICESExecutor {
	private QueryManager queryManager;
	private ContextAnalyzer contextAnalyzer;
	private IntentionAnalyzer intentionAnalyzer;
	private ProcessSelector processSelector;
	private ProcessOptimizer processOptimizer;
	private ProcessDispatcher processDispatcher;

	protected boolean contextAvailable;
	protected boolean optimizationNeeded;
	private List<TProcessDefinition> outputOfContextAnalyzer;
	private List<TProcessDefinition> outputOfIntentionAnalyzer;
	private TProcessDefinition selectedProcessDefintion;

	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public CESExecutor(){
		this.queryManager = null;
		this.contextAnalyzer = null;
		this.intentionAnalyzer = null;
		this.processSelector = null;
		this.processOptimizer = null;
		this.processDispatcher = null;
		
		this.outputOfContextAnalyzer = null;
		this.outputOfIntentionAnalyzer = null;
		this.selectedProcessDefintion = null;
		this.contextAvailable = false;
		this.optimizationNeeded = false;
	}
	
	public CESExecutor(TTaskCESDefinition cesDefinition){
		this.optimizationNeeded = cesDefinition.isOptimizationRequired();
		/*Camel Integration*/
		CamelContext camelCon = new DefaultCamelContext();
      	ConnectionFactory conFac = new ConnectionFactory();
      	conFac.setHost("localhost");
      	Connection connection = conFac.newConnection();
	    Channel channel = connection.createChannel();
	        
	        channel.queueDeclare("context_queue", false, false, false, null); 
	        channel.exchangeDeclare("contexts", "direct", false, false, false, null); 
	        channel.queueBind("context_queue", "contexts", "iaasdev$12"); 
	 
	        File file = new File("src/main/resources/datarepos/ContextData.xml");
	        byte[] bFile = new byte[(int) file.length()];
	        FileInputStream fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    
	        channel.basicPublish("contexts", "iaasdev$12", null, bFile);
	        
	        context.addRoutes(new RouteBuilder() {
	            public void configure() {
	                from("rabbitmq://localhost/contexts?routingKey=iaasdev$12&autoDelete=false"
	                		+ "&durable=false&queue=context_queue")
//	                	//test-jms:queue:inq - file://src//main//resources//datarepos?noop=true
//	                	.process(new MyProcessor())
	                	.bean(new Transormer("abacus"),"transformContent")
	                	.to("rabbitmq://localhost/contexts?routingKey=iaasdev$12&autoDelete=false"
	                		+ "&durable=false&queue=context_queue");
	            }
	        });
	        context.start();
	        
	        Thread.sleep(5000);
	        context.stop();
		/*Camel Ends*/
	        
		this.runQueryManager(cesDefinition);
		if(this.contextAvailable){
			this.outputOfContextAnalyzer = this.runContextAnalyzer(cesDefinition);
		}
		else{
			log.warning("Context Not Available!");
		}
		this.outputOfIntentionAnalyzer = this.runIntentionAnalyzer(cesDefinition);
		this.selectedProcessDefintion = this.runProcessSelector(this.outputOfContextAnalyzer, this.outputOfIntentionAnalyzer, cesDefinition);
		if(this.optimizationNeeded){
			this.runProcessOptimizer(this.selectedProcessDefintion);
		}
		this.runProcessDispatcher(this.selectedProcessDefintion, cesDefinition);
	}
	
	@Override
	public void runQueryManager(TTaskCESDefinition cesDefinition){
		this.queryManager = new QueryManager(cesDefinition);
		this.contextAvailable = this.queryManager.isContextAvailable();
	}

	@Override
	public List<TProcessDefinition> runContextAnalyzer(TTaskCESDefinition cesDefinition){
		this.contextAnalyzer = new ContextAnalyzer(cesDefinition);
		return this.contextAnalyzer.getProcessListOfAnalyzer();
	}
	
	@Override
	public List<TProcessDefinition> runIntentionAnalyzer(TTaskCESDefinition cesDefinition){
		this.intentionAnalyzer = new IntentionAnalyzer(cesDefinition);
		return this.intentionAnalyzer.getProcessListOfAnalyzer();
	}

	@Override
	public TProcessDefinition runProcessSelector(List<TProcessDefinition> outputOfContextAnalyzer, 
						List<TProcessDefinition> outputOfIntentionAnalyzer, TTaskCESDefinition cesDefinition) {
		this.processSelector = new ProcessSelector(outputOfContextAnalyzer, outputOfIntentionAnalyzer, cesDefinition);
		return this.processSelector.getDispatchedProcess();
	}
	
	@Override
	public void runProcessOptimizer(TProcessDefinition processDefinition) {
		this.processOptimizer = new ProcessOptimizer(this.selectedProcessDefintion);
	}

	@Override
	public void runProcessDispatcher(TProcessDefinition processDefinition, TTaskCESDefinition cesDefinition){
		this.processDispatcher = new ProcessDispatcher(processDefinition, cesDefinition);
	}

}
