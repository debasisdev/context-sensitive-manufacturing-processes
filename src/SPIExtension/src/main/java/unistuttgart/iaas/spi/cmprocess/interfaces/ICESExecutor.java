package unistuttgart.iaas.spi.cmprocess.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * A Generic Interface for Process Selector (CES Executor).
 * @author Debasis Kar
 */
public interface ICESExecutor {
	/**
	 * Implement your custom Query Manager here.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return void 
	 */
	public void runQueryManager(TTaskCESDefinition cesDefinition);
	
	/**
	 * Implement your custom Context Analyzer here.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return List<TProcessDefinition> 
	 */
	public List<TProcessDefinition> runContextAnalyzer(TTaskCESDefinition cesDefinition);	
	
	/**
	 * Implement your custom Intention Manager here.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return List<TProcessDefinition> 
	 */
	public List<TProcessDefinition> runIntentionAnalyzer(TTaskCESDefinition cesDefinition);
	
	/**
	 * Implement your custom Process Optimizer here.
	 * @author Debasis Kar
	 * @param List<TProcessDefinition>, List<TProcessDefinition>, TTaskCESDefinition
	 * @return TProcessDefinition 
	 */
	public TProcessDefinition runProcessSelector(List<TProcessDefinition> outputOfContextAnalyzer, List<TProcessDefinition> outputOfIntentionAnalyzer, TTaskCESDefinition cesDefinition);
	
	/**
	 * Implement your custom Process Dispatcher here.
	 * @author Debasis Kar
	 * @param TProcessDefinition
	 * @return void 
	 */
	public void runProcessOptimizer(TProcessDefinition processDefinition);
	
	/**
	 * Implement your custom Deployment Manager-cum-Process Diapatcher here.
	 * @author Debasis Kar
	 * @param TProcessDefinition, TTaskCESDefinition
	 * @return void 
	 */
	public void runProcessDispatcher(TProcessDefinition processDefinition, TTaskCESDefinition cesDefinition);

}
