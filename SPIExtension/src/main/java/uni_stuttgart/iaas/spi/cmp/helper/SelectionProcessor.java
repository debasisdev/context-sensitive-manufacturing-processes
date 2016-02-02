package uni_stuttgart.iaas.spi.cmp.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;

/**
 * A Helper Class to Process Selector that realizes an Algorithm for selecting a process.
 * @author Debasis Kar
 */

public class SelectionProcessor {
	
	/**Variable to Store Type of Selection Algorithm 
	 * @author Debasis Kar
	 * */
	private String algorithmIdentifier;
	
	/**Local Log Writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(SelectionProcessor.class.getName());
	
	/**Default Constructor of Selection Processor
	 * @author Debasis Kar
	 * */
	public SelectionProcessor(){
		this.algorithmIdentifier = CESConfig.SELECTION_RANDOM_NAMESPACE;
	}
	
	/**Parameterized Constructor of Selection Processor
	 * @author Debasis Kar
	 * */
	public SelectionProcessor(String algorithmIdentifier){
		this.algorithmIdentifier = algorithmIdentifier;
	}
	
	/**This method connects to the MongoDB Instance and tries to fetch the required data in runtime.
	 * @author Debasis Kar
	 * @param void
	 * @return TContexts
	 * */
	public TProcessDefinition findRealizationProcess(List<TProcessDefinition> processDefinitionList){
		Set<String> processIds = new HashSet<String>();
		for(TProcessDefinition processDefinition : processDefinitionList){
			processIds.add(processDefinition.getId());
		}
		TProcessDefinition dispatchedProcess = new TProcessDefinition();
		if(this.algorithmIdentifier.equals(CESConfig.SELECTION_WEIGHT_NAMESPACE)){
			Map<String, Double> weightList = new TreeMap<String, Double>();
			if(!processDefinitionList.isEmpty()){
				switch(processDefinitionList.size()){
					case 1: dispatchedProcess = processDefinitionList.iterator().next();
							break;
					default: 
							for(String processIdentifier : processIds){
								for(TProcessDefinition processDefinition : processDefinitionList){
										String processId = processDefinition.getId();
										if(processIdentifier.equals(processId)){
											Node nodeManuf = (Node) processDefinition.getProcessContent().getAny();
											String processWeight = nodeManuf.getChildNodes().item(3).getTextContent();
											weightList.put(processId,Double.parseDouble(processWeight));
										}
								}
							}
							String dispatchedProcessName = this.sortMap(weightList);
							for(TProcessDefinition processDef : processDefinitionList){
								if(processDef.getId().equals(dispatchedProcessName)){
									dispatchedProcess = processDef;
								}
							}
							break;
				}
			}
		}
		
		if(this.algorithmIdentifier.equals(CESConfig.SELECTION_RANDOM_NAMESPACE)){
			log.info("A process will be randomly selected.");
			if(!processDefinitionList.isEmpty()){
				Random randomNumberGenerator = new Random();
				int max = processDefinitionList.size();
				int min = 1;
				int randInt = randomNumberGenerator.nextInt((max - min) + 1);
				dispatchedProcess = processDefinitionList.get(randInt);
			}
		}
		return dispatchedProcess;
	}
	
	private String sortMap(Map<String, Double> inputMap) {
		List<Map.Entry<String, Double>> list = 
			new LinkedList<Map.Entry<String, Double>>(inputMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> ob1,
                                           Map.Entry<String, Double> ob2) {
				return (ob1.getValue()).compareTo(ob2.getValue());
			}
		});
		log.info("Weight Analysis is in Process...");
		Map<String, Double> outputMap = new TreeMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> iter = list.iterator(); iter.hasNext();) {
			Map.Entry<String, Double> entry = iter.next();
			outputMap.put(entry.getKey(), entry.getValue());
		}
		log.info("Process is Selected.");
		return outputMap.keySet().toArray()[0].toString();
	}
}
