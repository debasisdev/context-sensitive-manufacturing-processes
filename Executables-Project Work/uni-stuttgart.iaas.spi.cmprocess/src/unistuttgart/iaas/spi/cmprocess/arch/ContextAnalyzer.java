package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class ContextAnalyzer {
	private File contextRepository;
	private File contextAnalysisReport;
	
	public File getContextAnalysisReport() {
		return contextAnalysisReport;
	}

	public ContextAnalyzer(){
		this.contextRepository = null;
		this.contextAnalysisReport = null;
		this.analyzeContext();
	}
	
	public ContextAnalyzer(QueryManager queryManager){
		this.contextRepository = queryManager.getContextData();
		this.contextAnalysisReport = new File(ContextConfig.CONTEXT_ANALYSIS_REPORT);
		this.analyzeContext();
	}
	
	private void analyzeContext() {
		try {
			Source contextInput = new StreamSource(this.contextRepository);
			Source contextXsl = new StreamSource(new File(ContextConfig.CONTEXT_RULES));
			Result contextAnalyzerOutput = new StreamResult(this.contextAnalysisReport);
			Transformer transformer = TransformerFactory.newInstance().newTransformer(contextXsl);
			transformer.transform(contextInput, contextAnalyzerOutput);
		} catch (NullPointerException e) {
			 System.err.println("NullPointerException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Context Analyzer!");
		} catch (TransformerConfigurationException e) {
			 System.err.println("TransformerConfigurationException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Context Analyzer!");
		} catch (TransformerFactoryConfigurationError e) {
			System.err.println("TransformerFactoryConfigurationError has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Context Analyzer!");
		} catch (TransformerException e) {
			System.err.println("TransformerException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Context Analyzer!");
		} catch(Exception e){
			System.err.println("Unknown Exception has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Context Analyzer!");
		} finally{
			System.out.println("Context Analysis is Performed and The Report is available for Intention Analyzer.");
		}
	}
}