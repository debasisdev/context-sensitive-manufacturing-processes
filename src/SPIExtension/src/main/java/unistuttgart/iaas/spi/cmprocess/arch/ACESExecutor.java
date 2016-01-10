package unistuttgart.iaas.spi.cmprocess.arch;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * An Abstract Class for Process Selector (CES Executor) that implements ICESExecutor interface.
 * @author Debasis Kar
 */
public abstract class ACESExecutor implements ICESExecutor{
	protected TIntention intention;
	protected TContexts contexts;
	protected TDataList inputData;
	protected TDataList outputData;
	protected TProcessDefinition processToBeRealized;
	protected String processRepositoryUri;
	protected boolean contextAvailable;
	protected boolean optimizationNeeded;
	
	public ACESExecutor(){
		this.intention = null;
		this.contexts = null;
		this.outputData = null;
		this.inputData = null;
		this.processToBeRealized = null;
		this.processRepositoryUri = null;
		this.contextAvailable = false;
		this.optimizationNeeded = false;
	}
	
	/**
	 * It will fetch the Intentions (Main and Sub) attached with CES Definition for further processing.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TIntention 
	 */
	@Override
	public TIntention prepareIntention(TTaskCESDefinition cesDefinition){
		return this.intention = cesDefinition.getIntention();
	}
	
	/**
	 * It will fetch the required contexts to be looked for from the Sensor Networks for further processing.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TContexts 
	 */
	@Override
	public TContexts prepareContext(TTaskCESDefinition cesDefinition) {
		return this.contexts = cesDefinition.getRequiredContexts();
	}
	
	/**
	 * Any Custom Implementor should initialize the Input data for the task..
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TDataList
	 */
	@Override
	public TDataList prepareInputData(TTaskCESDefinition cesDefinition) {
		return this.inputData = cesDefinition.getInputData();
	}
	
	/**
	 * Any Custom Implementor should initialize the Output data for the task..
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TDataList
	 */
	@Override
	public TDataList prepareOutputData(TTaskCESDefinition cesDefinition) {
		return this.outputData = cesDefinition.getOutputVariable();
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
	
	/**
	 * Any Custom Implementor must fetch the Process Definition Repository as specified by the business modeler.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return TProcessDefinitions
	 */
	@Override
	public String prepareRepositoryUri(TTaskCESDefinition cesDefinition){
		return this.processRepositoryUri = cesDefinition.getProcessRepository();
	}
	
	/**
	 * Implement your custom code here to check the need of Optimization.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return boolean 
	 */
	@Override
	public boolean isOptimizationNeeded(TTaskCESDefinition cesDefinition){
		return this.optimizationNeeded = cesDefinition.isOptimizationRequired();
	}
}
