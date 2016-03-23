package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessDispatcher;

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
 * A generic interface for {@link ProcessDispatcher} module will invoke process engines.
 * @author Debasis Kar
 */

public interface IProcessEngine {
	
	/**
	 * Any custom deployment manager has to implement the following method that will take the 
	 * {@link TProcessDefinition} received from {@link ProcessOptimizer} and will deploy to an underlying
	 * process engine, e.g., Activiti BPMN, Apache ODE BPEL, etc. This interface is intended to make the 
	 * deployment of the CES pluggable and users can write their own deployment code as per their own engine.
	 * @author Debasis Kar
	 * @param processDefinition
	 * @return TDataList
	 */
	public TDataList deployMainProcess(TProcessDefinition processDefinition);
	
	/**
	 * Any custom deployment manager has to implement the following method that will search a Complementary
	 * Process Definition ({@link TProcessDefinition}) of the main process and will deploy to an underlying process 
	 * engine, e.g., Activiti BPMN, Apache ODE BPEL, etc. The output of the complementary process is not needed for
	 * any practical purposes, so it only returns the success status in a boolean output.
	 * @author Debasis Kar
	 * @param processDefinition
	 * @return boolean
	 */
	public boolean deployComplementaryProcess(TProcessDefinition processDefinition);
}
