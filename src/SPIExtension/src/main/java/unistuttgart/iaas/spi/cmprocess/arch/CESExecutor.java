package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.Set;
import java.util.logging.Logger;

import de.uni_stuttgart.iaas.ipsm.v0.TTaskCESDefiniton;

public class CESExecutor extends ACESExecutor {
	private QueryManager queryManager;
	private ContextAnalyzer contextAnalyzer;
	private IntentionAnalyzer intentionAnalyzer;
	private ProcessOptimizer processOptimizer;
	private Set<String> outputOfContextAnalyzer;
	private Set<String> outputOfIntentionAnalyzer;
	private boolean contextAvailable;
	private Set<String> result;
	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public CESExecutor(){
		this.queryManager = null;
		this.contextAnalyzer = null;
		this.intentionAnalyzer = null;
		this.intention = null;
		this.contexts = null;
		this.contextAvailable = false;
	}
	
	public CESExecutor(TTaskCESDefiniton cesDefinition){
		this.intention = this.prepareIntention(cesDefinition);
		this.contexts = this.prepareContext(cesDefinition);
		this.runQueryManager();
		if(this.contextAvailable){
			this.runContextAnalyzer();
			this.runIntentionAnalyzer();
			log.info("Processes To Be Sent to ProcessOptimizer: " + this.outputOfIntentionAnalyzer);
			this.setResult(this.outputOfContextAnalyzer);
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

	public Set<String> getResult() {
		return result;
	}

	public void setResult(Set<String> result) {
		this.result = result;
	}
}
