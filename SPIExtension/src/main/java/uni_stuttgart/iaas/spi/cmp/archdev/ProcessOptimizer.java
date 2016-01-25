package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.activiti.designer.test.POPT01;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.apache.camel.Exchange;
import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessOptimizer;

public class ProcessOptimizer implements IProcessOptimizer, ICamelSerializer {
	private boolean optimizerRunStatus;
	private TTaskCESDefinition cesDefinition;
	private TDataList inputData;
	private TDataList outputPlaceholder;
	private static final Logger log = Logger.getLogger(ProcessOptimizer.class.getName());
	
	public ProcessOptimizer(){
		this.optimizerRunStatus = false;
		this.cesDefinition = null;
		this.inputData = null;
		this.outputPlaceholder = null;
	}
	
	public ProcessOptimizer(TTaskCESDefinition cesDefinition){
		this.optimizerRunStatus = false;
		this.cesDefinition = cesDefinition;
		this.inputData = this.cesDefinition.getInputData();
		this.outputPlaceholder = this.cesDefinition.getOutputVariable();
	}
	
	@Override
	public TDataList optimizeProcess(TProcessDefinition processDefinition) {
		Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
		String optimizerModel = nodeManu.getChildNodes().item(2).getTextContent();
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//Start Deployment Code for Optimization
			log.info(optimizerModel + " Will Be Executed");
			POPT01 optModel = new POPT01();
			this.outputPlaceholder = optModel.startProcess(optimizerModel, processEngine, this.inputData);
		//End Deployment Code for Optimization
		return this.outputPlaceholder;
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
				this.outputPlaceholder = this.optimizeProcess(processDef);
				this.optimizerRunStatus = true;
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
			return "Optimization Done\n".getBytes();
		}
		else{
			log.warning("Process Optimization Failed or Strategy Not Found in Repository!");
			return "Optimization Is Not-Available/Failed\n".getBytes();
		}
	}

}
