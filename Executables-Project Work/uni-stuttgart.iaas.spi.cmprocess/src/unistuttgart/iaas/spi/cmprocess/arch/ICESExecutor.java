package unistuttgart.iaas.spi.cmprocess.arch;

import unistuttgart.iaas.spi.cmprocess.cmp.TTaskCESDefiniton;

public interface ICESExecutor {
	/*This will fetch the Intentions (Main and Sub) attached with CES Definition for further processing.*/
	public void prepareIntention(TTaskCESDefiniton cesDefinition);
	
	/*This will fetch the Required Contexts to be looked for from Sensor Networks for further processing.*/
	public void prepareContext(TTaskCESDefiniton cesDefinition);
	
	/*Implement your Query Manager here.*/
	public void runQueryManager();
	
	/*Implement your Context Analyzer here.*/
	public void runContextAnalyzer();	
	
	/*Implement your Intention Analyzer here.*/
	public void runIntentionAnalyzer();
	
	/*Implement your Process Optimizer here.*/
	public void runProcessOptimizer();
	
	/*Implement your Process Dispatcher here.*/
	public void runProcessDispatcher();
	
	/*Implement your Deployment Manager here.*/
	public void runDeploymentManager();
}
