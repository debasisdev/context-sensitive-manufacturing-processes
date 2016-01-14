package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import unistuttgart.iaas.spi.cmprocess.interfaces.IDataRepository;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessEliminator;

public class IntentionAnalyzer implements IProcessEliminator, IDataRepository {
	private List<TProcessDefinition> intentionAnalysisProcessList;
	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public IntentionAnalyzer(){
		this.intentionAnalysisProcessList = null;
	}
	
	public IntentionAnalyzer(TTaskCESDefinition cesDefinition){
		this.intentionAnalysisProcessList = new LinkedList<TProcessDefinition>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(this.getProcessRepository(cesDefinition));
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			this.intentionAnalysisProcessList = this.eliminate(processSet, cesDefinition);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred in Intention Analyzer!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Intention Analyzer!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Intention Analyzer!\n" + e.getMessage());
		} finally{
			log.info("Intention Analysis is Completed.");
		}
	}
	
	@Override
	public List<TProcessDefinition> eliminate(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition){
		TIntention intention = cesDefinition.getIntention();
		String mainIntention = intention.getName();
		Set<String> subIntentions = new TreeSet<String>();
		for(TSubIntention subIntention : intention.getSubIntentions().get(0).getSubIntention()){
			subIntentions.add(subIntention.getName());
		}
		try {			
			log.info("Intention Analysis is Started by Deserializing the ProcessRepository.xml");
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				Set<String> extraIntentions = new TreeSet<String>();
				if(processDefinition.getTargetIntention().getName().equals(mainIntention)){
					List<TSubIntention> subIntentionList = processDefinition.
							getTargetIntention().getSubIntentions().get(0).getSubIntention();
					for(TSubIntention intent : subIntentionList){
						extraIntentions.add(intent.getName());
					}
					extraIntentions.retainAll(subIntentions);
				}
				if(extraIntentions.size()>0)
					this.intentionAnalysisProcessList.add(processDefinition);
			}
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Intention Analyzer!!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Intention Analyzer!!\n" + e.getMessage());
			e.printStackTrace();
		} finally{
			log.info("Final Intention Analysis Report: " + this.intentionAnalysisProcessList);
		}
		return this.intentionAnalysisProcessList;
	}

	@Override
	public List<TProcessDefinition> getProcessListOfAnalyzer() {
		return this.intentionAnalysisProcessList;
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
				log.severe("IOException has occurred in Intention Analyzer!");
			}
		}
		return new File(fileName);
	}

	@Override
	public File getProcessRepository(TTaskCESDefinition cesDefinition) {
		return new File(cesDefinition.getProcessRepository());
	}
}
