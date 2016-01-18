package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
import unistuttgart.iaas.spi.cmprocess.interfaces.IDataRepository;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessEliminator;

public class ContextAnalyzer implements IProcessEliminator, IDataRepository {
	
	private List<TProcessDefinition> contextAnalysisProcessList;
	private static final Logger log = Logger.getLogger(ContextAnalyzer.class.getName());
	
	public ContextAnalyzer(){
		this.contextAnalysisProcessList = null;
	}

	public ContextAnalyzer(TTaskCESDefinition cesDefinition){
		this.contextAnalysisProcessList = new LinkedList<TProcessDefinition>();
		log.info("Deserializing the ProcessRepository.xml for Context Analysis.");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(this.getProcessRepository(cesDefinition));
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			this.contextAnalysisProcessList = this.eliminate(processSet, cesDefinition);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred in Context Analyzer!");
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Context Analyzer!");
		} catch(Exception e){
			log.severe("Unknown Exception has occurred in Context Analyzer!\n" + e.getMessage());
	    } finally{
			log.info("Context Matching Process is Completed.");
		}
	}
	
	@Override
	public List<TProcessDefinition> eliminate(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition) {
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
						this.contextAnalysisProcessList.add(processDefinition);
					}
				}
			}
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Context Analyzer!!");
		} catch (SAXException e) {
			log.severe("SAXException has occurred in Context Analyzer!!");
		} catch (IOException e) {
			log.severe("IOException has occurred in Context Analyzer!!");
		} catch (XPathExpressionException e) {
			log.severe("XPathExpressionException has occurred in Context Analyzer!!");
		} catch (ParserConfigurationException e) {
			log.severe("ParserConfigurationException has occurred in Context Analyzer!!");
		} catch(Exception e){
			log.severe("Unknown Exception has occurred in Context Analyzer!!\n" + e.getMessage());
	    } finally{
			log.info("Final Context Analysis Report: " + this.contextAnalysisProcessList);
		}
		return this.contextAnalysisProcessList;
	}

	@Override
	public List<TProcessDefinition> getProcessListOfAnalyzer() {
		return this.contextAnalysisProcessList;
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
				log.severe("IOException has occurred in Context Analyzer!!!");
			}
		}
		return new File(fileName);
	}

	@Override
	public File getProcessRepository(TTaskCESDefinition cesDefinition) {
		return new File(cesDefinition.getProcessRepository());
	}
	
}