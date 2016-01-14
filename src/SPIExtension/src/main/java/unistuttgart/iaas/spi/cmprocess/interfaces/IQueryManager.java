package unistuttgart.iaas.spi.cmprocess.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;

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
	public void queryRawContextData(TTaskCESDefinition cesDefinition);
	
	/**
	 * Any Custom Query Manager has to implement the following method that will tell the CESExecutor
	 * whether it was able to pull/fetch any Context Data out of the Middleware or Database whatsoever.
	 * @author Debasis Kar
	 * @param void 
	 * @return boolean
	 */
	public boolean isContextAvailable();
}
