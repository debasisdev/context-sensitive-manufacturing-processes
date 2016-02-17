package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessEliminator;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/**
 * A generic class that implements {@link IProcessEliminator}, {@link IDataSerializer}, and {@link Processor}.
 * This module analyzes the received {@link TContexts} by validating them with some predefined rules stored
 * in a Domain Know-How Repository, i.e., a Process Repository.
 * @author Debasis Kar
 */

public class ContextAnalyzer implements IProcessEliminator, IDataSerializer, Processor {
	
	/**Variable to store the {@link TProcessDefinitions} that pass context analysis 
	 * @author Debasis Kar
	 * */
	private TProcessDefinitions contextAnalysisPassedProcesses;
	
	/**Variable to store {@link TTaskCESDefinition} 
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Variable to store initial context data {@link TContexts}
	 * @author Debasis Kar
	 * */
	private TContexts contextSet;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(ContextAnalyzer.class);
	
	/**Default constructor of {@link ContextAnalyzer}
	 * @author Debasis Kar
	 * */
	public ContextAnalyzer(){
		this.contextAnalysisPassedProcesses = null;
		this.cesDefinition = null;
		this.contextSet = null;
	}

	/**Parameterized constructor of {@link ContextAnalyzer}
	 * @author Debasis Kar
	 * @param cesDefinition
	 * */
	public ContextAnalyzer(TTaskCESDefinition cesDefinition){
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.contextAnalysisPassedProcesses = ipsmMaker.createTProcessDefinitions();
		this.contextSet = ipsmMaker.createTContexts();
		this.cesDefinition = cesDefinition;
		log.info("Deserializing the ProcessRepository.xml for Context Analysis.");
	}
	
	@Override
	public TProcessDefinitions eliminate(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition) {
		//Table to store analysis results
		Map<String, Boolean> initialContextAnalysisTable = new TreeMap<String, Boolean>();
		Map<String, Boolean> finalContextAnalysisTable = new TreeMap<String, Boolean>();
		try {
			//JAXB implementation for serializing the context data received from Query Manager
			ObjectFactory ipsmMaker = new ObjectFactory();
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class, de.uni_stuttgart.iaas.cmp.v0.ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<TContexts> conSet = ipsmMaker.createContextSet(this.contextSet);
			//Create a temporary file
			File file = File.createTempFile(CESExecutorConfig.CONTEXTDATA_FILENAME, CESExecutorConfig.CONTEXTDATA_FILETYPE, new File(CESExecutorConfig.CONTEXTDATA_FILEPATH));
			jaxbMarshaller.marshal(conSet, file);
			//Create Document Builder for parsing through the temporary file created
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
	        //Scan each process definition and validate context rules
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				//Some process definitions might not contain context rules at all
				if(processDefinition.getInitialContexts() != null){
					List<TContext> expressionList = processDefinition.getInitialContexts().getContext();
					//Fetch context rules
					for(TContext contextExpression : expressionList){
						TContent tContent = contextExpression.getContextDefinition().get(0).
								getDefinitionContent();
						String applicationNamespace = contextExpression.getTargetNamespace();
						Node nodeManu = (Node)tContent.getAny();
						String xpathQuery = nodeManu.getFirstChild().getTextContent();
						//Ensure the context name-space of the application before validation
						if(applicationNamespace.equals(CESExecutorConfig.CONTEXT_NAMESPACE)){
							//Ensure whether the rules are defined in XPATH or any other language
							if(contextExpression.getContextDefinition().get(0).getDefinitionLanguage().equals(CESExecutorConfig.XPATH_NAMESPACE)){
								XPathFactory xPathfactory = XPathFactory.newInstance();
								XPath xpath = xPathfactory.newXPath();
								XPathExpression expr = xpath.compile(xpathQuery);
								//Analyze XPATH expressions
								NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
								int noOfPredicates = xpathQuery.trim().split(CESExecutorConfig.SPLIT_EXPRESSION).length;
								if(nl.getLength() == noOfPredicates) {
									initialContextAnalysisTable.put(contextExpression.getName(), true);
								}
								else {
									initialContextAnalysisTable.put(contextExpression.getName(), false);
								}
							}
						}
					}
				}
			}
			log.info("Phase-1 Context Analysis Report: " + initialContextAnalysisTable.toString());
			//For each process definition consolidate the context rules and process the final result 
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				//Fetch process ID
				String processId = processDefinition.getId();
				boolean result = false;
				if(processDefinition.getInitialContexts() != null){
					List<TContext> expressionList = processDefinition.getInitialContexts().getContext();
					//Decide by merging all context rules for a process definition
					for(TContext contextExpression : expressionList){
						String expressionId = contextExpression.getName();
						result = result | initialContextAnalysisTable.get(expressionId).booleanValue();
					}
					finalContextAnalysisTable.put(processId, result);
				}
			}
			log.info("Phase-2 Context Analysis Report: " + finalContextAnalysisTable.toString());
			//Process definitions those passes the phase-2 analysis should be added to the result
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				String processId = processDefinition.getId();
				if(processDefinition.getInitialContexts() != null){
					boolean result = finalContextAnalysisTable.get(processId);
					if(result){
						//Add to the final result that would be sent to Intention Analyzer
						this.contextAnalysisPassedProcesses.getProcessDefinition().add(processDefinition);
					}
				}
			}
			//Delete the temporary file created
			file.deleteOnExit();
		} catch (NullPointerException e) {
			log.error("CONAN13: NullPointerException has Occurred.");
		} catch (IOException e) {
			log.error("CONAN12: IOException has Occurred.");
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			log.error("CONAN11: XPathExpressionException has Occurred.");
		} catch(Exception e){
			log.error("CONAN10: Unknown Exception has Occurred - " + e);
	    } finally{
			log.info(this.contextAnalysisPassedProcesses.getProcessDefinition().size() + " Processes Passed Context Analysis.");
		}
		return this.contextAnalysisPassedProcesses;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			//Get process repository know-how
			ProcessRepository processRepository = new ProcessRepository();
			TProcessDefinitions processSet = processRepository.getProcessRepository(this.cesDefinition);
			//JAXB implementation for de-serializing the context data received from Query Manager
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TContexts contextSet = (TContexts) rootElement.getValue();
			//Store this as TContexts
			this.setContextSet(contextSet);
			//Perform context analysis
			this.contextAnalysisPassedProcesses = this.eliminate(processSet, this.cesDefinition);
			//JAXB implementation for serializing the Context Analyzer output into byte array for message exchange
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TProcessDefinitions> processDefSet = ipsmMaker.createProcessDefinitions(this.contextAnalysisPassedProcesses);
			jaxbMarshaller.marshal(processDefSet, outputStream);
		} catch (JAXBException e) {
			log.error("CONAN02: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.error("CONAN01: NullPointerException has Occurred.");
		} catch (Exception e){
			log.error("CONAN00: Unknown Exception has Occurred - " + e);
	    } finally{
			log.info("Context Matching Process is Completed.");
		}
		return outputStream.toByteArray();
	}
	
	/**
	 * This is a setter method to set the {@link TContexts} received from the {@link QueryManager}.
	 * @author Debasis Kar
	 * @param contextSet
	 * @return void
	 */
	public void setContextSet(TContexts contextSet) {
		this.contextSet = contextSet;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		//Send output of Context Analyzer to Intention Analyzer with relevant header information
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESExecutorConfig.RABBIT_SEND_SIGNAL);
        headerData.put(CESExecutorConfig.RABBIT_STATUS, CESExecutorConfig.RABBIT_MSG_CONTEXTANALYZER);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);
	}
}