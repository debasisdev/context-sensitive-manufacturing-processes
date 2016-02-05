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

public class DynamicSelector implements ISelectable{
	
	private ISelectionManager selectionManager; 
	private IExecutionManager executionManager;
	private IDataManager dataManager;

	public void setSelectionManager(ISelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}
	
	public void setExecutionManager(IExecutionManager executionManager) {
		this.executionManager = executionManager;
	}

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
