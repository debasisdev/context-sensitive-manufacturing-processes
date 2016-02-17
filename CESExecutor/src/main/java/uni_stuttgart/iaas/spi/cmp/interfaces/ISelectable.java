package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.realizations.QueryManager;

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
	public TProcessDefinition getRealizationProcess(List<TProcessDefinition> processDefinitionList);
	
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
	public TContexts getData(List<TContext> contextList);
	
	/**
	 * Any custom variant of {@link IDataManager} has this method. It needs to be exposed further for {@link QueryManager}. 
	 * @author Debasis Kar
	 * @param void 
	 * @return boolean
	 */
	public boolean isContextAvailable();
	
}
