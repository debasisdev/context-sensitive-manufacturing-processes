package uni_stuttgart.iaas.spi.cmp.realizations;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataManager;
import uni_stuttgart.iaas.spi.cmp.interfaces.IExecutionManager;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectable;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectionManager;

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
	 * @param ISelectionManager
	 * @return void
	 */
	public void setSelectionManager(ISelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}
	
	/**
	 * This is a setter method to set the {@link IExecutionManager}.
	 * @author Debasis Kar
	 * @param IExecutionManager
	 * @return void
	 */
	public void setExecutionManager(IExecutionManager executionManager) {
		this.executionManager = executionManager;
	}

	/**
	 * This is a setter method to set the {@link IDataManager}.
	 * @author Debasis Kar
	 * @param IDataManager
	 * @return void
	 */
	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	@Override
	public TProcessDefinition getRealizationProcess(List<TProcessDefinition> processDefinitionList) {
		return selectionManager.findRealizationProcess(processDefinitionList);
	}

	@Override
	public TDataList deployProcess(String filePath, String processName, TDataList input, TDataList outputHolder) {
		return executionManager.startProcess(filePath, processName, input, outputHolder);
	}
	
	@Override
	public TContexts getData(List<TContext> contextList){
		return dataManager.getDataFromDatabase(contextList);
	}

	@Override
	public boolean isContextAvailable() {
		return dataManager.isContextAvailable();
	}
	
}
