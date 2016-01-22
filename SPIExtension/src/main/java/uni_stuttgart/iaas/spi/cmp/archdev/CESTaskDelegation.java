package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntentions;

public class CESTaskDelegation implements JavaDelegate {
	
	private Expression mainIntention;
	private Expression subIntentions;
	private Expression requiredContext;
	private Expression processRepositoryPath;
	private Expression inputVariable;
	private Expression outputVariable;
	private Expression performOptimization;
	private Expression selectionStrategy;
	private Expression hiddenField;
	
	private static final Logger log = Logger.getLogger(CESTaskDelegation.class.getName());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ObjectFactory ob = new ObjectFactory();
		TTaskCESDefinition cesDefinition = ob.createTTaskCESDefinition();
		cesDefinition.setIsCommandAction(true);
		cesDefinition.setIsEventDriven(false);
		cesDefinition.setTargetNamespace("http://www.uni-stuttgart.de/iaas/");
		cesDefinition.setDomainKnowHowRepository(this.processRepositoryPath.getExpressionText().trim());
		cesDefinition.setOptimizationRequired(Boolean.parseBoolean(this.performOptimization.getExpressionText()));
		TIntention intention = this.createIntention(this.mainIntention.getExpressionText(), 
							this.subIntentions.getExpressionText(), this.selectionStrategy.getExpressionText());
		cesDefinition.setIntention(intention);
		TContexts contexts = this.createRequiredContext(requiredContext.getExpressionText());
		cesDefinition.setRequiredContexts(contexts);
		TDataList inputList = this.createInputData(inputVariable.getExpressionText());
		TDataList outputVar = this.createOutputPlaceholder(outputVariable.getExpressionText());
		cesDefinition.setInputData(inputList);
		cesDefinition.setOutputVariable(outputVar);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		JAXBElement<TTaskCESDefinition> root = ob.createCESDefinition(cesDefinition);
		jaxbMarshaller.marshal(root, new File("D://dev.xml"));
		
		String cesServiceEndpoint = "http://localhost:8080/CESExecutorService/services/cesexecutorservice";
		SOAPMessage soapMessage = CESTaskDelegation.createSOAPRequest(cesDefinition);
		String result = CESTaskDelegation.sendSOAPRequest(soapMessage, cesServiceEndpoint);
		
		System.out.println(result);
		CESExecutor cesProcess = new CESExecutor(cesDefinition);
		System.out.println("Hashcode: " + cesProcess.hashCode());
	}
	
	private TContexts createRequiredContext(String contextList){
		String[] contextNames = null;
		TContexts contexts = new TContexts();
		List<TContext> contextNameList = new ArrayList<TContext>();
		if(contextList.contains(",")){
			contextNames = contextList.split(",");
			for(String context : contextNames){
				TContext con = new TContext();
				con.setName(context.trim());
				contextNameList.add(con);
			}
		}
		for(TContext context : contextNameList){
			contexts.getContext().add(context);
		}
		return contexts;
	}
	
	public TDataList createInputData(String input){
		Set<TData> dataElements = new HashSet<TData>();
		if(input.contains(",")){
			String[] varList = input.split(",");
			for(String var : varList){
				String[] keyPair = var.split("=");
				String key = keyPair[0];
				String value = keyPair[1];
				TData dataElement = new TData();
				dataElement.setName(key.trim());
				dataElement.setValue(value.trim());
				dataElements.add(dataElement);
			}
		}
		TDataList dataList = new TDataList();
		for(TData data : dataElements){
			dataList.getDataList().add(data);
		}
		return dataList;
	}
	
	public TDataList createOutputPlaceholder(String output){
		TData dataElement = new TData();
		if(output.contains(",")){
			dataElement.setName(output.split(",")[0].trim());
		}
		else{
			dataElement.setName(output.trim());
		}
		dataElement.setValue("");
		TDataList dataList = new TDataList();
		dataList.getDataList().add(dataElement);
		return dataList;
	}
	
	private TIntention createIntention(String mainIntention, String subIntentions, String selectionStrategy) {
		mainIntention = mainIntention.trim();
		String[] subIntentionArray = null;
		TIntention intent = new TIntention();
		List<TSubIntention> subIntents = new ArrayList<TSubIntention>();
		Pattern noSpecialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher noSpecialCharMatcher = noSpecialCharPattern.matcher(mainIntention);
		if (!noSpecialCharMatcher.find()){
			intent.setName(mainIntention);
		}
		if(subIntentions.contains(",")){
			subIntentionArray = subIntentions.split(",");
			for(String subIntention : subIntentionArray){
				TSubIntention subIntent = new TSubIntention();
				subIntent.setName(subIntention.trim());
				subIntents.add(subIntent);
			}
		}
		else{
			subIntentions = subIntentions.trim();
			noSpecialCharMatcher = noSpecialCharPattern.matcher(subIntentions);
			if (!noSpecialCharMatcher.find()){
				TSubIntention subintent = new TSubIntention();
				subintent.setName(subIntentions);
			}
		}
		TSubIntentions subIntentionsList = new TSubIntentions();
		if(selectionStrategy.equals("Weight")){
			subIntentionsList.setSubIntentionRelations("http://www.uni-stuttgart.de/ipsm/intention/selections/weight-based");
		}
		for(TSubIntention subIntent : subIntents){
			subIntentionsList.getSubIntention().add(subIntent);
		}
		intent.getSubIntentions().add(subIntentionsList);
		return intent;
	}

	public void setMainIntention(Expression mainIntention) {
		this.mainIntention = mainIntention;
	}

	public void setSubIntentions(Expression subIntentions) {
		this.subIntentions = subIntentions;
	}

	public void setRequiredContext(Expression requiredContext) {
		this.requiredContext = requiredContext;
	}

	public void setProcessRepositoryPath(Expression processRepositoryPath) {
		this.processRepositoryPath = processRepositoryPath;
	}

	public void setInputVariable(Expression inputVariable) {
		this.inputVariable = inputVariable;
	}

	public void setOutputVariable(Expression outputVariable) {
		this.outputVariable = outputVariable;
	}

	public void setPerformOptimization(Expression performOptimization) {
		this.performOptimization = performOptimization;
	}

	public void setHiddenField(Expression hiddenField) {
		this.hiddenField = hiddenField;
	}

	public Expression getHiddenField() {
		return hiddenField;
	}
	
	public static SOAPMessage createSOAPRequest(TTaskCESDefinition cesDefinition) {
    	SOAPMessage soapMessage = null;
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();

	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("ser", "http://service.cmp.spi.iaas.uni_stuttgart/");
	        envelope.addNamespaceDeclaration("v0", "http://www.uni-stuttgart.de/iaas/ipsm/v0.2/");
	        envelope.addNamespaceDeclaration("v01", "http://www.uni-stuttgart.de/iaas/cmp/v0.1/");
	        envelope.addNamespaceDeclaration("ns", "http://docs.oasis-open.org/tosca/ns/2011/12");
	        
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement cesExecutorElem = soapBody.addChildElement("CESExecutor", "ser");
	        SOAPElement cesDefinitionElem = cesExecutorElem.addChildElement("CESDefinition");
	        cesDefinitionElem.setAttribute("name", cesDefinition.getName());
	        cesDefinitionElem.setAttribute("targetNamespace", cesDefinition.getTargetNamespace());
	        cesDefinitionElem.setAttribute("isEventDriven", cesDefinition.isIsEventDriven().toString());
	        cesDefinitionElem.setAttribute("isCommandAction", cesDefinition.isIsCommandAction().toString());
	        SOAPElement optRequired = cesDefinitionElem.addChildElement("OptimizationRequired");
	        optRequired.addTextNode(cesDefinition.isOptimizationRequired().toString());
	        SOAPElement domainRepos = cesDefinitionElem.addChildElement("DomainKnowHowRepository");
	        domainRepos.addTextNode(cesDefinition.getDomainKnowHowRepository());
	        SOAPElement requiredCon = cesDefinitionElem.addChildElement("RequiredContexts");
	        for(TContext con : cesDefinition.getRequiredContexts().getContext()){
	        	SOAPElement conElem = requiredCon.addChildElement("Context");
	        	conElem.setAttribute("name", con.getName());
	        }
	        SOAPElement inputList = cesDefinitionElem.addChildElement("InputData");
	        for(TData inputData : cesDefinition.getInputData().getDataList()){
	        	SOAPElement inputElem = inputList.addChildElement("DataList");
	        	inputElem.setAttribute("name", inputData.getName());
	        	inputElem.setAttribute("value", inputData.getValue());
	        }
	        SOAPElement outputList = cesDefinitionElem.addChildElement("OutputVariable");
	        for(TData outputData : cesDefinition.getOutputVariable().getDataList()){
	        	SOAPElement outputElem = outputList.addChildElement("DataList");
	        	outputElem.setAttribute("name", outputData.getName());
	        	outputElem.setAttribute("value", outputData.getValue());
	        }
	        SOAPElement intentElem = cesDefinitionElem.addChildElement("Intention");
	        intentElem.setAttribute("name", cesDefinition.getIntention().getName());
	        SOAPElement subIntentElem = intentElem.addChildElement("SubIntentions");
	        SOAPElement subIntRelation = subIntentElem.addChildElement("SubIntentionRelations");
	        subIntRelation.addTextNode(cesDefinition.getIntention().getSubIntentions().get(0).getSubIntentionRelations());
	        for(TSubIntentions subIntentions : cesDefinition.getIntention().getSubIntentions()){
	        	for(TSubIntention subIntention : subIntentions.getSubIntention()){
	        		SOAPElement subIntentionElem = subIntentElem.addChildElement("SubIntention");
	        		subIntentionElem.setAttribute("name", subIntention.getName());
	        	}
	        }
	        for(TData outputData : cesDefinition.getOutputVariable().getDataList()){
	        	SOAPElement outputElem = outputList.addChildElement("DataList");
	        	outputElem.setAttribute("name", outputData.getName());
	        	outputElem.setAttribute("value", outputData.getValue());
	        }	        
	        soapMessage.saveChanges();
	        soapMessage.writeTo(System.err);
	        System.out.println();
		} catch (SOAPException e) {
			log.severe("CESTD02: SOAPException has Occurred.");
		} catch (IOException e) {
			log.severe("CESTD01: IOException has Occurred.");
		} catch (Exception e) {
			log.severe("CESTD00: Unknown Exception has Occurred - " + e);
		}
		return soapMessage;
    }
	
	public static String sendSOAPRequest(SOAPMessage soapMessage, String url) {
    	SOAPBody sb = null;
		try {
	        //Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	        SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
	        soapResponse.writeTo(System.err);
	        sb = soapResponse.getSOAPBody();
	        soapConnection.close();
	        System.out.println();
		} catch (SOAPException e) {
			log.severe("CESTD12: SOAPException has Occurred.");
		} catch (IOException e) {
			log.severe("CESTD11: IOException has Occurred.");
		} catch (Exception e) {
			log.severe("CESTD10: Unknown Exception has Occurred - " + e);
		}
		if (sb.getFirstChild().getFirstChild().getTextContent().trim().length()>0)
			return sb.getFirstChild().getFirstChild().getTextContent();
		else
			return null;
    }

}