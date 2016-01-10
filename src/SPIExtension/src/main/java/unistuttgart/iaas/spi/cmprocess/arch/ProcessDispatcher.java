package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

public class ProcessDispatcher implements IProcessDispatcher {
	private File processRepository;
	private String dispatchedProcessName;
	private Set<String> processListReceived;
	private static final Logger log = Logger.getLogger(ProcessDispatcher.class.getName());
	
	public ProcessDispatcher() {
		this.processRepository = null;
		this.dispatchedProcessName = null;
	}
	
	public ProcessDispatcher(Set<String> processesFromIntentionAnalyzer) {
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.processListReceived = processesFromIntentionAnalyzer;
		log.info("Process Dispatcher Is About to Begin...");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(this.processRepository);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			this.dispatchedProcessName = this.dispatchProcess(processSet);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred at Line in Process Dispatcher!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line in Process Dispatcher!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Process Dispatcher!\n" + e.getMessage());
			e.printStackTrace();
		} finally{
			log.info("Process To Be Sent to Process Optimizer: [" + this.dispatchedProcessName + "]");
			log.info("Process is Dispatched.");
		}
	}
	
	@Override
	public String dispatchProcess(TProcessDefinitions processSet) {
		Map<String, Double> weightList = new TreeMap<String, Double>();
		log.info("Dispatching is being done...");
		if(!this.processListReceived.isEmpty()){
			switch(this.processListReceived.size()){
				case 1: this.dispatchedProcessName = this.processListReceived.iterator().next();
						break;
				default: 
						for(String processIdentifier : this.processListReceived){
							for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
									String processId = processDefinition.getId();
									if(processIdentifier.equals(processId)){
										Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
										String processWeight = nodeManu.getChildNodes().item(3).getTextContent();
										weightList.put(processId,Double.parseDouble(processWeight));
									}
							}
						}
						this.dispatchedProcessName = sortMap(weightList);
						break;
			}
		}
		log.info(weightList.toString());
		return this.dispatchedProcessName;
	}
	
	private static String sortMap(Map<String, Double> inputMap) {
		List<Map.Entry<String, Double>> list = 
			new LinkedList<Map.Entry<String, Double>>(inputMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> ob1,
                                           Map.Entry<String, Double> ob2) {
				return (ob1.getValue()).compareTo(ob2.getValue());
			}
		});
		Map<String, Double> outputMap = new TreeMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> iter = list.iterator(); iter.hasNext();) {
			Map.Entry<String, Double> entry = iter.next();
			outputMap.put(entry.getKey(), entry.getValue());
		}
		return outputMap.keySet().toArray()[0].toString();
	}

	public String getDispatchedProcessName() {
		return this.dispatchedProcessName;
	}

}
