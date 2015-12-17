package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.Set;
import java.util.logging.Logger;

import unistuttgart.iaas.spi.cmprocess.tcmp.TTaskCESDefinition;

public class CESExecutor extends ACESExecutor {
	private QueryManager queryManager;
	private ContextAnalyzer contextAnalyzer;
	private IntentionAnalyzer intentionAnalyzer;
	private ProcessOptimizer processOptimizer;
	private Set<String> outputOfContextAnalyzer;
	private Set<String> outputOfIntentionAnalyzer;
	private boolean contextAvailable;
	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public CESExecutor(){
		this.queryManager = null;
		this.contextAnalyzer = null;
		this.intentionAnalyzer = null;
		this.intention = null;
		this.contexts = null;
		this.contextAvailable = false;
	}
	
	public CESExecutor(TTaskCESDefinition cesDefinition){
		this.prepareIntention(cesDefinition);
		this.prepareContext(cesDefinition);
		this.runQueryManager();
		if(this.contextAvailable){
			this.runContextAnalyzer();
			this.runIntentionAnalyzer();
			log.info("Processes To Be Sent to ProcessOptimizer: " + this.outputOfIntentionAnalyzer);
			this.runProcessOptimizer();
		}
		else{
			log.warning("Context Not Available!");
		}
	}
	
	public void runQueryManager(){
		this.queryManager = new QueryManager(this.contexts,this.intention);
		this.contextAvailable = this.queryManager.isContextAvailable();
	}

	public void runContextAnalyzer(){
		this.contextAnalyzer = new ContextAnalyzer();
		this.outputOfContextAnalyzer = this.contextAnalyzer.getProcessListOfContextAnalyzer(
											this.contextAnalyzer.getFinalContextAnalysisTable());
	}
	
	public void runIntentionAnalyzer(){
		this.intentionAnalyzer = new IntentionAnalyzer(this.outputOfContextAnalyzer,
														this.queryManager.getIntention());
		this.outputOfIntentionAnalyzer = this.intentionAnalyzer.getProcessListOfIntentionAnalyzer(this.outputOfContextAnalyzer);
	}

	public void runProcessOptimizer() {
		this.processOptimizer = new ProcessOptimizer(this.outputOfIntentionAnalyzer,
														this.contextAvailable);
	}

	public void runProcessDispatcher() {
	
	}

	public void runDeploymentManager() {

	}
}
