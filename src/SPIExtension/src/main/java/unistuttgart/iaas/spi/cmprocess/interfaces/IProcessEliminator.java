package unistuttgart.iaas.spi.cmprocess.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

public interface IProcessEliminator {
	/**
	 * Any Custom Context Analyzer or Intention Analyzer has to implement the following method that will take
	 * TProcessDefinitions (a set of process definitions/alternatives) and TCESDefinitionas its input parameter. 
	 * The implementation must filter Process Definitions and the final  result is stored as a list of
	 * TProcessDefinition that are validated against the satisfying Contexts or Intentions respectively.
	 * @author Debasis Kar
	 * @param TProcessDefinitions, TTaskCESDefinition
	 * @return List<TProcessDefinition> 
	 */
	public List<TProcessDefinition> eliminate(TProcessDefinitions processDefinitions, TTaskCESDefinition cesDefinition);
	
	/**
	 * Any Custom Analyzer has to implement the following method that will retrieve 
	 * the list of TProcessDefinition that pass the Context or Intention Matching Process.
	 * This list will be forwarded to the Intention Analyzer.
	 * @author Debasis Kar
	 * @param void
	 * @return List<TProcessDefinition> 
	 */
	public List<TProcessDefinition> getProcessListOfAnalyzer();

}
