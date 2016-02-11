package uni_stuttgart.iaas.spi.cmp.engines;

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
import uni_stuttgart.iaas.spi.cmp.interfaces.IExecutionManager;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessDispatcher;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessOptimizer;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/**
 * A helper class to {@link ProcessDispatcher} and {@link ProcessOptimizer} that deploys process model
 * into Activiti BPMN engine. It implements the {@link IExecutionManager} interface.
 * @author Debasis Kar
 */

public class ActivitiExecutor implements IExecutionManager{
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(ActivitiExecutor.class.getName());
	
	@Override
	public TDataList startProcess(String filePath, String processName, TDataList input, TDataList outputHolder) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		try {
			repositoryService.createDeployment().addInputStream("cmp_process.bpmn20.xml", new FileInputStream(filePath)).deploy();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			Map<String, Object> variableMap = new HashMap<String, Object>();
			//Initial Process Inputs
			variableMap.put("orderID", "OD153728DE");
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processName, variableMap);
			//Check whether the manual activities need to be run
			boolean processtype = this.decideProcessType(processName);
			if(processtype){
				TaskService taskService = processEngine.getTaskService();
				this.performManualTask(taskService, input);
			}
			//Write back success if everything goes normally
			log.info("<ID:" + processInstance.getId() + ">");
			outputHolder.getDataList().get(0).setValue(CESExecutorConfig.SUCCESS_STRING);
			//Some auditing before signing-off
			HistoryService historyService = processEngine.getHistoryService();
		    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
		    log.info("Process Instance End-time: " + historicProcessInstance.getEndTime());
		} catch (FileNotFoundException e) {
			log.severe("ACTEXP02: FileNotFoundException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("ACTEXP01: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("ACTEXP00: Unknown Exception has Occurred - " + e);
		}
		return outputHolder;
	}
	
	/**
	 * This method decides whether a process requires manual labor or not from the process name itself.
	 * @author Debasis Kar
	 * @param String
	 * @return boolean
	 */
	private boolean decideProcessType(String processName){
		if(processName.toUpperCase().contains("MANUAL") || processName.toUpperCase().contains("SEMI")){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * This method performs the manual activities required for the execution of an business activity.
	 * @author Debasis Kar
	 * @param TaskService, TDataList
	 * @return void
	 */
	private void performManualTask(TaskService taskService, TDataList input){
		//Default Employees
		String packerName = "Jack";
		String operatorName = "Joe";
		String supervisorName = "Jill";
		//Runtime Change of Employee List
		for(TData data : input.getDataList()){
			switch(data.getName()){
				case "machinistName": packerName = data.getValue(); break;
				case "operatorName": operatorName = data.getValue(); break;
				case "supervisorName": supervisorName = data.getValue(); break;
				default: break;
			}
		}
		//Manual Packing Work
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
		for (Task task : tasks) {
			System.out.println( task.getName() + " Task is available for Manual Workers group.");
			taskService.claim(task.getId(), packerName);
			System.out.println("Task assigned to " + packerName);
			Map<String, Object> taskVariables = new HashMap<String, Object>();
			taskVariables.put("packStatus", "true");
			taskService.complete(task.getId(),taskVariables);
			System.out.println("Packing Completed by " + packerName + ".");
	    }
		//Manual Sealing Work
	    tasks = taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
		for (Task task : tasks) {
			System.out.println(task.getName() + " Task is available for Manual Workers group.");
			taskService.claim(task.getId(), operatorName);
			System.out.println("Task assigned to " + operatorName);
			Map<String, Object> taskVariables = new HashMap<String, Object>();
			taskVariables.put("sealStatus", "true");
			taskService.complete(task.getId(),taskVariables);
			System.out.println("Sealing Completed by " + operatorName + ".");
	    }
		//Manual Sorting/Palletizing Work
		tasks = taskService.createTaskQuery().taskCandidateGroup("supervisor").list();
		for (Task task : tasks) {
			System.out.println(task.getName() + " Task is available for Supervisor.");
			taskService.claim(task.getId(), supervisorName);
			System.out.println("Task assigned to " + supervisorName);
		    taskService.complete(task.getId());
		}
		System.out.println("Sorting/Palletizing Completed by " + supervisorName + ".");
	}

}
