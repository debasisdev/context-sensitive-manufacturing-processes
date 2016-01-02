package unistuttgart.iaas.spi.cmprocess.arch;

import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TTaskCESDefiniton;

/**
 * An Abstract Class for Process Selector (CES Executor) that implements ICESExecutor interface.
 * @author Debasis Kar
 */
public abstract class ACESExecutor implements ICESExecutor{
	protected TIntention intention;
	protected TContexts contexts;
	
	public ACESExecutor(){
		this.intention = null;
		this.contexts = null;
	}
	
	/**
	 * It will fetch the Intentions (Main and Sub) attached with CES Definition for further processing.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TIntention 
	 */
	public TIntention prepareIntention(TTaskCESDefiniton cesDefinition){
		return this.intention = cesDefinition.getIntention();
	}
	
	/**
	 * It will fetch the required contexts to be looked for from the Sensor Networks for further processing.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TContexts 
	 */
	public TContexts prepareContext(TTaskCESDefiniton cesDefinition) {
		return this.contexts = cesDefinition.getRequiredContexts();
	}
	
	/**
	 * Implement your custom Query Manager here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public abstract void runQueryManager();
	
	/**
	 * Implement your custom Context Analyzer here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public abstract void runContextAnalyzer();	
	
	/**
	 * Implement your custom Intention Manager here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public abstract void runIntentionAnalyzer();
	
	/**
	 * Implement your custom Process Optimizer here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public abstract void runProcessOptimizer();
	
	/**
	 * Implement your custom Process Dispatcher here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public abstract void runProcessDispatcher();
	
	/**
	 * Implement your custom Deployment Manager here.
	 * @author Debasis Kar
	 * @param void
	 * @return void 
	 */
	public abstract void runDeploymentManager();
}
