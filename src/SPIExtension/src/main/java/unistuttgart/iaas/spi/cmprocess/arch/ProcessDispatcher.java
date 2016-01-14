package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.logging.Logger;

import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessEngine;

public class ProcessDispatcher implements IProcessEngine {
	private TDataList inputData;
	private TDataList outputPlaceholder;
	private Object[] output;
	private static final Logger log = Logger.getLogger(ProcessDispatcher.class.getName());
	
	public ProcessDispatcher() {
		this.inputData = null;
		this.outputPlaceholder = null;
		this.output = null;
	}
	
	public ProcessDispatcher(TProcessDefinition processDefinition, TTaskCESDefinition cesDefinition){
		this.inputData = cesDefinition.getInputData();
		this.outputPlaceholder = cesDefinition.getOutputVariable();
		try {
			log.info("Deployment is about to Start...");
			this.output = this.deployProcess(processDefinition);
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line in Process Optimizer!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Process Optimizer!\n" + e.getMessage());
		} finally{
			log.info("Process has been Dispatched and Deployed Successfully!!");
			log.info("CES Task Completed!!");
		}
	}
	
	@Override
	public Object[] deployProcess(TProcessDefinition processDefinition) {
		Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
		String mainModel = nodeManu.getChildNodes().item(0).getTextContent();
		String complementaryModel = nodeManu.getChildNodes().item(1).getTextContent();
		//Start Deployment Code for Main Model
			log.info(mainModel + " Will Be Executed");
		//End Deployment Code for Main Model
		//Start Deployment Code for Complementary Model
			log.info(complementaryModel + " Will Be Executed");
		//End Deployment Code for Complementary Model
		return null;
	}

}
