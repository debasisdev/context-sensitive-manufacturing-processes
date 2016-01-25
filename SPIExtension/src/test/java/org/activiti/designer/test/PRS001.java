package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import uni_stuttgart.iaas.spi.cmp.archint.IRealization;

public class PRS001 implements IRealization{

	public TDataList startProcess(String fileName, ProcessEngine processEngine, TDataList input) {

		RepositoryService repositoryService = processEngine.getRepositoryService();
		try {
			repositoryService.createDeployment().addInputStream("semimanualrepairsealing.bpmn20.xml",
					new FileInputStream(fileName)).deploy();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
		
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		/*Process Inputs*/
		variableMap.put("orderID", "OD153728DE");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("semimanualrepairsealing", variableMap);
		assertNotNull(processInstance.getId());
		
		System.out.println("<ID:" + processInstance.getId() + ">-<" + processInstance.getProcessDefinitionId() + ">");
		
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
		for (Task task : tasks) {
			System.out.println( task.getName() + " Task is available for Manual Workers group.");
			taskService.claim(task.getId(), "John");
			System.out.println("Task assigned to John");
			Map<String, Object> taskVariables = new HashMap<String, Object>();
			taskVariables.put("packStatus", "true");
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			taskService.complete(task.getId(),taskVariables);
			System.out.println("Packing Completed by John.");
	    }
		
	    tasks = taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
		for (Task task : tasks) {
			System.out.println(task.getName() + " Task is available for Manual Workers group.");
			taskService.claim(task.getId(), "Joe");
			System.out.println("Task assigned to Joe");
			Map<String, Object> taskVariables = new HashMap<String, Object>();
			taskVariables.put("sealStatus", "true");
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			taskService.complete(task.getId(),taskVariables);
			System.out.println("Sealing Completed by Joe.");
	    }
		
		tasks = taskService.createTaskQuery().taskCandidateGroup("supervisor").list();
		for (Task task : tasks) {
			System.out.println(task.getName() + " Task is available for Supervisor.");
			taskService.claim(task.getId(), "Frank");
			System.out.println("Task assigned to Frank");
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    taskService.complete(task.getId());
		}
		System.out.println("Task Completed by Frank.");

		HistoryService historyService = processEngine.getHistoryService();
	    HistoricProcessInstance historicProcessInstance =
	      historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
	    System.out.println("Process Instance End-time: " + historicProcessInstance.getEndTime());
		return input;
	}
}