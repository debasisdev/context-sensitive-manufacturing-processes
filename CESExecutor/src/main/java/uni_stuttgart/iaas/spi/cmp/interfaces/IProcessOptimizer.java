package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessOptimizer;

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
 * A generic interface for {@link ProcessOptimizer} Module.
 * @author Debasis Kar
 */

public interface IProcessOptimizer {
	/**
	 * Any custom {@link ProcessOptimizer} has to implement the following method that will take
	 * {@link TProcessContent} defined inside a {@link TProcessDefinition} as its input parameter. 
	 * The implementation looks for any optimization model is defined for the process. 
	 * It deploys and executes the optimization model and will return true if successful else false.
	 * @author Debasis Kar
	 * @param processContent
	 * @return boolean
	 */
	public boolean optimizeProcess(TProcessDefinition processContent);
}
