package uni_stuttgart.iaas.spi.cmp.testcases;

/** 
 * Copyright 2016 Debasis Kar
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TCESIntentionDefinition;
import de.uni_stuttgart.iaas.cmp.v0.TCESIntentionDefinitions;
import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcess;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcesses;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinitions;
import de.uni_stuttgart.iaas.ipsm.v0.TIntentionDefinitions;


import uni_stuttgart.iaas.spi.cmp.realizations.CESExecutor;
import uni_stuttgart.iaas.spi.cmp.realizations.ContextAnalyzer;
import uni_stuttgart.iaas.spi.cmp.realizations.IntentionAnalyzer;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessDispatcher;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessOptimizer;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessRepository;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessSelector;
import uni_stuttgart.iaas.spi.cmp.realizations.QueryManager;

public class CESUnitTest {
	
	private TTaskCESDefinition cesDefinition;
	private TContextDefinitions queryManagerOutput;
	private TRealizationProcesses contextAnalyzerOutput;
	private TRealizationProcesses intentionAnalyzerOutput;
	private TRealizationProcess processSelectorOutput;
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
		TCESIntentionDefinition   intention = new TCESIntentionDefinition();
		intention.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName("PackAndPalletize");
		TCESIntentionDefinition   subIntenion = new TCESIntentionDefinition();
		subIntenion.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName("highThroughput");
		TCESIntentionDefinitions  subIntentionList = new TCESIntentionDefinitions ();
		intention.setSelectionStrategy("http://www.uni-stuttgart.de/iaas/cmp/weight-based");
		intention.setSubIntentions(new TCESIntentionDefinitions());
		intention.getSubIntentions().getCESIntentionDefinitions().add(subIntenion);
		//Prepare Data I/O
		TDataList inputDataList = new TDataList();
		TData inputData0 = new TData();
		inputData0.setName("machinist");
		inputData0.setValue("John");
		TData inputData1 = new TData();
		inputData1.setName("orderID");
		inputData1.setValue("OD153728DE");
		inputDataList.getDataList().add(inputData0);
		inputDataList.getDataList().add(inputData1);
		TData outputData = new TData();
		outputData.setName("taskOutput");
		outputData.setValue("");
		TDataList outputDataList = new TDataList();
		outputDataList.getDataList().add(outputData);
		//Prepare Context
		TContextDefinition cona = new TContextDefinition();
		cona.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName("shockDetectorStatus");
		TContextDefinition conb = new TContextDefinition();
		conb.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName("unitsOrdered");
		TContextDefinition conc = new TContextDefinition();
		conc.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName("availableWorkers");
		TContextDefinition cond = new TContextDefinition();
		cond.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName("infraredSensorStatus");
		TContextDefinitions requiredContexts = new TContextDefinitions();
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
		cesDefinition.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v0.1/");
		cesDefinition.setOptimizationRequired(true);
		cesDefinition.setDomainKnowHowRepository("F:\\Dropbox\\GitLab Repository\\domain-know-how\\ProcessRepository.xml");
		cesDefinition.setDomainKnowHowRepositoryType("xml");
		cesDefinition.setIntention(intention);
		cesDefinition.setInputData(inputDataList);
		cesDefinition.setOutputVariable(outputDataList);
		cesDefinition.getIntention().setRequiredContexts(requiredContexts);
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
		ProcessRepository processRepository = new ProcessRepository();
		TRealizationProcesses  processSet = processRepository.getProcessRepository(this.cesDefinition);
		this.contextAnalyzerOutput = contextAnalyzer.eliminate(processSet, this.cesDefinition);
		int countOfPassedProcesses = this.contextAnalyzerOutput.getRealizationProcess().size();
		assertEquals("2 Process Definitions should Satisfy the Criteria.", 2, countOfPassedProcesses);
	}
	
	@Test
	public void testIntentionAnalyzer(){
		IntentionAnalyzer intentionAnalyzer = new IntentionAnalyzer(this.cesDefinition);
		ProcessRepository processRepository = new ProcessRepository();
		TRealizationProcesses  processSet = processRepository.getProcessRepository(this.cesDefinition);
		this.intentionAnalyzerOutput = intentionAnalyzer.eliminate(processSet, this.cesDefinition);
		int countOfPassedProcesses = this.intentionAnalyzerOutput.getRealizationProcess().size();
		assertEquals("3 Process Definitions should Satisfy the Criteria.", 3, countOfPassedProcesses);
	}
	
	@Test
	public void testProcessSelector(){
		this.testContextAnalyzer();
		ProcessSelector processSelector = new ProcessSelector(this.cesDefinition);
		this.processSelectorOutput = processSelector.selectProcess(this.contextAnalyzerOutput, this.cesDefinition);
		assertEquals("Process SemiAutomatedRepairPacking should be Selected.", 
											"SemiAutomatedRepairPacking", this.processSelectorOutput.getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName());
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
		this.processDispatcherOutput = processDispatcher.deployMainProcess(this.processSelectorOutput);
		assertEquals("Process Output should Contain 'done'.", "done", 
												this.processDispatcherOutput.getDataList().get(0).getValue());
		boolean complementaryOutput = processDispatcher.deployComplementaryProcess(this.processSelectorOutput);
		assertTrue("Complementary Process should Complete Successfully.", complementaryOutput);
	}
	
	@Test
	public void testCESExecutor(){
		CESExecutor cesExecutor = new CESExecutor(this.cesDefinition);
		cesExecutor.runCESExecutor();
		this.cesExecutorOutput = cesExecutor.isSuccess();
		assertTrue("CES Executor should Run Successfully without Exceptions.", this.cesExecutorOutput);
	}
}
