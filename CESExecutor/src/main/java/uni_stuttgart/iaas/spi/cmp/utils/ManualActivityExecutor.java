package uni_stuttgart.iaas.spi.cmp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;

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
 * An utility class to perform all the manual tasks inside a BPMN model in Activiti Engine.
 * @author Debasis Kar
 */

public class ManualActivityExecutor {
	
	/**Variable to store the {@link TaskService} reference 
	 * @author Debasis Kar
	 * */
	private TaskService taskService;

	/**Default constructor of {@link ManualActivityExecutor}
	 * @author Debasis Kar
	 * */
	public ManualActivityExecutor(){
		this.taskService = null;
	}
	
	/**Parameterized constructor of {@link ManualActivityExecutor}
	 * @author Debasis Kar
	 * @param taskService
	 * */
	public ManualActivityExecutor(TaskService taskService){
		this.taskService = taskService;
	}
	
	/**
	 * This method performs the manual activities required for the execution of an business activity.
	 * @author Debasis Kar
	 * @param input
	 * @return void
	 */
	public void performManualTask(TDataList input){
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
		List<Task> tasks = this.taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
		for (Task task : tasks) {
			System.out.println( task.getName() + " Task is available for Manual Workers group.");
			this.taskService.claim(task.getId(), packerName);
			System.out.println("Task assigned to " + packerName);
			Map<String, Object> taskVariables = new HashMap<String, Object>();
			taskVariables.put("packStatus", "true");
			this.taskService.complete(task.getId(),taskVariables);
			System.out.println("Packing Completed by " + packerName + ".");
	    }
		//Manual Sealing Work
	    tasks = this.taskService.createTaskQuery().taskCandidateGroup("manualworker").list();
		for (Task task : tasks) {
			System.out.println(task.getName() + " Task is available for Manual Workers group.");
			this.taskService.claim(task.getId(), operatorName);
			System.out.println("Task assigned to " + operatorName);
			Map<String, Object> taskVariables = new HashMap<String, Object>();
			taskVariables.put("sealStatus", "true");
			this.taskService.complete(task.getId(),taskVariables);
			System.out.println("Sealing Completed by " + operatorName + ".");
	    }
		//Manual Sorting/Palletizing Work
		tasks = this.taskService.createTaskQuery().taskCandidateGroup("supervisor").list();
		for (Task task : tasks) {
			System.out.println(task.getName() + " Task is available for Supervisor.");
			this.taskService.claim(task.getId(), supervisorName);
			System.out.println("Task assigned to " + supervisorName);
		    this.taskService.complete(task.getId());
		}
		System.out.println("Sorting/Palletizing Completed by " + supervisorName + ".");
	}

}
