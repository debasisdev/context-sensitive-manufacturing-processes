package uni_stuttgart.iaas.spi.cmp.realizations;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcess;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinitions;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataManager;
import uni_stuttgart.iaas.spi.cmp.interfaces.IExecutionManager;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectable;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectionManager;

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
 * A generic class that implements {@link ISelectable}. This is primarily used for injecting dependency 
 * in runtime among the realization classes and multiple variations they can support.
 * @author Debasis Kar
 */

public class DynamicSelector implements ISelectable{
	
	/**Variable to store the {@link ISelectionManager} reference to inject selection strategy 
	 * @author Debasis Kar
	 * */
	private ISelectionManager selectionManager;
	
	/**Variable to store the {@link IExecutionManager} reference to inject process engine required 
	 * @author Debasis Kar
	 * */
	private IExecutionManager executionManager;
	
	/**Variable to store the {@link IDataManager} reference to inject database manager as required 
	 * @author Debasis Kar
	 * */
	private IDataManager dataManager;

	/**
	 * This is a setter method to set the {@link ISelectionManager}.
	 * @author Debasis Kar
	 * @param selectionManager
	 * @return void
	 */
	public void setSelectionManager(ISelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}
	
	/**
	 * This is a setter method to set the {@link IExecutionManager}.
	 * @author Debasis Kar
	 * @param executionManager
	 * @return void
	 */
	public void setExecutionManager(IExecutionManager executionManager) {
		this.executionManager = executionManager;
	}

	/**
	 * This is a setter method to set the {@link IDataManager}.
	 * @author Debasis Kar
	 * @param dataManager
	 * @return void
	 */
	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	@Override
	public TRealizationProcess getRealizationProcess(List<TRealizationProcess> processDefinitionList) {
		return selectionManager.findRealizationProcess(processDefinitionList);
	}

	@Override
	public TDataList deployProcess(String filePath, String processName, TDataList input, TDataList outputHolder) {
		return executionManager.startProcess(filePath, processName, input, outputHolder);
	}
	
	@Override
	public TContextDefinitions getData(List<TContextDefinition> contextList){
		return dataManager.getDataFromDatabase(contextList);
	}

	@Override
	public boolean isContextAvailable() {
		return dataManager.isContextAvailable();
	}
	
}
