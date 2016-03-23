package uni_stuttgart.iaas.spi.cmp.interfaces;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
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
 * A generic interface for implementations of realization processes.
 * @author Debasis Kar
 */

public interface IExecutionManager {
	
	/**
	 * Any custom implementation a process model must be defined inside this method
	 * such that this can be called from the {@link ProcessDispatcher} or {@link ProcessOptimizer} 
	 * easily without any ambiguity. This method takes the input and output variables of the process
	 * along with the process name and process model location. Finally it writes the output (if any) 
	 * to the specified output variable of {@link TDataList} in an {@link TTaskCESDefinition} envelope.
	 * @author Debasis Kar
	 * @param filePath
	 * @param processName
	 * @param input
	 * @param outputHolder
	 * @return TDataList
	 */
	public TDataList startProcess(String filePath, String processName, TDataList input, TDataList outputHolder);
}
