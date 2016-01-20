package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.interfaces.ICamelSerializer;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessEngine;

public class ProcessDispatcher implements IProcessEngine, ICamelSerializer {
	private TDataList inputData;
	private TDataList outputPlaceholder;
	private TTaskCESDefinition cesDefinition;
	private Object[] output;
	private static final Logger log = Logger.getLogger(ProcessDispatcher.class.getName());
	
	public ProcessDispatcher() {
		this.inputData = null;
		this.outputPlaceholder = null;
		this.output = null;
	}
	
	public ProcessDispatcher(TTaskCESDefinition cesDefinition){
		this.cesDefinition = cesDefinition;
		this.inputData = this.cesDefinition.getInputData();
		this.outputPlaceholder = this.cesDefinition.getOutputVariable();
	}
	
	@Override
	public Object[] deployProcess(TProcessDefinition processDefinition) {
		Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
		String mainModel = nodeManu.getChildNodes().item(0).getTextContent();
		String complementaryModel = nodeManu.getChildNodes().item(1).getTextContent();
		//Start Deployment Code for Main Model
			log.info(mainModel + " Will Be Executed" + this.inputData);
		//End Deployment Code for Main Model
		//Start Deployment Code for Complementary Model
			log.info(complementaryModel + " Will Be Executed" + this.outputPlaceholder);
		//End Deployment Code for Complementary Model
		return this.output;
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		try {
			log.info("Deployment is about to Start...");
			log.info("Optimization is being Carried Out...");
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinition processDef = (TProcessDefinition) rootElement.getValue();
			this.output = this.deployProcess(processDef);
		} catch (NullPointerException e) {
			log.severe("Code - PRODI01: NullPointerException has Occurred.");
		} catch(Exception e) {
      		log.severe("Code - PRODI00: Unknown Exception has Occurred.");
      	} finally{
			log.info("Process has been Dispatched and Deployed Successfully!!");
			log.info("CES Task Completed!!");
		}
		return null;
	}

}
