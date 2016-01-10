package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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

import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

public class ContextAnalyzer implements IContextAnalyzer {
	
	private File contextRepository;
	private File processRepository;
	private Map<String, Boolean> initialContextAnalysisTable;
	private Map<String, Boolean> finalContextAnalysisTable;
	private Set<String> contextAnalysisProcessList;
	private static final Logger log = Logger.getLogger(ContextAnalyzer.class.getName());

	public ContextAnalyzer(){
		this.contextRepository = new File(ContextConfig.CONTEXT_REPOSITORY);
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.initialContextAnalysisTable = new TreeMap<String, Boolean>();
		this.finalContextAnalysisTable = new TreeMap<String, Boolean>();
		this.contextAnalysisProcessList = new TreeSet<String>();
		log.info("Deserializing the ProcessRepository.xml for Context Analysis.");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(this.processRepository);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			this.finalContextAnalysisTable = this.analyzeContext(processSet);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred in Context Analyzer!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Context Analyzer!");
		} catch(Exception e){
			log.severe("Unknown Exception has occurred in Context Analyzer!\n" + e.getMessage());
	    } finally{
			log.info("Context Matching Process Is Being Done...");
		}
	}
	
	@Override
	public Map<String, Boolean> analyzeContext(TProcessDefinitions processSet) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(this.contextRepository);
			doc.getDocumentElement().normalize();
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				if(processDefinition.getTargetNamespace().equals(ContextConfig.CONTEXT_NAMESPACE)){
					List<TContext> expressionList = processDefinition.getInitialContexts().getContext();
					for(TContext contextExpression : expressionList){
						TContent tContent = contextExpression.getContextDefinition().get(0).
								getDefinitionContent();
						Node nodeManu = (Node)tContent.getAny();
						String xpathQuery = nodeManu.getFirstChild().getTextContent();
						if(contextExpression.getContextDefinition().get(0).getDefinitionLanguage().
								equals(ContextConfig.XPATH_NAMESPACE)){
							XPathFactory xPathfactory = XPathFactory.newInstance();
							XPath xpath = xPathfactory.newXPath();
							XPathExpression expr = xpath.compile(xpathQuery);
							NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
							int noOfPredicates = xpathQuery.trim().split("\\|").length;
							if(nl.getLength() == noOfPredicates) {
								this.initialContextAnalysisTable.put(contextExpression.getName(), true);
							}
							else {
								this.initialContextAnalysisTable.put(contextExpression.getName(), false);
							}
						}
					}
				}
			}
			log.info(this.initialContextAnalysisTable.toString());
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				if(processDefinition.getTargetNamespace().equals(ContextConfig.CONTEXT_NAMESPACE)){
					String processId = processDefinition.getId();
					boolean result = false;
					List<TContext> expressionList = processDefinition.getInitialContexts().getContext();
					for(TContext contextExpression : expressionList){
						String expressionId = contextExpression.getName();
						result = result | this.initialContextAnalysisTable.get(expressionId).booleanValue();
					}
					this.finalContextAnalysisTable.put(processId, result);
				}
			}
			log.info(this.finalContextAnalysisTable.toString());
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Context Analyzer!");
		} catch (SAXException e) {
			log.severe("SAXException has occurred in Context Analyzer!");
		} catch (IOException e) {
			log.severe("IOException has occurred in Context Analyzer!");
		} catch (XPathExpressionException e) {
			log.severe("XPathExpressionException has occurred in Context Analyzer!");
		} catch (ParserConfigurationException e) {
			log.severe("ParserConfigurationException has occurred in Context Analyzer!");
		} catch(Exception e){
			log.severe("Unknown Exception has occurred in Context Analyzer!!\n" + e.getMessage());
	    } finally{
			log.info("Context Analysis is Completed.");
		}
		return this.finalContextAnalysisTable;
	}
	
	@Override
	public Set<String> getProcessListOfContextAnalyzer(Map<String, Boolean> finalContextAnalysisTable) {
		Iterator<Map.Entry<String, Boolean>> contextIterator = finalContextAnalysisTable.entrySet().iterator();
		log.info("Final List of Processes is Being Generated.");
		while (contextIterator.hasNext()) {
			Map.Entry<String, Boolean> entry = contextIterator.next();
			if (entry.getValue()) {
				this.contextAnalysisProcessList.add(entry.getKey());
			}
		}
		log.info("Process List is Available for Intention Analyzer.");
		log.info("Context Matching Processes: "+ this.contextAnalysisProcessList);
		return this.contextAnalysisProcessList;
	}
	
	public Map<String, Boolean> getFinalContextAnalysisTable() {
		return this.finalContextAnalysisTable;
	}

	public Set<String> getcontextAnalysisProcessList() {
		return this.contextAnalysisProcessList;
	}
}