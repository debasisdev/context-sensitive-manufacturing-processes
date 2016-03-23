package uni_stuttgart.iaas.spi.cmp.engines;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import uni_stuttgart.iaas.spi.cmp.interfaces.IExecutionManager;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessDispatcher;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessOptimizer;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;
import uni_stuttgart.iaas.spi.cmp.utils.ManualActivityExecutor;

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

/**
 * A helper class to {@link ProcessDispatcher} and {@link ProcessOptimizer} that deploys process model
 * into Activiti BPMN engine. It implements the {@link IExecutionManager} interface.
 * @author Debasis Kar
 */

public class ActivitiExecutor implements IExecutionManager{
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(ActivitiExecutor.class);
	
	@Override
	public TDataList startProcess(String filePath, String processName, TDataList input, TDataList outputHolder) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		try {
			repositoryService.createDeployment().addInputStream("cmp_process.bpmn20.xml", new FileInputStream(filePath)).deploy();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			Map<String, Object> variableMap = new HashMap<String, Object>();
			//Initial Process Inputs
			for(TData data : input.getDataList()){
				variableMap.put(data.getName(), data.getValue());
			}
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processName, variableMap);
			//Check whether the manual activities need to be run
			boolean processtype = this.decideProcessType(processName);
			if(processtype){
				TaskService taskService = processEngine.getTaskService();
				ManualActivityExecutor manualTaskRunner = new ManualActivityExecutor(taskService);
				manualTaskRunner.performManualTask(input);
			}
			//Write back success if everything goes normally
			log.info("<ID:" + processInstance.getId() + ">");
			outputHolder.getDataList().get(0).setValue(CESExecutorConfig.SUCCESS_STRING);
			//Some auditing before signing-off
			HistoryService historyService = processEngine.getHistoryService();
		    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
		    log.info("Process Instance End-time: " + historicProcessInstance.getEndTime());
		} catch (FileNotFoundException e) {
			log.error("ACTEXP02: FileNotFoundException has Occurred.");
			outputHolder.getDataList().get(0).setValue(CESExecutorConfig.ERROR_STRING);
			return outputHolder;
		} catch (NullPointerException e) {
			log.error("ACTEXP01: NullPointerException has Occurred.");
			outputHolder.getDataList().get(0).setValue(CESExecutorConfig.ERROR_STRING);
			return outputHolder;
		} catch (Exception e) {
			outputHolder.getDataList().get(0).setValue(CESExecutorConfig.ERROR_STRING);
			log.error("ACTEXP00: Unknown Exception has Occurred - " + e);
			return outputHolder;
		} 
		return outputHolder;
	}
	
	/**
	 * This method decides whether a process requires manual labor or not from the process name itself.
	 * @author Debasis Kar
	 * @param processName
	 * @return boolean
	 */
	private boolean decideProcessType(String processName){
		if(processName.toUpperCase().contains(CESExecutorConfig.MANUAL_STRING1) || processName.toUpperCase().contains(CESExecutorConfig.MANUAL_STRING2)){
			return true;
		}
		else {
			return false;
		}
	}

}
