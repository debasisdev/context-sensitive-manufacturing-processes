package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class PackBlankets {
	
	//Absolute file path of the BPMN file
	private String file = "D:\\MyWorkThesis\\CESTaskDummyProcess\\src\\test\\resources\\PackBlankets.bpmn";
	
	//Local log writer
	private static final Logger log = LoggerFactory.getLogger(PackBlankets.class);

	@Test
	public void startProcess() throws Exception {
		long startTime = System.currentTimeMillis();
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment().addInputStream("Pack.bpmn20.xml", new FileInputStream(file)).deploy();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("orderID", "DE37464358BY");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("cesProcess", variableMap);
		assertNotNull(processInstance.getId());
		log.info("<Process Instance ID:" + processInstance.getId() + "> is Realized.");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);
	}
	
}																											