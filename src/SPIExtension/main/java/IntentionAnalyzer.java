package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import unistuttgart.iaas.spi.cmprocess.tcmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.tcmp.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.tcmp.TProcessDefinitions;

public class IntentionAnalyzer implements IIntentionAnalyzer {
	private File processRepository;
	private TIntention intention;
	private String mainIntention;
	private Set<String> subIntentions;
	private Set<String> intentionAnalysisProcessList;
	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public IntentionAnalyzer(){
		this.intention = null;
		this.processRepository = null;
		this.mainIntention = null;
		this.subIntentions = null;
		this.intentionAnalysisProcessList = null;
	}
	
	public IntentionAnalyzer(Set<String> processesFromContextAnalyzer, TIntention mainIntention){
		this.intention = mainIntention;
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.mainIntention = this.intention.getName();
		this.subIntentions = new TreeSet<String>();
		for(TIntention intention : this.intention.getSubIntentions().getIntention()){
			this.subIntentions.add(intention.getName());
		}
		this.intentionAnalysisProcessList = new TreeSet<String>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("unistuttgart.iaas.spi.cmprocess.tcmp");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			TProcessDefinitions processSet = (TProcessDefinitions) jaxbUnmarshaller.unmarshal(this.processRepository);
			this.intentionAnalysisProcessList = this.analyzeIntention(processSet, mainIntention);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Intention Analyzer!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Intention Analyzer!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + 
					" in Intention Analyzer!\n" + e.getMessage());
		} finally{
			log.info("Intention Analysis is Performed.");
		}
	}
	
	public Set<String> analyzeIntention(TProcessDefinitions processSet, TIntention mainIntention){
		try {			
			log.info("Intention Analysis is Started by Deserializing the ProcessRepository.xml");
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				String processId = processDefinition.getId();
				Set<String> extraIntentions = new TreeSet<String>();
				if(processDefinition.getTargetIntention().getName().equals(mainIntention.getName())){
					List<TIntention> subIntentionList = processDefinition.
							getTargetIntention().getSubIntentions().getIntention();
					for(TIntention intention : subIntentionList){
						extraIntentions.add(intention.getName());
					}
					extraIntentions.retainAll(this.subIntentions);
				}
				if(extraIntentions.size()>0)
					this.intentionAnalysisProcessList.add(processId);
			}
			log.info("Intention Matching Processes: "+ this.intentionAnalysisProcessList);
			log.info("Final List of Processes are Generated for Process Optimizer.");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + " in Intention Analyzer!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + 
					" in Intention Analyzer!\n" + e.getMessage());
		} finally{
			log.info("Intention Analysis is Performed.");
		}
		return this.intentionAnalysisProcessList;
	}

	public TIntention getIntention() {
		return intention;
	}

	public String getMainIntention() {
		return mainIntention;
	}

	public Set<String> getSubIntentions() {
		return subIntentions;
	}

	public Set<String> getProcessListOfIntentionAnalyzer(Set<String> processesFromContextAnalyzer) {
		this.intentionAnalysisProcessList.retainAll(processesFromContextAnalyzer);
		return this.intentionAnalysisProcessList;
	}
}
