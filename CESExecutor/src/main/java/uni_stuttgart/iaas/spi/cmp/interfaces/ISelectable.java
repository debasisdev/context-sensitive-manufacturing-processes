package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcess;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinitions;
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
 * A generic interface for selecting the Process-Engine, Realization-Process or DataProvider 
 * dynamically in runtime.
 * @author Debasis Kar
 */

public interface ISelectable {
	
	/**
	 * This method is executed to choose multiple variants of {@link ISelectionManager}. 
	 * @author Debasis Kar
	 * @param processDefinitionList
	 * @return TProcessDefinition
	 */
	public TRealizationProcess getRealizationProcess(List<TRealizationProcess> processDefinitionList);
	
	/**
	 * This method is executed to choose multiple variants of {@link IExecutionManager}. . 
	 * @author Debasis Kar
	 * @param bpmnFilePath
	 * @param processName
	 * @param input
	 * @param outputHolder
	 * @return TDataList
	 */
	public TDataList deployProcess(String bpmnFilePath, String processName, TDataList input, TDataList outputHolder);
	
	/**
	 * This method is executed to choose multiple variants of {@link IDataManager}. 
	 * @author Debasis Kar
	 * @param contextList 
	 * @return TContexts
	 */
	public TContextDefinitions getData(List<TContextDefinition> contextList);
	
	/**
	 * Any custom variant of {@link IDataManager} has this method. It needs to be exposed further for {@link QueryManager}. 
	 * @author Debasis Kar
	 * @param void 
	 * @return boolean
	 */
	public boolean isContextAvailable();
	
}
