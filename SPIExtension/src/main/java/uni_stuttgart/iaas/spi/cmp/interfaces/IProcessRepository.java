package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

/**
 * A generic interface for process repository access.
 * @author Debasis Kar
 */

public interface IProcessRepository {

	/**
	 * This method must be implemented in order to extract the process repository data defined inside
	 * {@link TTaskCESDefinition} and return {@link TProcessDefinitions} for filtering.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TProcessDefinitions 
	 */
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition);

	
}
