package uni_stuttgart.iaas.spi.cmp.archint;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * A Generic Interface for Process Optimizer Module.
 * @author Debasis Kar
 */

public interface IProcessOptimizer {
	/**
	 * Any Custom Process Optimizer has to implement the following method that will take
	 * TProcessContent defined inside a process definition as its input parameter. The implementation 
	 * must look for any optimization model is defined for the process. It will deploy and execute 
	 * the optimization model and will return true if successful else false.
	 * @author Debasis Kar
	 * @param TProcessDefinition
	 * @return boolean
	 */
	public boolean optimizeProcess(TProcessDefinition processContent);
}
