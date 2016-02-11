package uni_stuttgart.iaas.spi.cmp.realizations;

import org.apache.camel.builder.RouteBuilder;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/**
 * A generic route-builder that contains the whole routing logic of the application.
 * @author Debasis Kar
 */

public class CamelRoutingCore extends RouteBuilder {
	
	/**Variable to store {@link TTaskCESDefinition} 
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Default constructor of {@link CamelRoutingCore}
	 * @author Debasis Kar
	 * */
	public CamelRoutingCore() {
		this.cesDefinition = null;
	}
	
	/**Parameterized constructor of {@link CamelRoutingCore}
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * */
	public CamelRoutingCore(TTaskCESDefinition cesDefinition){
		this.cesDefinition = cesDefinition;
	}

	@Override
	public void configure() throws Exception {
		from(CESExecutorConfig.RABBIT_MAIN_QUEUE)
        .choice()
        	//Activate Query Manager
        	.when(header(CESExecutorConfig.RABBIT_STATUS).isEqualTo(CESExecutorConfig.RABBIT_MSG_CESACTIVATE))
        		.process(new QueryManager(cesDefinition))
    				.to(CESExecutorConfig.RABBIT_CONTENT_ROUTER)
    		//Activate Context Analyzer
    		.when(header(CESExecutorConfig.RABBIT_STATUS).isEqualTo(CESExecutorConfig.RABBIT_MSG_QUERYMANAGER))
        		.process(new ContextAnalyzer(cesDefinition))
    				.to(CESExecutorConfig.RABBIT_CONTENT_ROUTER)
    		//Activate Intention Analyzer
    		.when(header(CESExecutorConfig.RABBIT_STATUS).isEqualTo(CESExecutorConfig.RABBIT_MSG_CONTEXTANALYZER))
        		.process(new IntentionAnalyzer(cesDefinition))
    				.to(CESExecutorConfig.RABBIT_CONTENT_ROUTER)
    		//Activate Process Selector
    		.when(header(CESExecutorConfig.RABBIT_STATUS).isEqualTo(CESExecutorConfig.RABBIT_MSG_INTENTIONANALYZER))
        		.process(new ProcessSelector(cesDefinition))
    				.to(CESExecutorConfig.RABBIT_CONTENT_ROUTER)
    		//Activate Process Optimizer
    		.when(header(CESExecutorConfig.RABBIT_STATUS).isEqualTo(CESExecutorConfig.RABBIT_MSG_PROCESSSELECTOR))
        		.process(new ProcessOptimizer(cesDefinition))
    				.to(CESExecutorConfig.RABBIT_CONTENT_ROUTER)
    		//Activate Process Dispatcher
    		.when(header(CESExecutorConfig.RABBIT_STATUS).isEqualTo(CESExecutorConfig.RABBIT_MSG_PROCESSOPTIMIZER))
        		.process(new ProcessDispatcher(cesDefinition))
    				.to(CESExecutorConfig.RABBIT_CONTENT_ROUTER)
    		//Route Output to the Result queue
    		.when(header(CESExecutorConfig.RABBIT_STATUS).isEqualTo(CESExecutorConfig.RABBIT_MSG_PROCESSDISPATCHER))
    				.to(CESExecutorConfig.RABBIT_CONTENT_ROUTER)
    		//Route Failure to Standard output
    		.otherwise()
    				.to(CESExecutorConfig.RABBIT_CONSOLE_OUT);      
	}

}
