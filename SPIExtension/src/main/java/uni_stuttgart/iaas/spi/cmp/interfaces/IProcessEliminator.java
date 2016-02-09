package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntentions;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.realizations.ContextAnalyzer;
import uni_stuttgart.iaas.spi.cmp.realizations.IntentionAnalyzer;

/**
 * A generic interface for the filtering modules, i.e., {@link ContextAnalyzer} and {@link IntentionAnalyzer}. 
 * @author Debasis Kar
 */

public interface IProcessEliminator {
	/**
	 * Any custom {@link ContextAnalyzer} or {@link IntentionAnalyzer} has to implement the 
	 * following method that will take {@link TProcessDefinitions} (a set of process definitions/alternatives) 
	 * and {@link TCESDefinitionas} as its inputs. The implementation must filter {@link TProcessDefinitions} and 
	 * the final result is stored as {@link TProcessDefinitions} (list of {@link TProcessDefinition}) that are 
	 * validated against the satisfying {@link TContexts} or {@link TIntentions} respectively.
	 * @author Debasis Kar
	 * @param TProcessDefinitions, TTaskCESDefinition
	 * @return TProcessDefinitions 
	 */
	public TProcessDefinitions eliminate(TProcessDefinitions processDefinitions, TTaskCESDefinition cesDefinition);

}
