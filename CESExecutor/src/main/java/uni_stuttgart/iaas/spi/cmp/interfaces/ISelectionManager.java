package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * A generic interface for a weight based module.
 * @author Debasis Kar
 */

public interface ISelectionManager {
	
	/**
	 * Any custom selection algorithm implementor has to implement the following method to 
	 * select a {@link TProcessDefinition} out of a {@link List} of {@link TProcessDefinition}. 
	 * @author Debasis Kar
	 * @param processDefinitionList
	 * @return TProcessDefinition
	 */
	public TProcessDefinition findRealizationProcess(List<TProcessDefinition> processDefinitionList);
}
