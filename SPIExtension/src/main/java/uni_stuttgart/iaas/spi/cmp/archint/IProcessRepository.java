package uni_stuttgart.iaas.spi.cmp.archint;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

/**
 * A Generic Interface for Process Repository Access.
 * @author Debasis Kar
 */

public interface IProcessRepository {//Make it to JAXB Generic Classes

	/**
	 * This method must be implemented in order to extract the Process Repository detail from TTaskCESDefinition.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TProcessDefinitions 
	 */
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition);

	
}
