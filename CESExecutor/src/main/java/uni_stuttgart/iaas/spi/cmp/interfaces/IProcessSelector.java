package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessSelector;

/**
 * A generic interface for {@link ProcessSelector} Module.
 * @author Debasis Kar
 */

public interface IProcessSelector {
	
	/**
	 * Any custom {@link ProcessSelector} has to implement the following method that will take 
	 * {@link TProcessDefinitions} (a set of process definitions/alternatives) and TCESDefinitionas as its 
	 * input parameter. The implementation selects one process among the {@link TProcessDefinitions} available. 
	 * If one selection is strategy is available, then it selects one process as per the strategy. 
	 * If no strategy is available, then a process is chosen randomly.
	 * @author Debasis Kar
	 * @param processSet
	 * @param cesDefinition
	 * @return TProcessDefinition 
	 */
	public TProcessDefinition selectProcess(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition);
	
}
