package uni_stuttgart.iaas.spi.cmp.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import uni_stuttgart.iaas.spi.cmp.interfaces.ISelectionManager;
import uni_stuttgart.iaas.spi.cmp.realizations.ProcessSelector;

/**
 * A helper class to {@link ProcessSelector} that realizes an algorithm by analyzing the weights attached.
 * It implements the {@link ISelectionManager} interface.
 * @author Debasis Kar
 */

public class WeightAnalyzer implements ISelectionManager{
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(WeightAnalyzer.class.getName());
	
	@Override
	public TProcessDefinition findRealizationProcess(List<TProcessDefinition> processDefinitionList){
		Set<String> processIds = new HashSet<String>();
		for(TProcessDefinition processDefinition : processDefinitionList){
			processIds.add(processDefinition.getId());
		}
		TProcessDefinition dispatchedProcess = new TProcessDefinition();
		Map<String, Double> weightList = new TreeMap<String, Double>();
		//Ensure the list of process definition not to be empty
		if(!processDefinitionList.isEmpty()){
			switch(processDefinitionList.size()){
				case 1: dispatchedProcess = processDefinitionList.iterator().next();
						break;
				default: 
						for(String processIdentifier : processIds){
							//Prepare a table that contains process IDs and respective weights attached
							for(TProcessDefinition processDefinition : processDefinitionList){
									String processId = processDefinition.getId();
									if(processIdentifier.equals(processId)){
										NodeList nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
										for(int count=0; count < nodeList.getLength(); count++){
											if(nodeList.item(count).getNodeName().equals(CESConfigurations.REPOSITORY_FIELD_WEIGHT)){
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
						for(TProcessDefinition processDef : processDefinitionList){
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
