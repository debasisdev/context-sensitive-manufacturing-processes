package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * A Generic Interface for Process Dispatcher Module.
 * @author Debasis Kar
 */

public interface IProcessEngine {
	
	/**
	 * Any Custom Deployment Manager has to implement the following method that will take
	 * the Process Definition received from Process Dispatcher and will deploy to an underlying
	 * workflow engine, e.g., Activiti BPMN, BPEL etc. This interface is intended for making the 
	 * deployment of the CES pluggable and Users can write their own deployment code as per their own engine.
	 * @author Debasis Kar
	 * @param TProcessDefinition
	 * @return TDataList
	 */
	public TDataList deployMainProcess(TProcessDefinition processDefinition);
	
	/**
	 * Any Custom Deployment Manager has to implement the following method that will search the Complementary
	 * Process Definition of the main process and will deploy to an underlying workflow engine, e.g., 
	 * Activiti BPMN, BPEL etc. This interface is intended for making the deployment of the CES pluggable and 
	 * Users can write their own deployment code as per their own engine. It returns the success code as a boolean value.
	 * @author Debasis Kar
	 * @param TProcessDefinition
	 * @return boolean
	 */
	public boolean deployComplementaryProcess(TProcessDefinition processDefinition);
}
