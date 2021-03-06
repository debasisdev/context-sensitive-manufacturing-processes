package uni_stuttgart.iaas.spi.cmp.selectors;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcess;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectionManager;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessSelector;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

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
 * A helper class to {@link ProcessSelector} that realizes an algorithm by analyzing the weights attached.
 * It implements the {@link ISelectionManager} interface.
 * @author Debasis Kar
 */

public class WeightAnalyzer implements ISelectionManager{
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(WeightAnalyzer.class);
	
	@Override
	public TRealizationProcess findRealizationProcess(List<TRealizationProcess> processDefinitionList){
		Set<String> processIds = new HashSet<String>();
		for(TRealizationProcess processDefinition : processDefinitionList){
			processIds.add(processDefinition.getId());
		}
		TRealizationProcess dispatchedProcess = new TRealizationProcess();
		Map<String, Double> weightList = new TreeMap<String, Double>();
		//Ensure the list of process definition not to be empty
		if(!processDefinitionList.isEmpty()){
			switch(processDefinitionList.size()){
				case 1: dispatchedProcess = processDefinitionList.iterator().next();
						break;
				default: 
						for(String processIdentifier : processIds){
							//Prepare a table that contains process IDs and respective weights attached
							for(TRealizationProcess processDefinition : processDefinitionList){
									String processId = processDefinition.getId();
									if(processIdentifier.equals(processId)){
										NodeList nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
										for(int count=0; count < nodeList.getLength(); count++){
											if(nodeList.item(count).getNodeName().equals(CESExecutorConfig.REPOSITORY_FIELD_WEIGHT)){
												String processWeight = nodeList.item(count).getTextContent().trim();
												weightList.put(processId,Double.parseDouble(processWeight));
											}
										}
									}
							}
						}
						//Find the process ID with highest weight by sorting the HashMap
						String dispatchedProcessName = this.sortMap(weightList);
						//Select the indicated process definition for dispatcher and optimizer
						for(TRealizationProcess processDef : processDefinitionList){
							if(processDef.getId().equals(dispatchedProcessName)){
								dispatchedProcess = processDef;
							}
						}
						break;
			}
		}
		return dispatchedProcess;
	}
	
	/**
	 * This is a Map sorter that sorts a Map and returns the Key of the highest valued Map element.
	 * @author Debasis Kar
	 * @param Map<String, Double>
	 * @return String
	 */
	private String sortMap(Map<String, Double> inputMap) {
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(inputMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> ob1, Map.Entry<String, Double> ob2) {
				return (ob1.getValue()).compareTo(ob2.getValue());
			}
		});
		log.info("Weight Analysis is in Process...");
		Map<String, Double> outputMap = new TreeMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> iter = list.iterator(); iter.hasNext(); ) {
			Map.Entry<String, Double> entry = iter.next();
			outputMap.put(entry.getKey(), entry.getValue());
		}
		log.info("Process is Selected.");
		return outputMap.keySet().toArray()[0].toString();
	}
}
