package uni_stuttgart.iaas.spi.cmp.archint;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

/**
 * A Generic Interface for Process Selector Module.
 * @author Debasis Kar
 */

public interface IProcessSelector {
	
	/**
	 * Any Custom Process Selector has to implement the following method that will take TProcessDefinitions 
	 * (a set of process definitions/alternatives) and TCESDefinitionas its input parameter. The implementation 
	 * must select one process among the Process Definitions. If one selection is strategy is available, then it
	 * selects one process as per the strategy. If no strategy is available, then a process is chosen randomly.
	 * @author Debasis Kar
	 * @param TProcessDefinitions, TTaskCESDefinition
	 * @return TProcessDefinition 
	 */
	public TProcessDefinition selectProcess(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition);
	
}
