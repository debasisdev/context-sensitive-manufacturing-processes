package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import unistuttgart.iaas.spi.cmprocess.interfaces.ICamelSerializer;
import unistuttgart.iaas.spi.cmprocess.interfaces.IDataRepository;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessEliminator;

/**
 * A Demo Implementation Class that Implements IProcessEliminator, IDataRepository and ICamelSerializer.
 * This module sends Analyzes the received Context data by validating them with some predefined rules stored
 * in a Domain Know-How Repository, i.e., a Process Repository.
 * @author Debasis Kar
 */

public class ContextAnalyzer implements IProcessEliminator, IDataRepository, ICamelSerializer {
	
	private TProcessDefinitions contextAnalysisPassedProcesses;
	private TTaskCESDefinition cesDefinition;
	private static final Logger log = Logger.getLogger(ContextAnalyzer.class.getName());
	
	public ContextAnalyzer(){
		this.contextAnalysisPassedProcesses = null;
		this.cesDefinition = null;
	}

	public ContextAnalyzer(TTaskCESDefinition cesDefinition){
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.contextAnalysisPassedProcesses = ipsmMaker.createTProcessDefinitions();
		this.cesDefinition = cesDefinition;
		log.info("Deserializing the ProcessRepository.xml for Context Analysis.");
	}
	
	@Override
	public TProcessDefinitions eliminate(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition) {
		Map<String, Boolean> initialContextAnalysisTable = new TreeMap<String, Boolean>();
		Map<String, Boolean> finalContextAnalysisTable = new TreeMap<String, Boolean>();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(this.getContextRepository());
			doc.getDocumentElement().normalize();
			Properties propertyFile = new Properties();
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
		    	InputStream inputReader = this.getClass().getClassLoader().getResourceAsStream("config.properties");
		    	if(inputReader != null){
		    		propertyFile.load(inputReader);
					if(processDefinition.getTargetNamespace().equals(propertyFile.getProperty("CONTEXT_NAMESPACE"))){
						List<TContext> expressionList = processDefinition.getInitialContexts().getContext();
						for(TContext contextExpression : expressionList){
							TContent tContent = contextExpression.getContextDefinition().get(0).
									getDefinitionContent();
							Node nodeManu = (Node)tContent.getAny();
							String xpathQuery = nodeManu.getFirstChild().getTextContent();
							if(contextExpression.getContextDefinition().get(0).getDefinitionLanguage().
									equals(propertyFile.getProperty("XPATH_NAMESPACE"))){
								XPathFactory xPathfactory = XPathFactory.newInstance();
								XPath xpath = xPathfactory.newXPath();
								XPathExpression expr = xpath.compile(xpathQuery);
								NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
								int noOfPredicates = xpathQuery.trim().split("\\|").length;
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
		    	inputReader.close();
			}
			log.info("Phase-1 Context Analysis Report: " + initialContextAnalysisTable.toString());
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				if(processDefinition.getTargetNamespace().equals(propertyFile.getProperty("CONTEXT_NAMESPACE"))){
					String processId = processDefinition.getId();
					boolean result = false;
					List<TContext> expressionList = processDefinition.getInitialContexts().getContext();
					for(TContext contextExpression : expressionList){
						String expressionId = contextExpression.getName();
						result = result | initialContextAnalysisTable.get(expressionId).booleanValue();
					}
					finalContextAnalysisTable.put(processId, result);
				}
			}
			log.info("Phase-2 Context Analysis Report: " + finalContextAnalysisTable.toString());
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				if(processDefinition.getTargetNamespace().equals(propertyFile.getProperty("CONTEXT_NAMESPACE"))){
					String processId = processDefinition.getId();
					boolean result = finalContextAnalysisTable.get(processId);
					if(result){
						this.contextAnalysisPassedProcesses.getProcessDefinition().add(processDefinition);
					}
				}
			}
		} catch (NullPointerException e) {
			log.severe("Code - CONAN25: NullPointerException has Occurred.");
		} catch (SAXException e) {
			log.severe("Code - CONAN24: SAXException has Occurred.");
		} catch (IOException e) {
			log.severe("Code - CONAN23: IOException has Occurred.");
		} catch (XPathExpressionException e) {
			log.severe("Code - CONAN22: XPathExpressionException has Occurred.");
		} catch (ParserConfigurationException e) {
			log.severe("Code - CONAN21: ParserConfigurationException has Occurred.");
		} catch(Exception e){
			log.severe("Code - CONAN20: Unknown Exception has Occurred.");
	    } finally{
			log.info(this.contextAnalysisPassedProcesses.getProcessDefinition().size() + 
						" Processes Passed Context Analysis.");
		}
		return this.contextAnalysisPassedProcesses;
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
				log.severe("Code - CONAN11: IOException has Occurred.");
			} catch (Exception e){
				log.severe("Code - CONAN10: Unknown Exception has Occurred.");
		    } 
		}
		return new File(fileName);
	}

	@Override
	public File getProcessRepository(TTaskCESDefinition cesDefinition) {
		return new File(cesDefinition.getProcessRepository());
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(
												this.getProcessRepository(this.cesDefinition));
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			if(Boolean.parseBoolean(exchange.getIn().getBody().toString())){
				this.contextAnalysisPassedProcesses = this.eliminate(processSet, this.cesDefinition);
			}
			else{
				this.contextAnalysisPassedProcesses = processSet;
			}
		} catch (JAXBException e) {
			log.severe("Code - CONAN02: JAXBException has Occurred.");
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.severe("Code - CONAN01: NullPointerException has Occurred.");
		} catch (Exception e){
			log.severe("Code - CONAN00: Unknown Exception has Occurred.");
	    } finally{
			log.info("Context Matching Process is Completed.");
		}
		return this.getSerializedProcessListOfAnalyzer();
	}
	
	public byte[] getSerializedProcessListOfAnalyzer() {
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TProcessDefinitions> processDefSet = ipsmMaker.createProcessDefinitions(
	        										this.contextAnalysisPassedProcesses);
			jaxbMarshaller.marshal(processDefSet, outputStream);
		} catch(NullPointerException e){
			log.severe("Code - CONAN32: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.severe("Code - CONAN31: JAXBException has Occurred.");
		} catch (Exception e) {
			log.severe("Code - CONAN30: Unknown Exception has Occurred.");
	    } 
		return outputStream.toByteArray();
	}
}