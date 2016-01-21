package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessSelector;

public class ProcessSelector implements IProcessSelector, ICamelSerializer {
	private TProcessDefinition dispatchedProcess;
	private TTaskCESDefinition cesDefinition;
	private Set<String> processIds;
	private static final Logger log = Logger.getLogger(ProcessSelector.class.getName());
	
	public ProcessSelector() {
		this.dispatchedProcess = null;
		this.cesDefinition = null;
		this.processIds = null;
	}
	
	public ProcessSelector(TTaskCESDefinition cesDefinition) {
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.dispatchedProcess = ipsmMaker.createTProcessDefinition();
		this.processIds = new HashSet<String>();
		this.cesDefinition = cesDefinition;
		log.info("Process Selector Is About to Begin...");
	}
	
	@Override
	public TProcessDefinition selectProcess(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition){
		Map<String, Double> weightList = new TreeMap<String, Double>();
		log.info("Selection by Strategy analysis is being done.");
		try {
			Properties propertyFile = new Properties();
			InputStream inputReader = this.getClass().getClassLoader().getResourceAsStream("config.properties");
			propertyFile.load(inputReader);
			
			List<TProcessDefinition> processDefinitionList = new LinkedList<TProcessDefinition>();
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				processDefinitionList.add(processDefinition);
				this.processIds.add(processDefinition.getId());
			}
			if(cesDefinition.getIntention().getSubIntentions().get(0).getSubIntentionRelations()
					.equals(propertyFile.getProperty("WEIGHT_NAMESPACE"))){
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
								String dispatchedProcessName = this.sortMap(weightList);
								for(TProcessDefinition processDef : processDefinitionList){
									if(processDef.getId().equals(dispatchedProcessName)){
										this.dispatchedProcess = processDef;
									}
								}
								break;
					}
				}
			}
			else {
				log.info("A process will be randomly selected.");
				if(!processDefinitionList.isEmpty()){
					Random randomNumberGenerator = new Random();
					int max = processDefinitionList.size();
					int min = 1;
					int randInt = randomNumberGenerator.nextInt((max - min) + 1);
					this.dispatchedProcess = processDefinitionList.get(randInt);
				}
			}
			log.info(this.dispatchedProcess.getId() + " Is Selected for the Realization of Business Ojective.");
		} catch (IOException e) {
			log.severe("PROSE12: IOException has Occurred.");
		} catch (NullPointerException e){
			log.severe("PROSE11: NullPointerException has Occurred.");
		} catch (Exception e){
			log.severe("PROSE10: Unknown Exception has Occurred - " + e);
		}
		return this.dispatchedProcess;
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
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange){
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			this.dispatchedProcess = this.selectProcess(processSet, this.cesDefinition);
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TProcessDefinition> processDef = ipsmMaker.createProcessDefinition(this.dispatchedProcess);
			jaxbMarshaller.marshal(processDef, outputStream);
		} catch (NullPointerException e) {
			log.severe("PROSE02: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.severe("PROSE01: JAXBException has Occurred.");
		} catch (Exception e) {
			log.severe("PROSE00: Unknown Exception has Occurred - " + e);
	    } finally {
			log.info("Intention Analysis is Completed.");
		}
		return outputStream.toByteArray();
	}
	
}
