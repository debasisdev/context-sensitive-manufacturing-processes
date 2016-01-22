package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.io.FileInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class PCOM01 {

	private String filename = "F:\\Dropbox\\GitLab Repository\\Sample BPMN Models\\PCOM01.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	public void startProcess() throws Exception {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment().addInputStream("complementaryprocess.bpmn20.xml",
				new FileInputStream(filename)).deploy();
		System.out.println("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
		
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		/*Process Inputs*/
		variableMap.put("orderID", "OD153728DE");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("complementaryprocess", variableMap);
		assertNotNull(processInstance.getId());
		
		System.out.println("<ID:" + processInstance.getId() + ">-<" + processInstance.getProcessDefinitionId() + ">");
	}
}