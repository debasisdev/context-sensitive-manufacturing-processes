package uni_stuttgart.iaas.spi.cmp.archint;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;

/**
 * A Generic Interface for Solutions of Realization Processes.
 * @author Debasis Kar
 */

public interface IRealization {
	
	/**
	 * Any Custom Implementation (Realization) of a BPMN Process Model must be defined inside this method
	 * such that this can be called from the Dispatcher or Optimizer easily without any ambiguity. This method
	 * takes the BPMN input and output variables such that it processes the input data and writes the
	 * output (if any) to the specified output variable in TTaskCESDefinition.
	 * @author Debasis Kar
	 * @param TDataList, TDataList
	 * @return TDataList
	 */
	public TDataList startProcess(TDataList input, TDataList outputHolder);
}
