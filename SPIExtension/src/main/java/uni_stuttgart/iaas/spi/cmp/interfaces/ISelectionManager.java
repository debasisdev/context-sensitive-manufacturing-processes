package uni_stuttgart.iaas.spi.cmp.interfaces;

import java.util.List;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

public interface ISelectionManager {
	
	public TProcessDefinition findRealizationProcess(List<TProcessDefinition> processDefinitionList);
}
