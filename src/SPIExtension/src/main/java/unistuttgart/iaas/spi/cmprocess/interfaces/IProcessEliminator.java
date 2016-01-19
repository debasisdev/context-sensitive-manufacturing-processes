package unistuttgart.iaas.spi.cmprocess.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

/**
 * A Generic Interface for Filtering Modules, i.e., Context Analyzer and Intention Analyzer.
 * @author Debasis Kar
 */

public interface IProcessEliminator {
	/**
	 * Any Custom Context Analyzer or Intention Analyzer has to implement the following method that will take
	 * TProcessDefinitions (a set of process definitions/alternatives) and TCESDefinitionas its input parameter. 
	 * The implementation must filter Process Definitions and the final result is stored as TProcessDefinitions 
	 * (list of TProcessDefinition) that are validated against the satisfying Contexts or Intentions respectively.
	 * @author Debasis Kar
	 * @param TProcessDefinitions, TTaskCESDefinition
	 * @return TProcessDefinitions 
	 */
	public TProcessDefinitions eliminate(TProcessDefinitions processDefinitions, TTaskCESDefinition cesDefinition);

}
