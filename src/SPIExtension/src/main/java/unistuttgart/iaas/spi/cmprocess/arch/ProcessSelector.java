package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import unistuttgart.iaas.spi.cmprocess.interfaces.IDataRepository;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessSelector;

public class ProcessSelector implements IProcessSelector, IDataRepository {
	private TProcessDefinition dispatchedProcess;
	private Set<String> processIds;
	private static final Logger log = Logger.getLogger(ProcessSelector.class.getName());
	
	public ProcessSelector() {
		this.dispatchedProcess = null;
	}
	
	public ProcessSelector(List<TProcessDefinition> conOutput, List<TProcessDefinition> intOutput, TTaskCESDefinition cesDefinition) {
		this.processIds = new HashSet<String>();
		List<TProcessDefinition> processList = new LinkedList<TProcessDefinition>();
		for(TProcessDefinition processDef : conOutput){
			this.processIds.add(processDef.getId());
		}
		for(TProcessDefinition processDef : intOutput){
			this.processIds.add(processDef.getId());
		}
		log.info("Process Selector Is About to Begin...");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(this.getProcessRepository(cesDefinition));
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			for(String processId : this.processIds){
				for(TProcessDefinition process : processSet.getProcessDefinition()){
					if(processId.equals(process.getId())){
						processList.add(process);
					}
				}
			}
			this.dispatchedProcess = this.selectProcess(processList, cesDefinition);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred at Line in Process Selector!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line in Process Selector!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Process Selector!\n" + e.getMessage());
			e.printStackTrace();
		} finally{
			log.info("Process To Be Sent to Process Optimizer: [" + this.dispatchedProcess.getId() + "]");
			log.info("Process is Selected.");
		}
	}
	
	@Override
	public TProcessDefinition selectProcess(List<TProcessDefinition> processDefinitionList, TTaskCESDefinition cesDefinition){
		Map<String, Double> weightList = new TreeMap<String, Double>();
		log.info("Selection is being done...");
		log.info("Strategy analysis is being done.");
		if(cesDefinition.getIntention().getSubIntentions().get(0).getSubIntentionRelations()
				.equals("http://www.uni-stuttgart.de/ipsm/intention/selections/weight-based")){
			if(!processDefinitionList.isEmpty()){
				switch(processDefinitionList.size()){
					case 1: this.dispatchedProcess = processDefinitionList.iterator().next();
							break;
					default: 
							for(String processIdentifier : this.processIds){
								for(TProcessDefinition processDefinition : processDefinitionList){
										String processId = processDefinition.getId();
										if(processIdentifier.equals(processId)){
											Node nodeManuf = (Node) processDefinition.getProcessContent().getAny();
											String processWeight = nodeManuf.getChildNodes().item(3).getTextContent();
											weightList.put(processId,Double.parseDouble(processWeight));
										}
								}
							}
							String dispatchedProcessName = sortMap(weightList);
							for(TProcessDefinition processDef : processDefinitionList){
								if(processDef.getId().equals(dispatchedProcessName)){
									this.dispatchedProcess = processDef;
								}
							}
							break;
				}
			}
		}
		else{
			log.info("A process will be randomly selected.");
			if(!processDefinitionList.isEmpty()){
				Random randomNumberGenerator = new Random();
				int max = processDefinitionList.size();
				int min = 1;
				int randInt = randomNumberGenerator.nextInt((max - min) + 1);
				this.dispatchedProcess = processDefinitionList.get(randInt);
			}
		}
		return this.dispatchedProcess;
	}
	
	@Override
	public TProcessDefinition getDispatchedProcess() {
		return this.dispatchedProcess;
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
		log.info("Weight Analysis is in Process...");
		Map<String, Double> outputMap = new TreeMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> iter = list.iterator(); iter.hasNext();) {
			Map.Entry<String, Double> entry = iter.next();
			outputMap.put(entry.getKey(), entry.getValue());
		}
		return outputMap.keySet().toArray()[0].toString();
	}
	

	@Override
	public File getContextRepository() {
		Properties propertyFile = new Properties();
    	InputStream inputReader = this.getClass().getClassLoader().getResourceAsStream("config.properties");
    	String fileName = null;
		if(inputReader != null){
	        try {
				propertyFile.load(inputReader);
				fileName = propertyFile.getProperty("CONTEXT_REPOSITORY");
		        inputReader.close();
			} catch (IOException e) {
				log.severe("IOException has occurred in Query Manager!");
			}
		}
		return new File(fileName);
	}

	@Override
	public File getProcessRepository(TTaskCESDefinition cesDefinition) {
		return new File(cesDefinition.getProcessRepository());
	}
}
