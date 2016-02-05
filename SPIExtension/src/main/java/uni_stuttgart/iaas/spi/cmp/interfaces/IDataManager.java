package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;

/**
 * A Generic Interface for Different DBMS Clients to Extract data from.
 * @author Debasis Kar
 */

public interface IDataManager {

	/**
	 * Any Custom Implementation of DBMS Data Fetcher must implement this method to get data from the database.
	 * @author Debasis Kar
	 * @param List<TContext>
	 * @return TContexts
	 */
	public TContexts getDataFromDatabase(List<TContext> contextList);
	
	/**This is the getter method to know whether there is any context data available or not.
	 * @author Debasis Kar
	 * @param void
	 * @return boolean
	 * */
	public boolean isContextAvailable();
}
