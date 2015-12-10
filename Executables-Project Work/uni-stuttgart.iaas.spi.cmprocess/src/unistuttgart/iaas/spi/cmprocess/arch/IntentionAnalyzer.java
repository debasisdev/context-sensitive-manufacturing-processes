package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import unistuttgart.iaas.spi.cmprocess.processgen.ProcessReposType;

public class IntentionAnalyzer {
	private File processRepository;
	private String intention;
	private Map<String, String> contextAnalysisTable;
	private List<String> requiredContextsList;
	
	public IntentionAnalyzer(){
		this.intention = null;
		this.processRepository = null;
		this.contextAnalysisTable = null;
		this.requiredContextsList = null;
		this.contextReportAnalysis(null);
		this.intentionAnalysis();
	}
	
	public String getIntention() {
		return intention;
	}

	public IntentionAnalyzer(ContextAnalyzer contextAnalyzer, QueryManager queryManager){
		this.intention = queryManager.getIntention();
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.contextAnalysisTable = new TreeMap<String, String>();
		this.requiredContextsList = new ArrayList<String>();
		this.contextReportAnalysis(contextAnalyzer);
		this.intentionAnalysis();
	}
	
	private void intentionAnalysis(){
		try {			
			JAXBContext jaxbContext = JAXBContext.newInstance("unistuttgart.iaas.spi.cmprocess.processgen");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ProcessReposType conSetType = (ProcessReposType) jaxbUnmarshaller.unmarshal(this.processRepository);
			System.out.println(conSetType.getTProcessDefinition().get(2).getProcessContent());
		} catch (JAXBException e) {
			System.err.println("JAXBException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Intention Analyzer!");
		} catch (NullPointerException e) {
			System.err.println("NullPointerException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Intention Analyzer!");
		} catch (Exception e) {
			System.err.println("Unknown Exception has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Intention Analyzer!");
		} 
	}
	
	private void contextReportAnalysis(ContextAnalyzer contextAnalyzer){
		try {
		     Document doc = Jsoup.parse(contextAnalyzer.getContextAnalysisReport(), "UTF-8");
		     Elements tableElements = doc.select("table");
		     Elements tableRowElements = tableElements.select(":not(thead) tr");
		     for (int i = 1; i < tableRowElements.size(); i++) {
		        Element row = tableRowElements.get(i);
		        Elements rowItems = row.select("td");
		        this.contextAnalysisTable.put(rowItems.get(0).text(), rowItems.get(2).text());
		     }
		     System.out.println(this.contextAnalysisTable);
		  } catch (IOException e) {
			  System.err.println("IOException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Intention Analyzer!");
		  } catch (NullPointerException e) {
			 System.err.println("NullPointerException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Intention Analyzer!");
		  } catch (Exception e) {
			System.err.println("Unknown Exception has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Intention Analyzer!");
		  } 
	}
}
