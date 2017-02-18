package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcesses;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessRepository;
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
 * A generic class that de-serializes the Domain Know-how repository and returns the {@link TRealizationProcesss} for
 * other modules to perform analysis and deployment of business processes.
 * @author Debasis Kar
 */

public class ProcessRepository implements IProcessRepository {
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(ProcessRepository.class);

	@Override
	public TRealizationProcesses getProcessRepository(TTaskCESDefinition cesDefinition) {
		TRealizationProcesses processDefinitions = null;
		String repositoryType = cesDefinition.getDomainKnowHowRepositoryType();
		String fileName = null;
		//Ensure the type of Domain Know-how repository from the CES Definition
		if(repositoryType.equals(CESExecutorConfig.XML_EXTENSION)){
			fileName = cesDefinition.getDomainKnowHowRepository();
		}
		try {
			//JAXB implementation for de-serializing the Process Repository into TRealizationProcesss
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new File(fileName));
			processDefinitions = (TRealizationProcesses) rootElement.getValue();
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
