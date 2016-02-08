package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * A Generic Interface for selecting the Procss Engine, Realization Process or DataProvider in runtime.
 * @author Debasis Kar
 */

public interface ISelectable {
	
	public TProcessDefinition getRealizationProcess(List<TProcessDefinition> processDefinitionList);
	
	public TDataList deployProcess(String bpmnFilePath, String processName, TDataList input, TDataList outputHolder);
	
	public TContexts getData(List<TContext> contextList);
	
	public boolean isContextAvailable();
	
}
