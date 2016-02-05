package uni_stuttgart.iaas.spi.cmp.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntentions;
import uni_stuttgart.iaas.spi.cmp.realizations.CESExecutor;
import uni_stuttgart.iaas.spi.cmp.realizations.ContextAnalyzer;
import uni_stuttgart.iaas.spi.cmp.realizations.IntentionAnalyzer;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessDispatcher;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessOptimizer;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessSelector;
import uni_stuttgart.iaas.spi.cmp.realizations.QueryManager;
import uni_stuttgart.iaas.spi.cmp.utils.CESConfigurations;

public class CESUnitTest {
	
	private TTaskCESDefinition cesDefinition;
	private TContexts queryManagerOutput;
	private TProcessDefinitions contextAnalyzerOutput;
	private TProcessDefinitions intentionAnalyzerOutput;
	private TProcessDefinition processSelectorOutput;
	private boolean processOptimizerOutput;
	private TDataList processDispatcherOutput;
	private boolean cesExecutorOutput;
	
	public CESUnitTest() {
		this.cesDefinition = null;
		this.queryManagerOutput = null;
		this.contextAnalyzerOutput = null;
		this.intentionAnalyzerOutput = null;
		this.processSelectorOutput = null;
		this.processDispatcherOutput = null;
		this.processOptimizerOutput = false;
	}
	
	@Before 
	public void setUpData(){
		//Prepare Intention
		TIntention intention = new TIntention();
		intention.setName("SealAndSortPackets");
		TSubIntention subIntenion = new TSubIntention();
		subIntenion.setName("highThroughput");
		TSubIntentions subIntentionList = new TSubIntentions();
		subIntentionList.setSubIntentionRelations(CESConfigurations.SELECTION_WEIGHT_NAMESPACE);
		subIntentionList.getSubIntention().add(subIntenion);
		intention.getSubIntentions().add(subIntentionList);		
		//Prepare Data I/O
		TDataList inputDataList = new TDataList();
		TData inputData = new TData();
		inputData.setName("machinist");
		inputData.setValue("John");
		inputDataList.getDataList().add(inputData);
		TData outputData = new TData();
		outputData.setName("taskOutput");
		outputData.setValue("");
		TDataList outputDataList = new TDataList();
		outputDataList.getDataList().add(outputData);
		//Prepare Context
		TContext cona = new TContext();
		cona.setName("shockDetectorStatus");
		TContext conb = new TContext();
		conb.setName("unitsOrdered");
		TContext conc = new TContext();
		conc.setName("availableWorkers");
		TContext cond = new TContext();
		cond.setName("infraredSensorStatus");
		TContexts requiredContexts = new TContexts();
		requiredContexts.getContext().add(cona);
		requiredContexts.getContext().add(conb);
		requiredContexts.getContext().add(conc);
		requiredContexts.getContext().add(cond);
		//Prepare CES Task
		ObjectFactory ob = new ObjectFactory();
		TTaskCESDefinition cesDefinition = ob.createTTaskCESDefinition();
		cesDefinition.setName("Packaging");
		cesDefinition.setIsCommandAction(true);
		cesDefinition.setIsEventDriven(false);
		cesDefinition.setTargetNamespace(CESConfigurations.CMP_NAMESPACE);
		cesDefinition.setOptimizationRequired(true);
		cesDefinition.setDomainKnowHowRepository("D:\\MyWorkThesis\\SPIExtension\\src\\main\\resources\\dataRepository");
		cesDefinition.setDomainKnowHowRepositoryType("xml");
		cesDefinition.setIntention(intention);
		cesDefinition.setInputData(inputDataList);
		cesDefinition.setOutputVariable(outputDataList);
		cesDefinition.setRequiredContexts(requiredContexts);
		this.cesDefinition = cesDefinition;
	}
	
	@Test
	public void testQueryManager(){
		QueryManager queryManager = new QueryManager(this.cesDefinition);
		this.queryManagerOutput = queryManager.queryRawContextData(this.cesDefinition);
		assertNotNull("Query Manager Output should not be 'null'.", this.queryManagerOutput);
	}
	
	@Test
	public void testContextAnalyzer(){
		this.testQueryManager();
		ContextAnalyzer contextAnalyzer = new ContextAnalyzer(this.cesDefinition);
		contextAnalyzer.setContextSet(this.queryManagerOutput);
		TProcessDefinitions processSet = contextAnalyzer.getProcessRepository(this.cesDefinition);
		this.contextAnalyzerOutput = contextAnalyzer.eliminate(processSet, this.cesDefinition);
		int countOfPassedProcesses = this.contextAnalyzerOutput.getProcessDefinition().size();
		assertEquals("2 Process Definitions should Satisfy the Criteria.", 2, countOfPassedProcesses);
	}
	
	@Test
	public void testIntentionAnalyzer(){
		IntentionAnalyzer intentionAnalyzer = new IntentionAnalyzer(this.cesDefinition);
		TProcessDefinitions processSet = intentionAnalyzer.getProcessRepository(this.cesDefinition);
		this.intentionAnalyzerOutput = intentionAnalyzer.eliminate(processSet, this.cesDefinition);
		int countOfPassedProcesses = this.intentionAnalyzerOutput.getProcessDefinition().size();
		assertEquals("3 Process Definitions should Satisfy the Criteria.", 3, countOfPassedProcesses);
	}
	
	@Test
	public void testProcessSelector(){
		this.testContextAnalyzer();
		ProcessSelector processSelector = new ProcessSelector(this.cesDefinition);
		this.processSelectorOutput = processSelector.selectProcess(this.contextAnalyzerOutput, this.cesDefinition);
		assertEquals("Process SemiAutomatedRepairPacking should be Selected.", 
											"SemiAutomatedRepairPacking", this.processSelectorOutput.getName());
	}
	
	@Test
	public void testProcessOptimizer(){
		this.testProcessSelector();
		ProcessOptimizer processOptimizer = new ProcessOptimizer(this.cesDefinition);
		this.processOptimizerOutput = processOptimizer.optimizeProcess(this.processSelectorOutput);
		assertTrue("Optimization should Complete Successfully.", this.processOptimizerOutput);
	}
	
	@Test
	public void testProcessDispatcher(){
		this.testProcessSelector();
		ProcessDispatcher processDispatcher = new ProcessDispatcher(this.cesDefinition);
		this.processDispatcherOutput = processDispatcher.deployProcess(this.processSelectorOutput);
		assertEquals("Process Output should Contain 'done'.", "done", 
												this.processDispatcherOutput.getDataList().get(0).getValue());
	}
	
	@Test
	public void testCESExecutor(){
		CESExecutor cesExecutor = new CESExecutor(this.cesDefinition);
		this.cesExecutorOutput = cesExecutor.isSuccess();
		assertTrue("CES Executor should Run Successfully without Exceptions.", this.cesExecutorOutput);
	}
}
