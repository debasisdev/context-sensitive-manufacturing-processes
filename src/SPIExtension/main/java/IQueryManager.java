package unistuttgart.iaas.spi.cmprocess.arch;

import unistuttgart.iaas.spi.cmprocess.tcmp.TContexts;

/**
 * A Generic Interface for Query Manager Module.
 * @author Debasis Kar
 */
public interface IQueryManager {
	/**
	 * Any Custom Query Manager has to implement the following method that will take
	 * TContexts (a set of required contexts for a domain) as its input parameter. 
	 * The implementation must look for the repository where the required contexts can be 
	 * found (independent of this code)such that they can be fetched and serialized in to 
	 * an XML file named ContextData.
	 * @author Debasis Kar
	 * @param TContexts 
	 * @return void
	 */
	public void queryRawContextData(TContexts requiredContexts);
}
