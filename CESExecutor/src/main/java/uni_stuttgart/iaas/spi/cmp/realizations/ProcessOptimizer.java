package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessOptimizer;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/**
 * A generic class that implements {@link IProcessOptimizer}, {@link IDataSerializer}, and {@link Processor}.
 * This module executes the optimization process related to the {@link TProcessDefinition} received from {@link ProcessSelector}. 
 * @author Debasis Kar
 */

public class ProcessOptimizer implements IProcessOptimizer, IDataSerializer, Processor {
	
	/**Variable to store status of optimization execution 
	 * @author Debasis Kar
	 * */
	private boolean optimizerRunStatus;
	
	/**Variable to store {@link TTaskCESDefinition} 
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(ProcessOptimizer.class.getName());
	
	/**Default constructor of {@link ProcessOptimizer}
	 * @author Debasis Kar
	 * */
	public ProcessOptimizer(){
		this.optimizerRunStatus = false;
		this.cesDefinition = null;
	}
	
	/**Parameterized constructor of {@link ProcessOptimizer}
	 * @author Debasis Kar
	 * */
	public ProcessOptimizer(TTaskCESDefinition cesDefinition){
		this.optimizerRunStatus = false;
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public boolean optimizeProcess(TProcessDefinition processDefinition) {
		try{
			//Fetch the optimization process model details from process definition received
			String optimizerModelReference = null;
			String optimizerModelPath = null;
			NodeList nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
			for(int count=0; count < nodeList.getLength(); count++){
				if(nodeList.item(count).getNodeName().equals(CESExecutorConfig.REPOSITORY_FIELD_OPTIMIZERMODEL)){
					optimizerModelReference = nodeList.item(count).getTextContent().trim();
				}
			}
			//Search the required optimization process model inside process repository
			ProcessRepository processRepository = new ProcessRepository();
			TProcessDefinitions processSet = processRepository.getProcessRepository(this.cesDefinition);
			for(TProcessDefinition optProcess : processSet.getProcessDefinition()){
				if(optProcess.getId().equals(optimizerModelReference)){
					processDefinition = optProcess;
				}
			}
			//Fetch the process model path and process name
			nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
			for(int count=0; count < nodeList.getLength(); count++){
				if(nodeList.item(count).getNodeName().equals(CESExecutorConfig.REPOSITORY_FIELD_MAINMODEL)){
					optimizerModelPath = nodeList.item(count).getTextContent().trim();
				}
			}
			String optimizerModelName = processDefinition.getName();
			//Start deployment code for optimization
			log.info(optimizerModelReference + " Will Be Executed.");
			if(processDefinition.getProcessType().equals(CESExecutorConfig.BPMN_NAMESPACE)){
				//Activiti specific execution
				if(processDefinition.getTargetNamespace().equals(CESExecutorConfig.ACTIVITI_NAMESPACE)){
					ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESExecutorConfig.SPRING_BEAN);
					DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(CESExecutorConfig.ACTIVITI_NAMESPACE);
					selectionProcessor.deployProcess(optimizerModelPath, optimizerModelName, this.cesDefinition.getInputData(), this.cesDefinition.getOutputVariable());
					appContext.registerShutdownHook();
					appContext.close();
				}
				//Add other BPMN engine specific code here (if required)
				else {
					log.info("Suitable BPMN Engine Not Found!!");
				}
			}
			//Add BPEL specific execution Here (if required)
			else {
				log.info("Suitable Workflow Engine Not Found!!");
			}
			//End deployment code for optimization
		} catch(Exception e) {
			e.printStackTrace();
      		log.severe("PROOP10: Unknown Exception has Occurred - " + e);
      		return false;
      	} 
		return true;
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		byte[] inputStream = (byte[]) exchange.getIn().getBody();
		try {
			//Check whether optimization is indeed required 
			if(this.cesDefinition.isOptimizationRequired()){
				log.info("Optimization is being Carried Out...");
				//JAXB implementation for de-serializing the process definition received from Process Selector
				InputStream byteInputStream = new ByteArrayInputStream(inputStream);
				JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
				TProcessDefinition processDef = (TProcessDefinition) rootElement.getValue();
				//Perform Optimization
				this.optimizerRunStatus = this.optimizeProcess(processDef);
			}
			else{
				log.info("Optimization is not Required by the modeler.");
			}
		} catch (NullPointerException e) {
			log.severe("PROOP01: NullPointerException has Occurred.");
		} catch(Exception e) {
      		log.severe("PROOP00: Unknown Exception has Occurred - " + e);
      	} 
		//Check optimization status before losing the control to Dispatcher
		if(this.optimizerRunStatus){
			log.info("Process Optimization Is Complete...");
		}
		else{
			log.warning("Process Optimization Failed or Strategy Not Found in Repository!");
		}
		//Forward the process definition received from Process Selector to Process Dispatcher
		return inputStream;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		//Send output of Process Optimizer to Process Dispatcher with relevant header information
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESExecutorConfig.RABBIT_SEND_SIGNAL);
        headerData.put(CESExecutorConfig.RABBIT_STATUS, CESExecutorConfig.RABBIT_MSG_PROCESSOPTIMIZER);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);		
	}
}
