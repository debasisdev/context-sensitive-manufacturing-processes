package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
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
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessRepository;
import uni_stuttgart.iaas.spi.cmp.utils.CESConfigurations;

public class ProcessOptimizer implements IProcessOptimizer, IDataSerializer, IProcessRepository, Processor {
	
	private boolean optimizerRunStatus;
	private TTaskCESDefinition cesDefinition;
	private static final Logger log = Logger.getLogger(ProcessOptimizer.class.getName());
	
	public ProcessOptimizer(){
		this.optimizerRunStatus = false;
		this.cesDefinition = null;
	}
	
	public ProcessOptimizer(TTaskCESDefinition cesDefinition){
		this.optimizerRunStatus = false;
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public boolean optimizeProcess(TProcessDefinition processDefinition) {
		try{
			String optimizerModelReference = null;
			String optimizerModelPath = null;
			NodeList nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
			for(int count=0; count < nodeList.getLength(); count++){
				if(nodeList.item(count).getNodeName().equals(CESConfigurations.REPOSITORY_FIELD_OPTIMIZERMODEL)){
					optimizerModelReference = nodeList.item(count).getTextContent().trim();
				}
			}
			TProcessDefinitions processSet = this.getProcessRepository(this.cesDefinition);
			for(TProcessDefinition optProcess : processSet.getProcessDefinition()){
				if(optProcess.getId().equals(optimizerModelReference)){
					processDefinition = optProcess;
				}
			}
			nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
			for(int count=0; count < nodeList.getLength(); count++){
				if(nodeList.item(count).getNodeName().equals(CESConfigurations.REPOSITORY_FIELD_MAINMODEL)){
					optimizerModelPath = nodeList.item(count).getTextContent().trim();
				}
			}
			String optimizerModelName = processDefinition.getName();
			//Start Deployment Code for Optimization
			log.info(optimizerModelReference + " Will Be Executed.");
			if(processDefinition.getProcessType().equals(CESConfigurations.BPMN_NAMESPACE)){
				if(processDefinition.getTargetNamespace().equals(CESConfigurations.ACTIVITI_NAMESPACE)){
					ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESConfigurations.SPRING_BEAN);
					DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(CESConfigurations.ACTIVITI_NAMESPACE);
					selectionProcessor.deployProcess(optimizerModelPath, optimizerModelName, this.cesDefinition.getInputData(), this.cesDefinition.getOutputVariable());
					appContext.registerShutdownHook();
					appContext.close();
				}
				else {
					log.info("Suitable BPMN Engine Not Found!!");
				}
			}
			else {
				log.info("Suitable Workflow Engine Not Found!!");
			}
			//End Deployment Code for Optimization
		} catch(Exception e) {
      		log.severe("PROOP10: Unknown Exception has Occurred - " + e);
      		return false;
      	} 
		return true;
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		byte[] inputStream = (byte[]) exchange.getIn().getBody();
		try {
			if(this.cesDefinition.isOptimizationRequired()){
				log.info("Optimization is being Carried Out...");
				InputStream byteInputStream = new ByteArrayInputStream(inputStream);
				JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
				TProcessDefinition processDef = (TProcessDefinition) rootElement.getValue();
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
		if(this.optimizerRunStatus){
			log.info("Process Optimization Is Complete...");
		}
		else{
			log.warning("Process Optimization Failed or Strategy Not Found in Repository!");
		}
		return inputStream;
	}

	@Override
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition) {
		TProcessDefinitions processDefinitions = null;
		String repositoryType = cesDefinition.getDomainKnowHowRepositoryType();
		String fileName = null;
		if(repositoryType.equals(CESConfigurations.XML_EXTENSION)){
			fileName = cesDefinition.getDomainKnowHowRepository();
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new File(fileName));
			processDefinitions = (TProcessDefinitions) rootElement.getValue();
		} catch (JAXBException e) {
			log.severe("PROOP22: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("PROOP21: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("PROOP20: Unknown Exception has Occurred - " + e);
		}
		return processDefinitions;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESConfigurations.RABBIT_SEND_SIGNAL);
        headerData.put(CESConfigurations.RABBIT_STATUS, CESConfigurations.RABBIT_MSG_PROCESSOPTIMIZER);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);		
	}
}
