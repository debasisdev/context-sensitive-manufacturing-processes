package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

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
 * A generic interface for a weight based module.
 * @author Debasis Kar
 */

public interface ISelectionManager {
	
	/**
	 * Any custom selection algorithm implementor has to implement the following method to 
	 * select a {@link TProcessDefinition} out of a {@link List} of {@link TProcessDefinition}. 
	 * @author Debasis Kar
	 * @param processDefinitionList
	 * @return TProcessDefinition
	 */
	public TProcessDefinition findRealizationProcess(List<TProcessDefinition> processDefinitionList);
}
