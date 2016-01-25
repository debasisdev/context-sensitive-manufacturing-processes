package org.activiti.designer.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import uni_stuttgart.iaas.spi.cmp.archint.IRealization;

public class POPT01 implements IRealization{

	public TDataList startProcess(String fileName, ProcessEngine processEngine, TDataList input) {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		try {
			repositoryService.createDeployment().addInputStream("optimizationprocess.bpmn20.xml",
					new FileInputStream(fileName)).deploy();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
		
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		/*Process Inputs*/
		variableMap.put("orderID", "OD153728DE");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("optimizationprocess", variableMap);
		
		System.out.println("<ID:" + processInstance.getId() + ">-<" + processInstance.getProcessDefinitionId() + ">");
		return input;
	}
}