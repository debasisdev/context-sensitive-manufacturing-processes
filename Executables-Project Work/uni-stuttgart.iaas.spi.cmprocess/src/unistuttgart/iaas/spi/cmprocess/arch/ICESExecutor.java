package unistuttgart.iaas.spi.cmprocess.arch;

public interface ICESExecutor {
	public void prepareIntention(String intentionQuery);
	public void prepareContext(String contextQuery);
	public void runQueryManager();
	public void runContextAnalyzer();	
	public void runIntentionAnalyzer();
	public void runProcessOptimizer();
	public void runProcessDispatcher();
	public void runDeploymentManager();
}
