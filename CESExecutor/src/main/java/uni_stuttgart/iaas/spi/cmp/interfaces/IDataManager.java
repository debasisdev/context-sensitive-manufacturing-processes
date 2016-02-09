package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;

/**
 * A generic interface for different DBMS clients to extract data from Middle-wares.
 * @author Debasis Kar
 */

public interface IDataManager {

	/**
	 * Any custom implementation of DBMS data fetcher must implement this 
	 * method to get data from the Middle-ware that takes {@link List} of {@link TContext}
	 * as its input parameter.
	 * @author Debasis Kar
	 * @param List<TContext>
	 * @return TContexts
	 */
	public TContexts getDataFromDatabase(List<TContext> contextList);
	
	/**This is the getter method to know whether there is any context data 
	 * available or not after the fetching operation.
	 * @author Debasis Kar
	 * @param void
	 * @return boolean
	 * */
	public boolean isContextAvailable();
}
