package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessRepository;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/**
 * A generic class that de-serializes the Domain Know-how repository and returns the {@link TProcessDefinitions} for
 * other modules to perform analysis and deployment of business processes.
 * @author Debasis Kar
 */

public class ProcessRepository implements IProcessRepository {
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(ProcessRepository.class);

	@Override
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition) {
		TProcessDefinitions processDefinitions = null;
		String repositoryType = cesDefinition.getDomainKnowHowRepositoryType();
		String fileName = null;
		//Ensure the type of Domain Know-how repository from the CES Definition
		if(repositoryType.equals(CESExecutorConfig.XML_EXTENSION)){
			fileName = cesDefinition.getDomainKnowHowRepository();
		}
		try {
			//JAXB implementation for de-serializing the Process Repository into TProcessDefinitions
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new File(fileName));
			processDefinitions = (TProcessDefinitions) rootElement.getValue();
		} catch (JAXBException e) {
			log.error("PROREP02: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.error("PROREP01: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("PROREP00: Unknown Exception has Occurred - " + e);
		}
		return processDefinitions;
	}

}
