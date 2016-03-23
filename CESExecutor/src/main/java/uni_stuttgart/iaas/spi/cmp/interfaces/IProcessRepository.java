package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

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
 * A generic interface for process repository access.
 * @author Debasis Kar
 */

public interface IProcessRepository {

	/**
	 * This method must be implemented in order to extract the process repository data defined inside
	 * {@link TTaskCESDefinition} and return {@link TProcessDefinitions} for filtering.
	 * @author Debasis Kar
	 * @param cesDefinition
	 * @return TProcessDefinitions 
	 */
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition);

	
}
