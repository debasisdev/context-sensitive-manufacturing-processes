package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessDispatcher;

/**
 * A generic interface for {@link ProcessDispatcher} module will invoke process engines.
 * @author Debasis Kar
 */

public interface IProcessEngine {
	
	/**
	 * Any custom deployment manager has to implement the following method that will take the 
	 * {@link TProcessDefinition} received from {@link ProcessOptimizer} and will deploy to an underlying
	 * process engine, e.g., Activiti BPMN, Apache ODE BPEL, etc. This interface is intended to make the 
	 * deployment of the CES pluggable and users can write their own deployment code as per their own engine.
	 * @author Debasis Kar
	 * @param processDefinition
	 * @return TDataList
	 */
	public TDataList deployMainProcess(TProcessDefinition processDefinition);
	
	/**
	 * Any custom deployment manager has to implement the following method that will search a Complementary
	 * Process Definition ({@link TProcessDefinition}) of the main process and will deploy to an underlying process 
	 * engine, e.g., Activiti BPMN, Apache ODE BPEL, etc. The output of the complementary process is not needed for
	 * any practical purposes, so it only returns the success status in a boolean output.
	 * @author Debasis Kar
	 * @param processDefinition
	 * @return boolean
	 */
	public boolean deployComplementaryProcess(TProcessDefinition processDefinition);
}
