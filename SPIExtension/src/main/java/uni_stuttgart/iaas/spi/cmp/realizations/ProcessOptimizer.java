package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessOptimizer;
import uni_stuttgart.iaas.spi.cmp.utils.CESConfigurations;

public class ProcessOptimizer implements IProcessOptimizer, IDataSerializer {
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
			Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
			String optimizerModel = nodeManu.getChildNodes().item(2).getTextContent().trim();
			String optimizerModelName = nodeManu.getChildNodes().item(2).getAttributes().
														getNamedItem("name").getTextContent().trim();
			//Start Deployment Code for Optimization
			log.info(optimizerModel + " Will Be Executed.");
			if(processDefinition.getProcessType().equals(CESConfigurations.BPMN_NAMESPACE)){
				if(processDefinition.getTargetNamespace().equals(CESConfigurations.ACTIVITI_NAMESPACE)){
					ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext("META-INF/beans.xml");
					DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(CESConfigurations.ACTIVITI_NAMESPACE);
					selectionProcessor.deployProcess(optimizerModel, optimizerModelName, this.cesDefinition.getInputData(), this.cesDefinition.getOutputVariable());
					appContext.registerShutdownHook();
					appContext.close();
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

}