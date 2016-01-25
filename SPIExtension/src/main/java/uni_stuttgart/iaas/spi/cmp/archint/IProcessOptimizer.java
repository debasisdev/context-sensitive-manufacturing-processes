package uni_stuttgart.iaas.spi.cmp.archint;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
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
	 * the optimization model and will return any output if required.
	 * @author Debasis Kar
	 * @param TProcessDefinition
	 * @return TDataList
	 */
	public TDataList optimizeProcess(TProcessDefinition processContent);
}
