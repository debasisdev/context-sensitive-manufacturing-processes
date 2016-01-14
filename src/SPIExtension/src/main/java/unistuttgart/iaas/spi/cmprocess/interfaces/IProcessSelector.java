package unistuttgart.iaas.spi.cmprocess.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

public interface IProcessSelector {
	
	/**
	 * Any Custom Process Selector has to implement the following method that will take TProcessDefinitions 
	 * (a set of process definitions/alternatives) and TCESDefinitionas its input parameter. The implementation 
	 * must select one process among the Process Definitions. If one selection is strategy is available, then it
	 * selects one process as per the strategy. If no strategy is available, then a process is chosen randomly.
	 * @author Debasis Kar
	 * @param List<TProcessDefinition>, TTaskCESDefinition
	 * @return TProcessDefinition 
	 */
	public TProcessDefinition selectProcess(List<TProcessDefinition> processDefinitionList, TTaskCESDefinition cesDefinition);
	
	/**
	 * Any Custom Selector has to implement the following method that will retrieve the TProcessDefinition 
	 * that will be forwarded to the Process Optimizer and Process Dispatcher respectively.
	 * @author Debasis Kar
	 * @param void
	 * @return TProcessDefinition 
	 */
	public TProcessDefinition getDispatchedProcess();
}
