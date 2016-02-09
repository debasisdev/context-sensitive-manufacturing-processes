package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessDispatcher;

/**
 * A generic interface for implementations of realization processes.
 * @author Debasis Kar
 */

public interface IExecutionManager {
	
	/**
	 * Any custom implementation a process model must be defined inside this method
	 * such that this can be called from the {@link ProcessDispatcher} or {@link ProcessOptimizer} 
	 * easily without any ambiguity. This method takes the input and output variables of the process
	 * along with the process name and process model location. Finally it writes the output (if any) 
	 * to the specified output variable of {@link TDataList} in an {@link TTaskCESDefinition} envelope.
	 * @author Debasis Kar
	 * @param String, String, TDataList, TDataList
	 * @return TDataList
	 */
	public TDataList startProcess(String filePath, String processName, TDataList input, TDataList outputHolder);
}
