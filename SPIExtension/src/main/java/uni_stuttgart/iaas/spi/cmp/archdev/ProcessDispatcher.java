package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.activiti.designer.test.ComplementaryMaintenance;
import org.activiti.designer.test.ManualPacking;
import org.activiti.designer.test.SemiautomaticPackingRepair;
import org.activiti.designer.test.SemiautomaticPackingReinstall;
import org.activiti.designer.test.SemiautomaticPackingPlain;
import org.apache.camel.Exchange;
import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessEngine;

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
		String complementaryModel = nodeManu.getChildNodes().item(1).getTextContent();
		//Start Deployment Code for Main Model
			log.info(mainModel + " Will Be Executed.");
			if(processDefinition.getId().equals("PRS001")){
				SemiautomaticPackingRepair primeModel = new SemiautomaticPackingRepair();
				this.outputPlaceholder = primeModel.startProcess(mainModel, this.inputData, this.outputPlaceholder);
			}
			else if(processDefinition.getId().equals("PRS002")){
				SemiautomaticPackingReinstall primeModel = new SemiautomaticPackingReinstall();
				this.outputPlaceholder = primeModel.startProcess(mainModel, this.inputData, this.outputPlaceholder);
			}
			else if(processDefinition.getId().equals("PMX001")){
				ManualPacking primeModel = new ManualPacking();
				this.outputPlaceholder = primeModel.startProcess(mainModel, this.inputData, this.outputPlaceholder);
			}
			else if(processDefinition.getId().equals("PSM001")){
				SemiautomaticPackingPlain primeModel = new SemiautomaticPackingPlain();
				this.outputPlaceholder = primeModel.startProcess(mainModel, this.inputData, this.outputPlaceholder);
			}
			else{
				log.info("Method Definition Not Found!!");
			}
		//End Deployment Code for Main Model
		//Start Deployment Code for Complementary Model
			log.info(complementaryModel + " Will Be Executed.");
			ComplementaryMaintenance compModel = new ComplementaryMaintenance();
			compModel.startProcess(complementaryModel, this.inputData, this.outputPlaceholder);
		//End Deployment Code for Complementary Model
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
