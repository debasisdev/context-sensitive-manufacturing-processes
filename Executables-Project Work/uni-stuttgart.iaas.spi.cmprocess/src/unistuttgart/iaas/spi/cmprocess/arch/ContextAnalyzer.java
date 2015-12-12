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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import unistuttgart.iaas.spi.cmprocess.cmp.TContextExpression;
import unistuttgart.iaas.spi.cmprocess.cmp.TProcessDefinition;
import unistuttgart.iaas.spi.cmprocess.cmp.TProcessModel;

public class ContextAnalyzer implements IContextAnalyzer {
	
	private File contextRepository;
	private File processRepository;
	private Map<String, Boolean> initialContextAnalysisTable;
	private Map<String, Boolean> finalContextAnalysisTable;
	private Set<String> contextAnalysisProcessList;
	private static final Logger log = Logger.getLogger(ContextAnalyzer.class.getName());

	public ContextAnalyzer(){
		this.contextRepository = null;
		this.processRepository = null;
		this.initialContextAnalysisTable = null;
		this.finalContextAnalysisTable = null;
		this.contextAnalysisProcessList = null;
		this.analyzeContext();
		this.getProcessListForContextAnalysis();
	}
	
	public ContextAnalyzer(File contextRepository){
		this.contextRepository = contextRepository;
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.initialContextAnalysisTable = new TreeMap<String, Boolean>();
		this.finalContextAnalysisTable = new TreeMap<String, Boolean>();
		this.contextAnalysisProcessList = new TreeSet<String>();
		this.analyzeContext();
		this.getProcessListForContextAnalysis();
	}
	
	public void analyzeContext() {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(this.contextRepository);
			doc.getDocumentElement().normalize();
			log.info("Deserializing the CotextData.xml for Context Analysis.");
			JAXBContext jaxbContext = JAXBContext.newInstance("unistuttgart.iaas.spi.cmprocess.cmp");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			TProcessModel processModels = (TProcessModel) jaxbUnmarshaller.unmarshal(this.processRepository);
			for(TProcessDefinition processDefinition : processModels.getProcessDefinition()){
				if(processDefinition.getTargetNamespace().equals(ContextConfig.CONTEXT_NAMESPACE)){
					List<TContextExpression> expressionList = processDefinition.getInitialContexts().
							getContextExpression();
					for(TContextExpression contextExpression : expressionList){
						String xpathQuery = contextExpression.getCondition().get(0).
								getDefinitionContent().getExpression();
						if(contextExpression.getCondition().get(0).getDefinitionLanguage().
								equals(ContextConfig.XPATH_NAMESPACE)){
							XPathFactory xPathfactory = XPathFactory.newInstance();
							XPath xpath = xPathfactory.newXPath();
							XPathExpression expr = xpath.compile(xpathQuery);
							NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
							int noOfPredicates = xpathQuery.trim().split("\\|").length;
							if(nl.getLength() == noOfPredicates) {
								this.initialContextAnalysisTable.put(contextExpression.getId(), true);
							}
							else {
								this.initialContextAnalysisTable.put(contextExpression.getId(), false);
							}
						}
					}
				}
			}
			for(TProcessDefinition processDefinition : processModels.getProcessDefinition()){
				if(processDefinition.getTargetNamespace().equals(ContextConfig.CONTEXT_NAMESPACE)){
					String processId = processDefinition.getId();
					boolean result = false;
					List<TContextExpression> expressionList = processDefinition.
							getInitialContexts().getContextExpression();
					for(TContextExpression contextExpression : expressionList){
						String expressionId = contextExpression.getId();
						result = result | this.initialContextAnalysisTable.get(expressionId).booleanValue();
					}
					this.finalContextAnalysisTable.put(processId, result);
				}
			}
		} catch (NullPointerException e) {
			 log.severe("NullPointerException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Context Analyzer!");
		} catch (SAXException e) {
			 log.severe("SAXException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Context Analyzer!");
		} catch (IOException e) {
			log.severe("IOException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Context Analyzer!");
		} catch (XPathExpressionException e) {
			log.severe("XPathExpressionException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Context Analyzer!");
		} catch (ParserConfigurationException e) {
			log.severe("ParserConfigurationException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Context Analyzer!");
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + " in Context Analyzer!");
		} catch(Exception e){
			log.severe("Unknown Exception has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-4].getLineNumber() + 
					" in Context Analyzer!\n" + e.getMessage());
	    } finally{
			log.info("Context Analysis is Completed.");
		}
	}
	
	public void getProcessListForContextAnalysis() {
		Iterator<Map.Entry<String, Boolean>> contextIterator = this.finalContextAnalysisTable.entrySet().iterator();
		log.info("Final List of Processes is Being Generated.");
		while (contextIterator.hasNext()) {
			Map.Entry<String, Boolean> entry = contextIterator.next();
			if (entry.getValue()) {
				this.contextAnalysisProcessList.add(entry.getKey());
			}
		}
		log.info("Context Matching Processes: "+ this.contextAnalysisProcessList);
		log.info("Process List is Available for Intention Analyzer.");
	}
	
	public File getContextRepository() {
		return contextRepository;
	}

	public File getProcessRepository() {
		return processRepository;
	}

	public Map<String, Boolean> getInitialContextAnalysisTable() {
		return initialContextAnalysisTable;
	}

	public Map<String, Boolean> getFinalContextAnalysisTable() {
		return finalContextAnalysisTable;
	}

	public Set<String> getcontextAnalysisProcessList() {
		return contextAnalysisProcessList;
	}
}