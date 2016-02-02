package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessEngine;
import uni_stuttgart.iaas.spi.cmp.helper.ActivitiExecutor;
import uni_stuttgart.iaas.spi.cmp.helper.CESConfig;

public class ProcessDispatcher implements IProcessEngine, ICamelSerializer {
	private TDataList inputData;
	private TDataList outputPlaceholder;
	private TTaskCESDefinition cesDefinition;
	private static final Logger log = Logger.getLogger(ProcessDispatcher.class.getName());
	
	public ProcessDispatcher() {
		this.inputData = null;
		this.outputPlaceholder = null;
	}
	
	public ProcessDispatcher(TTaskCESDefinition cesDefinition){
		this.cesDefinition = cesDefinition;
		this.inputData = this.cesDefinition.getInputData();
		this.outputPlaceholder = this.cesDefinition.getOutputVariable();
	}
	
	@Override
	public TDataList deployProcess(TProcessDefinition processDefinition) {
		Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
		String mainModel = nodeManu.getChildNodes().item(0).getTextContent();
		String complementaryModel = nodeManu.getChildNodes().item(1).getTextContent().trim();
		String complemetaryModelName = nodeManu.getChildNodes().item(1).getAttributes().
												getNamedItem("name").getTextContent().trim();
		//Start Deployment Code for Main Model
		log.info(mainModel + " Will Be Executed.");
		if(processDefinition.getProcessType().equals(CESConfig.BPMN_NAMESPACE)){
			if(processDefinition.getTargetNamespace().equals(CESConfig.ACTIVITI_NAMESPACE)){
				ActivitiExecutor activitiDispatcher = new ActivitiExecutor(mainModel, processDefinition.getName());
				this.outputPlaceholder = activitiDispatcher.startProcess(this.inputData, this.outputPlaceholder);
				//Start Deployment Code for Complementary Model
				log.info(complementaryModel + " Will Be Executed.");
				activitiDispatcher = new ActivitiExecutor(complementaryModel, complemetaryModelName);
				//We don't really care whether a complementary process runs successfully or not.
				activitiDispatcher.startProcess(this.inputData, this.outputPlaceholder);
				//End Deployment Code for Complementary Model
			}
		}
		else {
			log.info("Suitable Workflow Engine Not Found!!");
		}
		//End Deployment Code for Main Model
		return this.outputPlaceholder;
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		de.uni_stuttgart.iaas.cmp.v0.ObjectFactory cmpMaker = new de.uni_stuttgart.iaas.cmp.v0.ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			log.info("Deployment is about to Start...");
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinition processDef = (TProcessDefinition) rootElement.getValue();
			this.outputPlaceholder = this.deployProcess(processDef);
			
			TTaskCESDefinition cesDef = new TTaskCESDefinition();
			cesDef.setOutputVariable(this.outputPlaceholder);
			jaxbContext = JAXBContext.newInstance(cmpMaker.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<TTaskCESDefinition> rootElem = cmpMaker.createCESDefinition(cesDef);
			jaxbMarshaller.marshal(rootElem, outputStream);
		} catch (NullPointerException e) {
			log.severe("PRODI01: NullPointerException has Occurred.");
		} catch(Exception e) {
      		log.severe("PRODI00: Unknown Exception has Occurred - " + e);
      	} finally{
			log.info("Process has been Dispatched and Deployed Successfully!!");
			log.info("CES Task Completed!!");
		}
		return outputStream.toByteArray();
	}

}
