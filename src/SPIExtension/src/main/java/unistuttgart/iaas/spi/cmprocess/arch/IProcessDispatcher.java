package unistuttgart.iaas.spi.cmprocess.arch;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

public interface IProcessDispatcher {
	/**
	 * Any Custom Process Dispatcher has to implement the following method that will take
	 * a Process Definition List as its input and will remove any duplicates (if found) and
	 * if multiple process models are fir for the scenario, it'll poll and proceed with the
	 * process having highest priority or weight and will return the Process Name or ID for the
	 * reference of Process Optimizer.
	 * @author Debasis Kar
	 * @param TProcessDefinitions
	 * @return String
	 */
	public String dispatchProcess(TProcessDefinitions processSet);
}
