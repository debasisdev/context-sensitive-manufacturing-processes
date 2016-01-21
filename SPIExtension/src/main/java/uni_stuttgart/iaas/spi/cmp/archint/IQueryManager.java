package uni_stuttgart.iaas.spi.cmp.archint;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;

/**
 * A Generic Interface for Query Manager Module.
 * @author Debasis Kar
 */

public interface IQueryManager {
	/**
	 * Any Custom Query Manager has to implement the following method that will take TTaskCESDefinition
	 * (a set of required contexts for a domain resides in this definiton) as its input parameter. 
	 * The implementation must look for the repository where the required contexts can be found (independent 
	 * of this code)such that they can be fetched and serialized in to an XML file named ContextData.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition 
	 * @return void
	 */
	public TContexts queryRawContextData(TTaskCESDefinition cesDefinition);
	
}
