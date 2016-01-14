package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.List;
import java.util.logging.Logger;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.interfaces.ICESExecutor;

public class CESExecutor implements ICESExecutor {
	private QueryManager queryManager;
	private ContextAnalyzer contextAnalyzer;
	private IntentionAnalyzer intentionAnalyzer;
	private ProcessSelector processSelector;
	private ProcessOptimizer processOptimizer;
	private ProcessDispatcher processDispatcher;

	protected boolean contextAvailable;
	protected boolean optimizationNeeded;
	private List<TProcessDefinition> outputOfContextAnalyzer;
	private List<TProcessDefinition> outputOfIntentionAnalyzer;
	private TProcessDefinition selectedProcessDefintion;

	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public CESExecutor(){
		this.queryManager = null;
		this.contextAnalyzer = null;
		this.intentionAnalyzer = null;
		this.processSelector = null;
		this.processOptimizer = null;
		this.processDispatcher = null;
		
		this.outputOfContextAnalyzer = null;
		this.outputOfIntentionAnalyzer = null;
		this.selectedProcessDefintion = null;
		this.contextAvailable = false;
		this.optimizationNeeded = false;
	}
	
	public CESExecutor(TTaskCESDefinition cesDefinition){
		this.optimizationNeeded = cesDefinition.isOptimizationRequired();
		this.runQueryManager(cesDefinition);
		if(this.contextAvailable){
			this.outputOfContextAnalyzer = this.runContextAnalyzer(cesDefinition);
		}
		else{
			log.warning("Context Not Available!");
		}
		this.outputOfIntentionAnalyzer = this.runIntentionAnalyzer(cesDefinition);
		this.selectedProcessDefintion = this.runProcessSelector(this.outputOfContextAnalyzer, this.outputOfIntentionAnalyzer, cesDefinition);
		if(this.optimizationNeeded){
			this.runProcessOptimizer(this.selectedProcessDefintion);
		}
		this.runProcessDispatcher(this.selectedProcessDefintion, cesDefinition);
	}
	
	@Override
	public void runQueryManager(TTaskCESDefinition cesDefinition){
		this.queryManager = new QueryManager(cesDefinition);
		this.contextAvailable = this.queryManager.isContextAvailable();
	}

	@Override
	public List<TProcessDefinition> runContextAnalyzer(TTaskCESDefinition cesDefinition){
		this.contextAnalyzer = new ContextAnalyzer(cesDefinition);
		return this.contextAnalyzer.getProcessListOfAnalyzer();
	}
	
	@Override
	public List<TProcessDefinition> runIntentionAnalyzer(TTaskCESDefinition cesDefinition){
		this.intentionAnalyzer = new IntentionAnalyzer(cesDefinition);
		return this.intentionAnalyzer.getProcessListOfAnalyzer();
	}

	@Override
	public TProcessDefinition runProcessSelector(List<TProcessDefinition> outputOfContextAnalyzer, 
						List<TProcessDefinition> outputOfIntentionAnalyzer, TTaskCESDefinition cesDefinition) {
		this.processSelector = new ProcessSelector(outputOfContextAnalyzer, outputOfIntentionAnalyzer, cesDefinition);
		return this.processSelector.getDispatchedProcess();
	}
	
	@Override
	public void runProcessOptimizer(TProcessDefinition processDefinition) {
		this.processOptimizer = new ProcessOptimizer(this.selectedProcessDefintion);
	}

	@Override
	public void runProcessDispatcher(TProcessDefinition processDefinition, TTaskCESDefinition cesDefinition){
		this.processDispatcher = new ProcessDispatcher(processDefinition, cesDefinition);
	}

}
