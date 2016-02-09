package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessOptimizer;

/**
 * A generic interface for {@link ProcessOptimizer} Module.
 * @author Debasis Kar
 */

public interface IProcessOptimizer {
	/**
	 * Any custom {@link ProcessOptimizer} has to implement the following method that will take
	 * {@link TProcessContent} defined inside a {@link TProcessDefinition} as its input parameter. 
	 * The implementation looks for any optimization model is defined for the process. 
	 * It deploys and executes the optimization model and will return true if successful else false.
	 * @author Debasis Kar
	 * @param TProcessDefinition
	 * @return boolean
	 */
	public boolean optimizeProcess(TProcessDefinition processContent);
}
