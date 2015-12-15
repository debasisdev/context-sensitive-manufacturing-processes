package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import unistuttgart.iaas.spi.cmprocess.cmp.TContext;
import unistuttgart.iaas.spi.cmprocess.cmp.TContexts;
import unistuttgart.iaas.spi.cmprocess.cmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.cmp.TIntentions;

public class CESExecutor implements ICESExecutor {
	private QueryManager queryManager;
	private ContextAnalyzer contextAnalyzer;
	private IntentionAnalyzer intentionAnalyzer;
	private ProcessOptimizer processOptimizer;
	private TIntention intention;
	private TContexts contexts;
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
	
	public CESExecutor(String intentionQuery, String contextQuery){
		this.prepareIntention(intentionQuery);
		this.prepareContext(contextQuery);
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
	
	public void prepareIntention(String intentionQuery){
		this.intention = new TIntention();
		List<TIntention> subIntentionList = new ArrayList<TIntention>();
		TIntentions subIntentions = new TIntentions();
		if(intentionQuery.length()>0){
			String[] intentions = intentionQuery.trim().split(",");
			this.intention.setDocumentation(ContextConfig.CONTEXT_PACKAGE_DOC);
			this.intention.setTargetNamespace(ContextConfig.CONTEXT_NAMESPACE);
			int loopCounter = 0;
			for(String intention : intentions){
				if(loopCounter == 0){
					this.intention.setName(intention);
				}
				else{
					TIntention subIntention = new TIntention();
					subIntention.setName(intention.trim());
					subIntention.setTargetNamespace(ContextConfig.CONTEXT_NAMESPACE);
					subIntentionList.add(subIntention);
				}
				loopCounter++;	
			}
			for(TIntention intention : subIntentionList){
				if(intention.getName().equals(ContextConfig.IN_FIELD_AUTOMATION))
					intention.setDocumentation(ContextConfig.HIGH_AUTOMATION_DOC);
				if(intention.getName().equals(ContextConfig.IN_FIELD_HR_UTILIZATION))
					intention.setDocumentation(ContextConfig.HIGH_HR_UTILIZATION_DOC);
				if(intention.getName().equals(ContextConfig.IN_FIELD_THROUGHPUT))
					intention.setDocumentation(ContextConfig.HIGH_THROUGHPUT_DOC);
				if(intention.getName().equals(ContextConfig.IN_FIELD_MAINTENANCE))
					intention.setDocumentation(ContextConfig.LOW_MAINTENANCE_DOC);
				subIntentions.getIntention().add(intention);
			}
			this.intention.setSubIntentions(subIntentions);
		}
	}
	
	public void prepareContext(String contextQuery){
		this.contexts = new TContexts();
		if(contextQuery.length()>0){
			String[] contextsRequired = contextQuery.trim().split(",");
			for(String contextVar : contextsRequired){
				TContext context = new TContext();
				context.setName(contextVar.trim());
				context.setTargetNamespace(ContextConfig.CONTEXT_NAMESPACE);
				if(contextVar.trim().equals(ContextConfig.GPSLOCATION_CONTEXT_NAME)){
					context.setDocumentation(ContextConfig.GPSLOCATION_DOC);
				}
				if(contextVar.trim().equals(ContextConfig.SHOCKDETECTORSENSOR_CONTEXT_NAME)){
					context.setDocumentation(ContextConfig.SHOCKDETECTORSENSOR_DOC);
				}
				if(contextVar.trim().equals(ContextConfig.INFRAREDSENSOR_CONTEXT_NAME)){
					context.setDocumentation(ContextConfig.INFRAREDSENSOR_DOC);
				}
				if(contextVar.trim().equals(ContextConfig.UNITSORDERED_CONTEXT_NAME)){
					context.setDocumentation(ContextConfig.UNITSORDERED_DOC);
				}
				this.contexts.getContext().add(context);
			}
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
