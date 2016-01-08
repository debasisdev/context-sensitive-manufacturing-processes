package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.Set;
import java.util.logging.Logger;

import de.uni_stuttgart.iaas.ipsm.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TTaskCESDefinition;

public class CESExecutor extends ACESExecutor {
	private QueryManager queryManager;
	private ContextAnalyzer contextAnalyzer;
	private IntentionAnalyzer intentionAnalyzer;
	private ProcessOptimizer processOptimizer;
	private ProcessDispatcher processDispatcher;
	private DeploymentManager deployer;
	private Set<String> outputOfContextAnalyzer;
	private Set<String> outputOfIntentionAnalyzer;
	private String finalSelectedProcess;
	private String result;

	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public CESExecutor(){
		this.queryManager = null;
		this.contextAnalyzer = null;
		this.intentionAnalyzer = null;
		this.processOptimizer = null;
		this.processDispatcher = null;
		this.deployer = null;
		this.outputOfContextAnalyzer = null;
		this.outputOfIntentionAnalyzer = null;
		this.finalSelectedProcess = null;
		this.result = null;
		this.inputData = null;
		this.outputData = null;
	}
	
	public CESExecutor(TTaskCESDefinition cesDefinition){
		this.intention = this.prepareIntention(cesDefinition);
		this.contexts = this.prepareContext(cesDefinition);
		this.optimizationNeeded = this.isOptimizationNeeded(cesDefinition);
		this.inputData = this.prepareInputData(cesDefinition);
		this.outputData = this.prepareOutputData(cesDefinition);
		this.runQueryManager();
		if(this.contextAvailable){
			this.runContextAnalyzer();
		}
		else{
			log.warning("Context Not Available!");
		}
		this.runIntentionAnalyzer();
		this.runProcessDispatcher();
		log.info("Processes To Be Sent to Process Optimizer: " + this.finalSelectedProcess);
		this.result = this.finalSelectedProcess;
		if(this.optimizationNeeded){
			this.runProcessOptimizer();
		}
		this.runDeploymentManager();
	}
	
	@Override
	public void runQueryManager(){
		this.queryManager = new QueryManager(this.contexts);
		this.contextAvailable = this.queryManager.isContextAvailable();
	}

	@Override
	public void runContextAnalyzer(){
		this.contextAnalyzer = new ContextAnalyzer();
		this.outputOfContextAnalyzer = this.contextAnalyzer.getProcessListOfContextAnalyzer(
											this.contextAnalyzer.getFinalContextAnalysisTable());
	}
	
	@Override
	public void runIntentionAnalyzer(){
		this.intentionAnalyzer = new IntentionAnalyzer(this.outputOfContextAnalyzer, this.intention);
		this.outputOfIntentionAnalyzer = this.intentionAnalyzer.getProcessListOfIntentionAnalyzer
														(this.outputOfContextAnalyzer);
	}

	@Override
	public void runProcessDispatcher() {
		this.processDispatcher = new ProcessDispatcher(this.outputOfIntentionAnalyzer);
		this.finalSelectedProcess = this.processDispatcher.getDispatchedProcessName();
	}
	
	@Override
	public void runProcessOptimizer() {
		this.processOptimizer = new ProcessOptimizer(this.finalSelectedProcess);
	}

	@Override
	public void runDeploymentManager() {
		this.deployer = new DeploymentManager(this.inputData, this.outputData, this.finalSelectedProcess);
	}

	public String getResult() {
		return this.result;
	}

}
