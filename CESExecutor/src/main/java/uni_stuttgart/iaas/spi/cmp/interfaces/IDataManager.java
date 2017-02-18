package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinitions;

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
 * A generic interface for different DBMS clients to extract data from Middle-wares.
 * @author Debasis Kar
 */

public interface IDataManager {

	/**
	 * Any custom implementation of DBMS data fetcher must implement this 
	 * method to get data from the Middle-ware that takes {@link List} of {@link TContext}
	 * as its input parameter.
	 * @author Debasis Kar
	 * @param contextList
	 * @return TContexts
	 */
	public TContextDefinitions getDataFromDatabase(List<TContextDefinition> contextList);
	
	/**This is the getter method to know whether there is any context data 
	 * available or not after the fetching operation.
	 * @author Debasis Kar
	 * @param void
	 * @return boolean
	 * */
	public boolean isContextAvailable();
}
