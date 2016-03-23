package uni_stuttgart.iaas.spi.cmp.selectors;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectionManager;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessSelector;

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
 * A helper class to {@link ProcessSelector} that realizes an algorithm randomly.
 * It implements the {@link ISelectionManager} interface.
 * @author Debasis Kar
 */

public class RandomAnalyzer implements ISelectionManager{
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(RandomAnalyzer.class);
	
	@Override
	public TProcessDefinition findRealizationProcess(List<TProcessDefinition> processDefinitionList){
		Set<String> processIds = new HashSet<String>();
		for(TProcessDefinition processDefinition : processDefinitionList){
			processIds.add(processDefinition.getId());
		}
		TProcessDefinition dispatchedProcess = new TProcessDefinition();		
		log.info("A process will be randomly selected.");
		//Choose randomly by generating a random number
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
