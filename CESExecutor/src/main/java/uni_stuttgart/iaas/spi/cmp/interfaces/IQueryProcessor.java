package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import uni_stuttgart.iaas.spi.cmp.realizations.QueryManager;

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
 * A generic interface for {@link QueryManager} module.
 * @author Debasis Kar
 */

public interface IQueryProcessor {
	
	/**
	 * Any custom {@link QueryManager} has to implement the following method that will take 
	 * {@link TTaskCESDefinition} (a set of required contexts for a domain resides in this definition) 
	 * as its input parameter. The implementation must look for the repository where the required contexts 
	 * can be found such that they can be fetched and serialized in to an XML file named ContextData 
	 * by {@link ContextAnalyzer.
	 * @author Debasis Kar
	 * @param cesDefinition
	 * @return TContexts
	 */
	public TContexts queryRawContextData(TTaskCESDefinition cesDefinition);
	
}
