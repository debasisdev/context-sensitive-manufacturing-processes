package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import uni_stuttgart.iaas.spi.cmp.realizations.QueryManager;

/**
 * A generic interface for {@link QueryManager} module.
 * @author Debasis Kar
 */

public interface IQueryProcessor {
	
	/**
	 * Any custom {@link QueryManager} has to implement the following method that will take 
	 * {@link TTaskCESDefinition} (a set of required contexts for a domain resides in this definition) 
	 * as its input parameter. The implementation must look for the repository where the required contexts 
	 * can be found such that they can be fetched and serialized in to an XML file named ContextData 
	 * by {@link ContextAnalyzer.
	 * @author Debasis Kar
	 * @param cesDefinition
	 * @return TContexts
	 */
	public TContexts queryRawContextData(TTaskCESDefinition cesDefinition);
	
}
