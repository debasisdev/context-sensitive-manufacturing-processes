package uni_stuttgart.iaas.spi.cmp.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectionManager;

/**
 * A Helper Class to Process Selector that realizes an Algorithm for selecting a process.
 * @author Debasis Kar
 */

public class RandomAnalyzer implements ISelectionManager{
	
	/**Local Log Writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(RandomAnalyzer.class.getName());
	
	/**This method connects to the MongoDB Instance and tries to fetch the required data in runtime.
	 * @author Debasis Kar
	 * @param void
	 * @return TContexts
	 * */
	@Override
	public TProcessDefinition findRealizationProcess(List<TProcessDefinition> processDefinitionList){
		Set<String> processIds = new HashSet<String>();
		for(TProcessDefinition processDefinition : processDefinitionList){
			processIds.add(processDefinition.getId());
		}
		TProcessDefinition dispatchedProcess = new TProcessDefinition();		
		log.info("A process will be randomly selected.");
		if(!processDefinitionList.isEmpty()){
			Random randomNumberGenerator = new Random();
			int max = processDefinitionList.size();
			int min = 1;
			int randInt = randomNumberGenerator.nextInt((max - min) + 1);
			dispatchedProcess = processDefinitionList.get(randInt);
		}
		return dispatchedProcess;
	}
	
}
