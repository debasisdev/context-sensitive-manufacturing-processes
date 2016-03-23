package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntentions;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.realizations.ContextAnalyzer;
import uni_stuttgart.iaas.spi.cmp.realizations.IntentionAnalyzer;

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
 * A generic interface for the filtering modules, i.e., {@link ContextAnalyzer} and {@link IntentionAnalyzer}. 
 * @author Debasis Kar
 */

public interface IProcessEliminator {
	/**
	 * Any custom {@link ContextAnalyzer} or {@link IntentionAnalyzer} has to implement the 
	 * following method that will take {@link TProcessDefinitions} (a set of process definitions/alternatives) 
	 * and {@link TCESDefinitionas} as its inputs. The implementation must filter {@link TProcessDefinitions} and 
	 * the final result is stored as {@link TProcessDefinitions} (list of {@link TProcessDefinition}) that are 
	 * validated against the satisfying {@link TContexts} or {@link TIntentions} respectively.
	 * @author Debasis Kar
	 * @param processDefinitions
	 * @param cesDefinition
	 * @return TProcessDefinitions 
	 */
	public TProcessDefinitions eliminate(TProcessDefinitions processDefinitions, TTaskCESDefinition cesDefinition);

}
