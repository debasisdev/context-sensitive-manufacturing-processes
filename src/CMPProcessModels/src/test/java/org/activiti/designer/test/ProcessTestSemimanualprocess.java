package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestSemimanualprocess {

	private String filename = "D:\\MyWorkThesis\\CMPProcessModels\\src\\main\\resources\\diagrams\\semimanualprocess.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	public void startProcess() throws Exception {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment().addInputStream("semimanualprocess.bpmn20.xml",
				new FileInputStream(filename)).deploy();
		System.out.println("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
		
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		/*Process Inputs*/
		variableMap.put("orderID", "OD153728DE");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("semimanualprocess", variableMap);
		assertNotNull(processInstance.getId());
		
		System.out.println("Id: " + processInstance.getId() + "---" + processInstance.getProcessDefinitionId());
		
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
		for (Task task : tasks) {
			System.out.println( task.getName() + " Task is available for Manual Workers group.");
			taskService.claim(task.getId(), "John");
			System.out.println("Task assigned to John");
			Map<String, Object> taskVariables = new HashMap<String, Object>();
			taskVariables.put("packStatus", "true");
			Thread.sleep(6000);
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
			Thread.sleep(8000);
			taskService.complete(task.getId(),taskVariables);
			System.out.println("Sealing Completed by Joe.");
	    }
		
		tasks = taskService.createTaskQuery().taskCandidateGroup("supervisor").list();
		for (Task task : tasks) {
			System.out.println(task.getName() + " Task is available for Supervisor.");
			taskService.claim(task.getId(), "Frank");
			System.out.println("Task assigned to Frank");
			Thread.sleep(8000);
		    taskService.complete(task.getId());
		}
		System.out.println("Task Completed by Frank.");

		HistoryService historyService = processEngine.getHistoryService();
	    HistoricProcessInstance historicProcessInstance =
	      historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
	    System.out.println("Process Instance End-time: " + historicProcessInstance.getEndTime());
	}
}