package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Node;

import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

public class ProcessOptimizer implements IProcessOptimizer {
	private File processRepository;
	private String processId;
	private boolean optimizerRunStatus;
	private TProcessDefinition goalProcessDefinition;
	private static final Logger log = Logger.getLogger(ProcessOptimizer.class.getName());
	
	public ProcessOptimizer(){
		this.processRepository = null;
		this.processId = null;
		this.goalProcessDefinition = null;
		this.optimizerRunStatus = false;
	}
	
	public ProcessOptimizer(String processIdentifier){
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.processId = processIdentifier;
		try {
			log.info("Optimization is being Carried Out...");
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(this.processRepository);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			int localIndex = 0;
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				if(processDefinition.getId().equals(this.processId)){
					break;
				}
				localIndex++;
			}
			log.info("Process Found for Optimization...");
			this.goalProcessDefinition = processSet.getProcessDefinition().get(localIndex);
			this.optimizerRunStatus = this.optimizeProcess(this.goalProcessDefinition);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred at Line in Process Optimizer!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line in Process Optimizer!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Process Optimizer!\n" + e.getMessage());
		} finally{
			if(this.optimizerRunStatus)
				log.info("Process Optimization Is Complete...");
			else
				log.warning("Process Optimization Failed or Strategy Not Found in Repository!");
		}
	}
	
	@Override
	public boolean optimizeProcess(TProcessDefinition processDefinition) {
		Node nodeManu = (Node) processDefinition.getProcessContent().getAny();
		String optimizerModel = nodeManu.getChildNodes().item(2).getTextContent();
		//Start Deployment Code for Optimization
		log.info(optimizerModel + " Will Be Executed");
		//End Deployment Code for Optimization
		return true;
	}

}
