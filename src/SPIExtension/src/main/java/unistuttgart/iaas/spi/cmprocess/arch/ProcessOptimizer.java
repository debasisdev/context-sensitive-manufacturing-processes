package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.interfaces.ICamelSerializer;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessOptimizer;

public class ProcessOptimizer implements IProcessOptimizer, ICamelSerializer {
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
		Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
		String optimizerModel = nodeManu.getChildNodes().item(2).getTextContent();
		//Start Deployment Code for Optimization
			log.info(optimizerModel + " Will Be Executed");
		//End Deployment Code for Optimization
		return true;
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		try {
			if(this.cesDefinition.isOptimizationRequired()){
				log.info("Optimization is being Carried Out...");
				InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
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
			log.severe("Code - PROOP01: NullPointerException has Occurred.");
		} catch(Exception e) {
      		log.severe("Code - PROOP00: Unknown Exception has Occurred.");
      	} 
		if(this.optimizerRunStatus){
			log.info("Process Optimization Is Complete...");
			return "Optimization Done\n".getBytes();
		}
		else{
			log.warning("Process Optimization Failed or Strategy Not Found in Repository!");
			return "Optimization Is Not-Available/Failed\n".getBytes();
		}
	}

}
