package org.activiti.designer.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import uni_stuttgart.iaas.spi.cmp.archint.IRealization;

public class PRS002 implements IRealization{
	
	private static final Logger log = Logger.getLogger(PRS002.class.getName());
	
	public TDataList startProcess(String filePath, TDataList input, TDataList outputHolder) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		try {
			repositoryService.createDeployment().addInputStream("semimanualnewmachine.bpmn20.xml",
					new FileInputStream(filePath)).deploy();

			RuntimeService runtimeService = processEngine.getRuntimeService();
			Map<String, Object> variableMap = new HashMap<String, Object>();
			/*Process Inputs*/
			variableMap.put("orderID", "OD153728DE");
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("semimanualnewmachine", variableMap);
			log.info("<ID:" + processInstance.getId() + ">");
			
			String packerName = "Jack";
			String operatorName = "Joe";
			String supervisorName = "Jill";

			for(TData data : input.getDataList()){
				switch(data.getName()){
					case "machinistName": packerName = data.getValue(); break;
					case "operatorName": operatorName = data.getValue(); break;
					case "supervisorName": supervisorName = data.getValue(); break;
					default: break;
				}
			}
			
			TaskService taskService = processEngine.getTaskService();
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
			for (Task task : tasks) {
				System.out.println( task.getName() + " Task is available for Manual Workers group.");
				taskService.claim(task.getId(), packerName);
				System.out.println("Task assigned to " + packerName);
				Map<String, Object> taskVariables = new HashMap<String, Object>();
				taskVariables.put("packStatus", "true");
				Thread.sleep(6000);
				taskService.complete(task.getId(),taskVariables);
				System.out.println("Packing Completed by " + packerName + ".");
		    }
			
		    tasks = taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
			for (Task task : tasks) {
				System.out.println(task.getName() + " Task is available for Manual Workers group.");
				taskService.claim(task.getId(), operatorName);
				System.out.println("Task assigned to " + operatorName);
				Map<String, Object> taskVariables = new HashMap<String, Object>();
				taskVariables.put("sealStatus", "true");
				Thread.sleep(8000);
				taskService.complete(task.getId(),taskVariables);
				System.out.println("Sealing Completed by " + operatorName + ".");
		    }
			
			tasks = taskService.createTaskQuery().taskCandidateGroup("supervisor").list();
			for (Task task : tasks) {
				System.out.println(task.getName() + " Task is available for Supervisor.");
				taskService.claim(task.getId(), supervisorName);
				System.out.println("Task assigned to " + supervisorName);
				Thread.sleep(8000);
			    taskService.complete(task.getId());
			}
			System.out.println("Task Completed by " + supervisorName + ".");
			
			outputHolder.getDataList().get(0).setValue("done");

			HistoryService historyService = processEngine.getHistoryService();
		    HistoricProcessInstance historicProcessInstance =
		      historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
		    log.info("Process Instance End-time: " + historicProcessInstance.getEndTime());
			
		} catch (FileNotFoundException e) {
			log.severe("PRS.002.03: FileNotFoundException has Occurred.");
		} catch (InterruptedException e) {
			log.severe("PRS.002.02: InterruptedException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("PRS.002.01: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("PRS.002.00: Unknown Exception has Occurred - " + e);
		}
		return outputHolder;
	}
}