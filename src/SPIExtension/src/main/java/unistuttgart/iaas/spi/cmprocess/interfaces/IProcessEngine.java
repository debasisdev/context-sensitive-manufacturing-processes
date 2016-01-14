package unistuttgart.iaas.spi.cmprocess.interfaces;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * A Generic Interface for Context Analyzer Module.
 * @author Debasis Kar
 */

public interface IProcessEngine {
	
	/**
	 * Any Custom Deployment Manager has to implement the following method that will take
	 * the Process Definition received from Process Dispatcher and will deploy to an underlying
	 * workflow engine, e.g., Activiti BPMN, BPEL etc. This interface is intended for making the 
	 * deployment of the CES pluggable and User can write their own deployment code as per their own engine.
	 * @author Debasis Kar
	 * @param TProcessDefinition
	 * @return Object 
	 */
	public Object[] deployProcess(TProcessDefinition processDefinition);
}
