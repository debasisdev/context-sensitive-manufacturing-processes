package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayInputStream;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
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
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessEliminator;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessRepository;

/**
 * A Demo Implementation Class that Implements IProcessEliminator, IDataRepository and ICamelSerializer.
 * This module sends Analyzes the received Context data by validating them with some predefined rules stored
 * in a Domain Know-How Repository, i.e., a Process Repository.
 * @author Debasis Kar
 */

public class ContextAnalyzer implements IProcessEliminator, ICamelSerializer, IProcessRepository {
	
	private TProcessDefinitions contextAnalysisPassedProcesses;
	private TTaskCESDefinition cesDefinition;
	private TContexts contextSet;
	private static final Logger log = Logger.getLogger(ContextAnalyzer.class.getName());
	
	public ContextAnalyzer(){
		this.contextAnalysisPassedProcesses = null;
		this.cesDefinition = null;
		this.contextSet = null;
	}

	public ContextAnalyzer(TTaskCESDefinition cesDefinition){
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.contextAnalysisPassedProcesses = ipsmMaker.createTProcessDefinitions();
		this.contextSet = ipsmMaker.createTContexts();
		this.cesDefinition = cesDefinition;
		log.info("Deserializing the ProcessRepository.xml for Context Analysis.");
	}
	
	@Override
	public TProcessDefinitions eliminate(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition) {
		Map<String, Boolean> initialContextAnalysisTable = new TreeMap<String, Boolean>();
		Map<String, Boolean> finalContextAnalysisTable = new TreeMap<String, Boolean>();
		try {
			ObjectFactory ipsmMaker = new ObjectFactory();
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<TContexts> conSet = ipsmMaker.createContextSet(this.contextSet);
			File file = File.createTempFile("ContextData", ".xml", new File("src/main/resources/diagrams/"));
			jaxbMarshaller.marshal(conSet, file);
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
	        
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
			file.delete();
		} catch (NullPointerException e) {
			log.severe("CONAN13: NullPointerException has Occurred.");
		} catch (IOException e) {
			log.severe("CONAN12: IOException has Occurred.");
		} catch (XPathExpressionException e) {
			log.severe("CONAN11: XPathExpressionException has Occurred.");
		} catch(Exception e){
			log.severe("CONAN10: Unknown Exception has Occurred - " + e);
	    } finally{
			log.info(this.contextAnalysisPassedProcesses.getProcessDefinition().size() + 
						" Processes Passed Context Analysis.");
		}
		return this.contextAnalysisPassedProcesses;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			TProcessDefinitions processSet = this.getProcessRepository(this.cesDefinition);
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TContexts contextSet = (TContexts) rootElement.getValue();
			this.contextSet = contextSet;
			this.contextAnalysisPassedProcesses = this.eliminate(processSet, this.cesDefinition);
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TProcessDefinitions> processDefSet = ipsmMaker.createProcessDefinitions(
	        										this.contextAnalysisPassedProcesses);
			jaxbMarshaller.marshal(processDefSet, outputStream);
		} catch (JAXBException e) {
			log.severe("CONAN02: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("CONAN01: NullPointerException has Occurred.");
		} catch (Exception e){
			log.severe("CONAN00: Unknown Exception has Occurred - " + e);
	    } finally{
			log.info("Context Matching Process is Completed.");
		}
		return outputStream.toByteArray();
	}
	
	@Override
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition) {
		TProcessDefinitions processDefinitions = null;
		String fileName = cesDefinition.getDomainKnowHowRepository() + "\\ProcessRepository.xml";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new File(fileName));
			processDefinitions = (TProcessDefinitions) rootElement.getValue();
		} catch (JAXBException e) {
			log.severe("CONAN22: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("CONAN21: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("CONAN20: Unknown Exception has Occurred - " + e);
		}
		return processDefinitions;
	}
}