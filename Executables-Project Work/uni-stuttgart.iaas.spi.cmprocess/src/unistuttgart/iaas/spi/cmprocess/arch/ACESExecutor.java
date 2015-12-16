package unistuttgart.iaas.spi.cmprocess.arch;

import unistuttgart.iaas.spi.cmprocess.cmp.TContexts;
import unistuttgart.iaas.spi.cmprocess.cmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.cmp.TTaskCESDefiniton;

public abstract class ACESExecutor implements ICESExecutor{
	protected TIntention intention;
	protected TContexts contexts;
	
	public ACESExecutor(){
		this.intention = null;
		this.contexts = null;
	}
	
	/*This will fetch the Intentions (Main and Sub) attached with CES Definition for further processing.*/
	public void prepareIntention(TTaskCESDefiniton cesDefinition){
		this.intention = cesDefinition.getIntention();
	}
	
	/*This will fetch the Required Contexts to be looked for from Sensor Networks for further processing.*/
	public void prepareContext(TTaskCESDefiniton cesDefinition) {
		this.contexts = cesDefinition.getRequiredContext();
	}
	
	/*Implement your Query Manager here.*/
	public abstract void runQueryManager();
	
	/*Implement your Context Analyzer here.*/
	public abstract void runContextAnalyzer();	
	
	/*Implement your Intention Analyzer here.*/
	public abstract void runIntentionAnalyzer();
	
	/*Implement your Process Optimizer here.*/
	public abstract void runProcessOptimizer();
	
	/*Implement your Process Dispatcher here.*/
	public abstract void runProcessDispatcher();
	
	/*Implement your Deployment Manager here.*/
	public abstract void runDeploymentManager();
}
