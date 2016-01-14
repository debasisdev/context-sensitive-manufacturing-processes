package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.logging.Logger;

import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessOptimizer;

public class ProcessOptimizer implements IProcessOptimizer {
	private boolean optimizerRunStatus;
	private static final Logger log = Logger.getLogger(ProcessOptimizer.class.getName());
	
	public ProcessOptimizer(){
		this.optimizerRunStatus = false;
	}
	
	public ProcessOptimizer(TProcessDefinition processDefinition){
		try {
			log.info("Optimization is being Carried Out...");
			this.optimizerRunStatus = this.optimizeProcess(processDefinition);
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Process Optimizer!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Process Optimizer!\n" + e.getMessage());
		} finally{
			if(this.optimizerRunStatus)
				log.info("Process Optimization Is Complete...");
			else
				log.warning("Process Optimization Failed or Strategy Not Found in Repository!");
		}
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

}
