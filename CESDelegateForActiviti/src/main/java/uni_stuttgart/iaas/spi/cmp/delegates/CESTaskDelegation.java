package uni_stuttgart.iaas.spi.cmp.delegates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntentions;
import uni_stuttgart.iaas.spi.cmp.realizations.CESExecutor;

/**
 * The central class that gets activates as soon as the CES task gets the control. It prepares the 
 * {@link TTaskCESDefinition} from the user inputs and forwards it to the CES Executor (that also can be a service).
 * Finally it waits for the CES Executor to finish processing and reads the final output from the Result queue.
 * @author Debasis Kar
 */

public class CESTaskDelegation implements JavaDelegate {
	
	/**Variable that stores the Intention specified by user 
	 * @author Debasis Kar
	 * */
	private Expression mainIntention;
	
	/**Variable that stores the Sub-intentions specified by user 
	 * @author Debasis Kar
	 * */
	private Expression subIntentions;
	
	/**Variable that stores the Required-contexts specified by user 
	 * @author Debasis Kar
	 * */
	private Expression requiredContext;
	
	/**Variable that stores the Domain Know-how type, e.g., XML or SQL etc. 
	 * @author Debasis Kar
	 * */
	private Expression processRepositoryType;
	
	/**Variable that stores the Domain Know-how path. 
	 * @author Debasis Kar
	 * */
	private Expression processRepositoryPath;
	
	/**Variable that stores the input variables and their values. 
	 * @author Debasis Kar
	 * */
	private Expression inputVariable;
	
	/**Variable that stores the output variable name. 
	 * @author Debasis Kar
	 * */
	private Expression outputVariable;
	
	/**Variable that stores the reuqirement of optimization. 
	 * @author Debasis Kar
	 * */
	private Expression performOptimization;
	
	/**Variable that stores the slection strategy specifier. 
	 * @author Debasis Kar
	 * */
	private Expression selectionStrategy;
	
	/**Variable that stores a hidden field. 
	 * @author Debasis Kar
	 * */
	private Expression hiddenField;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(CESTaskDelegation.class.getName());
	
	/**Variable that stores the output returned by the Executor of CES task. 
	 * @author Debasis Kar
	 * */
	private static TDataList output;
	
	/**Variable used for deciding web-service based implementation or java based. 
	 * @author Debasis Kar
	 * */
	private int response;
	
	/**Default constructor of {@link CESTaskDelegation}
	 * @author Debasis Kar
	 * */
	public CESTaskDelegation() {
		System.out.print("Enter 1 for WSDL Based Execution OR 2 for Java Based Execution:");
		Scanner scanner = new Scanner(System.in);
		this.response = scanner.nextInt();
		scanner.close();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//Prepare CES Definition
		ObjectFactory ob = new ObjectFactory();
		TTaskCESDefinition cesDefinition = ob.createTTaskCESDefinition();
		//Set attributes
		cesDefinition.setIsCommandAction(true);
		cesDefinition.setIsEventDriven(false);
		cesDefinition.setTargetNamespace(CESTaskDelegationConfig.CMP_NAMESPACE);
		cesDefinition.setDomainKnowHowRepository(this.processRepositoryPath.getExpressionText().trim());
		cesDefinition.setOptimizationRequired(Boolean.parseBoolean(this.performOptimization.getExpressionText()));
		//Set Intention
		TIntention intention = this.createIntention(this.mainIntention.getExpressionText(), this.subIntentions.getExpressionText(), this.selectionStrategy.getExpressionText());
		cesDefinition.setIntention(intention);
		//Set Required-contexts
		TContexts contexts = this.createRequiredContext(this.requiredContext.getExpressionText());
		cesDefinition.setRequiredContexts(contexts);
		//Set I/O data
		cesDefinition.setDomainKnowHowRepositoryType(this.processRepositoryType.getExpressionText());
		TDataList inputList = this.createInputData(this.inputVariable.getExpressionText());
		List<TData> runtimeData = new LinkedList<TData>();
		for(String name : execution.getVariableNames()){
			TData data = new TData();
			data.setName(name);
			data.setValue(execution.getVariable(name).toString());
			runtimeData.add(data);
		}
		for(TData data : runtimeData){
			inputList.getDataList().add(data);
		}
		TDataList outputVar = this.createOutputPlaceholder(this.outputVariable.getExpressionText());
		cesDefinition.setInputData(inputList);
		cesDefinition.setOutputVariable(outputVar);
		//JAXB implementation for serializing the CES Definition to standard output
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		JAXBElement<TTaskCESDefinition> root = ob.createCESDefinition(cesDefinition);
		jaxbMarshaller.marshal(root, System.out);
		boolean result = false;
		try{
			//Call Web-service if response is 1
			if(this.response == 1){
				String cesServiceEndpoint = CESTaskDelegationConfig.SOAPSERVICE_URI;
				SOAPMessage soapMessage = CESTaskDelegation.createSOAPRequest(cesDefinition);
				result = CESTaskDelegation.sendSOAPRequest(soapMessage, cesServiceEndpoint);
			}
			//Directly call the Java methd if response is 2
			if(this.response == 2){
				CESExecutor cesProcess = new CESExecutor(cesDefinition);
				cesProcess.runCESExecutor();
				result = cesProcess.isSuccess();
			}
			//if CES task is successful read the result
			if(result){
				CESTaskDelegation.output = CESTaskDelegation.getOutputOfProcess();
				if(CESTaskDelegation.output != null){
					for(TData data : CESTaskDelegation.output.getDataList()){
						if(data.getValue().equals(CESTaskDelegationConfig.BLANK_STRING)){
							data.setValue(CESTaskDelegationConfig.ERROR_STRING);
						}
						execution.setVariable(data.getName(), data.getValue());
					}
				}
			}
			//if CES task is unsuccessful, send error result
			else{
				execution.setVariable(outputVar.getDataList().get(0).getName(), CESTaskDelegationConfig.ERROR_STRING);
			}
		} catch (NullPointerException e) {
			log.severe("CESTD00: NullPointerException has Occurred.");
			execution.setVariable(outputVar.getDataList().get(0).getName(), CESTaskDelegationConfig.ERROR_STRING);
		} catch (Exception e) {
			log.severe("CESTD00: Unknown Exception has Occurred - " + e);
			execution.setVariable(outputVar.getDataList().get(0).getName(), CESTaskDelegationConfig.ERROR_STRING);
		}
	}
	
	/**
	 * This method prepares the {@link TContexts} element of {@link TTaskCESDefinition} 
	 * from the input in GUI of CES Task.
	 * @author Debasis Kar
	 * @param String
	 * @return TContexts
	 */
	private TContexts createRequiredContext(String contextList){
		String[] contextNames = null;
		TContexts contexts = new TContexts();
		List<TContext> contextNameList = new ArrayList<TContext>();
		//If the input contains commas(,), separate it and save as individual TContext
		if(contextList.contains(CESTaskDelegationConfig.COMMA_STRING)){
			contextNames = contextList.split(CESTaskDelegationConfig.COMMA_STRING);
			for(String context : contextNames){
				TContext con = new TContext();
				con.setName(context.trim());
				contextNameList.add(con);
			}
		}
		//Store TContext into TContexts
		for(TContext context : contextNameList){
			contexts.getContext().add(context);
		}
		return contexts;
	}
	
	/**
	 * This method prepares the Input {@link TDataList} element of {@link TTaskCESDefinition} 
	 * from the input in GUI of CES Task.
	 * @author Debasis Kar
	 * @param String
	 * @return TDataList
	 */
	public TDataList createInputData(String input){
		Set<TData> dataElements = new HashSet<TData>();
		//If the input contains commas(,), separate it and save as individual TData
		if(input.contains(CESTaskDelegationConfig.COMMA_STRING)){
			String[] varList = input.split(CESTaskDelegationConfig.COMMA_STRING);
			for(String var : varList){
				//Split each TData item by equal(=) sign such that key-value pair is deduced
				String[] keyPair = var.split(CESTaskDelegationConfig.EQUAL_STRING);
				String key = keyPair[0];
				String value = keyPair[1];
				TData dataElement = new TData();
				dataElement.setName(key.trim());
				dataElement.setValue(value.trim());
				dataElements.add(dataElement);
			}
		}
		//Store TData into TDataList
		TDataList dataList = new TDataList();
		for(TData data : dataElements){
			dataList.getDataList().add(data);
		}
		return dataList;
	}
	
	/**
	 * This method prepares the Output {@link TDataList} element of {@link TTaskCESDefinition} 
	 * from the input in GUI of CES Task.
	 * @author Debasis Kar
	 * @param String
	 * @return TDataList
	 */
	public TDataList createOutputPlaceholder(String output){
		TData dataElement = new TData();
		//If the input contains commas(,), separate it and consider only the first element
		if(output.contains(CESTaskDelegationConfig.COMMA_STRING)){
			dataElement.setName(output.split(CESTaskDelegationConfig.COMMA_STRING)[0].trim());
		}
		else{
			dataElement.setName(output.trim());
		}
		//Set value of output variable initially to blank
		dataElement.setValue(CESTaskDelegationConfig.BLANK_STRING);
		TDataList dataList = new TDataList();
		dataList.getDataList().add(dataElement);
		return dataList;
	}
	
	/**
	 * This method prepares the {@link TIntention} element of {@link TTaskCESDefinition} 
	 * from the input in GUI of CES Task.
	 * @author Debasis Kar
	 * @param String
	 * @return TIntention
	 */
	private TIntention createIntention(String mainIntention, String subIntentions, String selectionStrategy) {
		mainIntention = mainIntention.trim();
		String[] subIntentionArray = null;
		TIntention intent = new TIntention();
		List<TSubIntention> subIntents = new ArrayList<TSubIntention>();
		//If the input other than alphanumeric characters, ignore them
		Pattern noSpecialCharPattern = Pattern.compile(CESTaskDelegationConfig.REGEX1, Pattern.CASE_INSENSITIVE);
		Matcher noSpecialCharMatcher = noSpecialCharPattern.matcher(mainIntention);
		//Set Main-intention
		if (!noSpecialCharMatcher.find()){
			intent.setName(mainIntention);
		}
		//If the input contains commas(,), separate it and consider only the first element
		if(subIntentions.contains(CESTaskDelegationConfig.COMMA_STRING)){
			//Set Sub-intentions
			subIntentionArray = subIntentions.split(CESTaskDelegationConfig.COMMA_STRING);
			for(String subIntention : subIntentionArray){
				TSubIntention subIntent = new TSubIntention();
				subIntent.setName(subIntention.trim());
				subIntents.add(subIntent);
			}
		}
		else{
			//If only one sub-intetion is present
			subIntentions = subIntentions.trim();
			noSpecialCharMatcher = noSpecialCharPattern.matcher(subIntentions);
			if (!noSpecialCharMatcher.find()){
				TSubIntention subintent = new TSubIntention();
				subintent.setName(subIntentions);
			}
		}
		TSubIntentions subIntentionsList = new TSubIntentions();
		//Set selection strategy
		if(selectionStrategy.equals(CESTaskDelegationConfig.SELECTION_WEIGHT_NAMESPACE)){
			subIntentionsList.setSubIntentionRelations(CESTaskDelegationConfig.SELECTION_WEIGHT_NAMESPACE);
		} 
		else if(selectionStrategy.equals(CESTaskDelegationConfig.SELECTION_MOSTUSED_NAMESPACE)){
			subIntentionsList.setSubIntentionRelations(CESTaskDelegationConfig.SELECTION_MOSTUSED_NAMESPACE);
		}
		else {
			subIntentionsList.setSubIntentionRelations(CESTaskDelegationConfig.SELECTION_RANDOM_NAMESPACE);
		}
		//Store TSubIntention to TSubIntentions
		for(TSubIntention subIntent : subIntents){
			subIntentionsList.getSubIntention().add(subIntent);
		}
		intent.getSubIntentions().add(subIntentionsList);
		return intent;
	}
	
	/**
	 * This method prepares the {@link SOAPMessage} out of {@link TTaskCESDefinition} to be sent to the Web-service.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return SOAPMessage
	 */
	public static SOAPMessage createSOAPRequest(TTaskCESDefinition cesDefinition) {
    	SOAPMessage soapMessage = null;
		try {
			//Create Message Factory
			MessageFactory messageFactory = MessageFactory.newInstance();
			soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	        //Prepare SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration(CESTaskDelegationConfig.SOAP_FIELD_SER, CESTaskDelegationConfig.SERVICE_NAMESPACE);
	        envelope.addNamespaceDeclaration(CESTaskDelegationConfig.SOAP_FIELD_V0, CESTaskDelegationConfig.IPSM_NAMESPACE);
	        envelope.addNamespaceDeclaration(CESTaskDelegationConfig.SOAP_FIELD_V01, CESTaskDelegationConfig.CMP_NAMESPACE);
	        envelope.addNamespaceDeclaration(CESTaskDelegationConfig.SOAP_FIELD_NS, CESTaskDelegationConfig.TOSCA_NAMESPACE);
	        //Prepare SOAP Body
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement cesExecutorElem = soapBody.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_CESEXECUTOR, CESTaskDelegationConfig.SOAP_FIELD_SER);
	        SOAPElement cesDefinitionElem = cesExecutorElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_CESDEFINITION);
	        cesDefinitionElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_NAME, cesDefinition.getName());
	        cesDefinitionElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_TARGETNAMESPACE, cesDefinition.getTargetNamespace());
	        cesDefinitionElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_EVENTDRIVEN, cesDefinition.isIsEventDriven().toString());
	        cesDefinitionElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_COMMANDACTION, cesDefinition.isIsCommandAction().toString());
	        SOAPElement optRequired = cesDefinitionElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_OPTIMIATIONREQUIRED);
	        optRequired.addTextNode(cesDefinition.isOptimizationRequired().toString());
	        SOAPElement domainRepos = cesDefinitionElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_PROCESSREPOS);
	        domainRepos.addTextNode(cesDefinition.getDomainKnowHowRepository());
	        //Set Required-contexts
	        SOAPElement requiredCon = cesDefinitionElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_REQUIREDCONTEXTS);
	        for(TContext con : cesDefinition.getRequiredContexts().getContext()){
	        	SOAPElement conElem = requiredCon.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_CONTEXT);
	        	conElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_NAME, con.getName());
	        }
	        //Set Input data
	        SOAPElement inputList = cesDefinitionElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_INPUT);
	        for(TData inputData : cesDefinition.getInputData().getDataList()){
	        	SOAPElement inputElem = inputList.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_DATALIST);
	        	inputElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_NAME, inputData.getName());
	        	inputElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_VALUE, inputData.getValue());
	        }
	        //Set Output data
	        SOAPElement outputList = cesDefinitionElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_OUTPUT);
	        for(TData outputData : cesDefinition.getOutputVariable().getDataList()){
	        	SOAPElement outputElem = outputList.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_DATALIST);
	        	outputElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_NAME, outputData.getName());
	        	outputElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_VALUE, outputData.getValue());
	        }
	        //Set Intention
	        SOAPElement intentElem = cesDefinitionElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_INTENTION);
	        intentElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_NAME, cesDefinition.getIntention().getName());
	        SOAPElement subIntentElem = intentElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_SUBINTENTIONS);
	        SOAPElement subIntRelation = subIntentElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_SUBINTENTIONRELATIONS);
	        subIntRelation.addTextNode(cesDefinition.getIntention().getSubIntentions().get(0).getSubIntentionRelations().toString());
	        for(TSubIntentions subIntentions : cesDefinition.getIntention().getSubIntentions()){
	        	for(TSubIntention subIntention : subIntentions.getSubIntention()){
	        		SOAPElement subIntentionElem = subIntentElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_SUBINTENTION);
	        		subIntentionElem.setAttribute(CESTaskDelegationConfig.SOAP_FIELD_NAME, subIntention.getName());
	        	}
	        }
	        //Save the Message
	        soapMessage.saveChanges();
	        soapMessage.writeTo(System.err);
	        System.out.println();
		} catch (SOAPException e) {
			log.severe("CESTD13: SOAPException has Occurred.");
		} catch (IOException e) {
			log.severe("CESTD12: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("CESTD11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("CESTD10: Unknown Exception has Occurred - " + e);
		}
		return soapMessage;
    }
	
	/**
	 * This method sends the {@link SOAPMessage} to the specified URL (Web-Service) and returns whether it's
	 * been successfully or not by a boolean value.
	 * @author Debasis Kar
	 * @param SOAPMessage, String
	 * @return boolean
	 */
	public static boolean sendSOAPRequest(SOAPMessage soapMessage, String url) {
    	SOAPBody sb = null;
		try {
	        //Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			//Retrieve Response
	        SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
	        soapResponse.writeTo(System.err);
	        sb = soapResponse.getSOAPBody();
	        soapConnection.close();
	        System.out.println();
		} catch (SOAPException e) {
			log.severe("CESTD23: SOAPException has Occurred.");
		} catch (IOException e) {
			log.severe("CESTD22: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("CESTD21: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("CESTD20: Unknown Exception has Occurred - " + e);
		}
		//If response is not null, read the result
		if (sb != null)
			return Boolean.parseBoolean(sb.getFirstChild().getFirstChild().getTextContent());
		else
			return false;
    }
	
	/**
	 * This method reads the output of the CES task from the RabbitMQ Result queue.
	 * @author Debasis Kar
	 * @param void
	 * @return TDataList
	 */
	public static TDataList getOutputOfProcess(){
		//Initializing Camel Context, RabbitMQ Factory and others for Content-based message Routing
		CamelContext camelCon = new DefaultCamelContext();
      	ConnectionFactory conFac = new ConnectionFactory();
      	conFac.setHost(CESTaskDelegationConfig.RABBIT_SERVER);
      	try {
      		Connection connection = conFac.newConnection();
      		Channel channel = connection.createChannel();
			camelCon.addRoutes(new RouteBuilder() {
	            public void configure() {
	            	//Process Dispatcher Invocation
	                from(CESTaskDelegationConfig.RABBIT_RESULT_QUEUE)
		              	//Set Respective Routing Key
	                	.process(new Processor() {
	            			public void process(Exchange exchange) throws Exception {
	            				//JAXB implementation for de-serializing the output generated by Executor of CES
	            				InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
	            				JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
	            				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	            				JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
	            				TTaskCESDefinition definition = (TTaskCESDefinition) rootElement.getValue();
	            				CESTaskDelegation.output = definition.getOutputVariable();
	            			}})
                		.to(CESTaskDelegationConfig.RABBIT_CONSOLE_OUT); 
	            }
	        });
	        //Start Camel Context
			camelCon.start();
			 //Stop Camel Context and close connection/channel objects
			camelCon.stop();
			channel.close();
			connection.close();
		} catch (IOException e) {
			log.severe("CESTD33: IOException has Occurred.");
		} catch (TimeoutException e) {
			log.severe("CESTD32: TimeoutException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("CESTD31: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("CESTD30: Unknown Exception has Occurred - " + e);
		}
		return CESTaskDelegation.output;
	}
	
	/**
	 * This is a setter method to set the value of Hidden field.
	 * @author Debasis Kar
	 * @param Expression
	 * @return void
	 */
	public void setHiddenField(Expression hiddenField) {
		this.hiddenField = hiddenField;
	}
	
	/**
	 * This is a getter method to get the Hideen field value.
	 * @author Debasis Kar
	 * @param void
	 * @return Expression
	 */
	public Expression getHiddenField() {
		return hiddenField;
	}

}