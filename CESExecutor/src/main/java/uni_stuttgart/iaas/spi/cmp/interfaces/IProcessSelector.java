package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessSelector;

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
 * A generic interface for {@link ProcessSelector} Module.
 * @author Debasis Kar
 */

public interface IProcessSelector {
	
	/**
	 * Any custom {@link ProcessSelector} has to implement the following method that will take 
	 * {@link TProcessDefinitions} (a set of process definitions/alternatives) and TCESDefinitionas as its 
	 * input parameter. The implementation selects one process among the {@link TProcessDefinitions} available. 
	 * If one selection is strategy is available, then it selects one process as per the strategy. 
	 * If no strategy is available, then a process is chosen randomly.
	 * @author Debasis Kar
	 * @param processSet
	 * @param cesDefinition
	 * @return TProcessDefinition 
	 */
	public TProcessDefinition selectProcess(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition);
	
}
