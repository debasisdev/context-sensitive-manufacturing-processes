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

public class PackBlankets {
	
	//Absolute file path of the BPMN file
	private String file = "D:\\MyWorkThesis\\CESTaskDummyProcess\\src\\test\\resources\\PackBlankets.bpmn";
	
	//Local log writer
	private static final Logger log = LoggerFactory.getLogger(PackBlankets.class);

	@Test
	public void startProcess() throws Exception {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment().addInputStream("Pack.bpmn20.xml", new FileInputStream(file)).deploy();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("orderID", "OD153728DE");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("cesProcess", variableMap);
		assertNotNull(processInstance.getId());
		log.info("<Process Instance ID:" + processInstance.getId() + "> is Realized.");
	}
	
}																											