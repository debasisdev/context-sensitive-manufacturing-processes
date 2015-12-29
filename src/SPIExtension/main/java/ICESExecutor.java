package unistuttgart.iaas.spi.cmprocess.arch;

import unistuttgart.iaas.spi.cmprocess.tcmp.TContexts;
import unistuttgart.iaas.spi.cmprocess.tcmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.tcmp.TTaskCESDefinition;

/**
 * A Generic Interface for Process Selector (CES Executor).
 * @author Debasis Kar
 */
public interface ICESExecutor {
	/**
	 * Any Custom Implementor must fetch the Intentions (Main and Sub) attached with CES Definition 
	 * for further processing.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TIntention 
	 */
	public TIntention prepareIntention(TTaskCESDefinition cesDefinition);
	
	/**
	 * Any Custom Implementor fetch the required contexts to be looked for from the Sensor Networks 
	 * for further processing.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TContexts 
	 */
	public TContexts prepareContext(TTaskCESDefinition cesDefinition);
	
	/**
	 * Implement your custom Query Manager here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public void runQueryManager();
	
	/**
	 * Implement your custom Context Analyzer here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public void runContextAnalyzer();	
	
	/**
	 * Implement your custom Intention Manager here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public void runIntentionAnalyzer();
	
	/**
	 * Implement your custom Process Optimizer here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public void runProcessOptimizer();
	
	/**
	 * Implement your custom Process Dispatcher here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public void runProcessDispatcher();
	
	/**
	 * Implement your custom Deployment Manager here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public void runDeploymentManager();
}
