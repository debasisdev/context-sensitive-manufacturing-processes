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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPMessage;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TCESIntentionDefinition;
import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinitions;
import uni_stuttgart.iaas.spi.cmp.delutils.CESSOAPSolicitor;
import uni_stuttgart.iaas.spi.cmp.delutils.CESTaskDelegationConfig;
import uni_stuttgart.iaas.spi.cmp.realizations.CESExecutor;

/** 
 * Copyright 2016 Debasis Kar
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

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
	private static final Logger log = LoggerFactory.getLogger(CESTaskDelegation.class);
	
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
		TCESIntentionDefinition intention = this.createIntention(this.mainIntention.getExpressionText(), this.subIntentions.getExpressionText(), this.selectionStrategy.getExpressionText());
		cesDefinition.setIntention(intention);
		//Set Required-contexts
		TContextDefinitions contexts = this.createRequiredContext(this.requiredContext.getExpressionText());
		cesDefinition.getIntention().setRequiredContexts(contexts);
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
				SOAPMessage soapMessage = CESSOAPSolicitor.createSOAPRequest(cesDefinition);
				CESSOAPSolicitor.sendSOAPRequest(soapMessage, cesServiceEndpoint);
				//Polling Camel-RabbitMQ queue for reading the final output
				CamelContext context = new DefaultCamelContext();
				Endpoint endpoint = context.getEndpoint(CESTaskDelegationConfig.RABBIT_RESULT_QUEUE);
				PollingConsumer consumer = endpoint.createPollingConsumer();
				consumer.start();
				Exchange exchange = consumer.receive();
				consumer.stop();
				if(exchange != null){
					ProducerTemplate template = context.createProducerTemplate();
					template.sendBody(CESTaskDelegationConfig.RABBIT_CONSOLE_OUT, (byte[]) exchange.getIn().getBody());
					CESTaskDelegation.output = CESTaskDelegation.getOutput(exchange);
				}
			}
			//Directly call the Java method if response is 2
			if(this.response == 2){
				CESExecutor cesProcess = new CESExecutor(cesDefinition);
				cesProcess.runCESExecutor();
				result = cesProcess.isSuccess();
				if(result){
					CESTaskDelegation.output = CESTaskDelegation.getOutputOfProcess();
				}
			}
			//if CES task is successful read the result			
			if(CESTaskDelegation.output != null){
				for(TData data : CESTaskDelegation.output.getDataList()){
					if(data.getValue().equals(CESTaskDelegationConfig.BLANK_STRING) ||
							data.getValue().equals(CESTaskDelegationConfig.ERROR_STRING)){
						data.setValue(CESTaskDelegationConfig.ERROR_STRING);
					}
					execution.setVariable(data.getName(), data.getValue());
				}
			}
			//if CES task is unsuccessful, send error result
			else{
				execution.setVariable(outputVar.getDataList().get(0).getName(), CESTaskDelegationConfig.ERROR_STRING);
			}
		} catch (NullPointerException e) {
			log.error("CESTD00: NullPointerException has Occurred.");
			execution.setVariable(outputVar.getDataList().get(0).getName(), CESTaskDelegationConfig.ERROR_STRING);
		} catch (Exception e) {
			log.error("CESTD00: Unknown Exception has Occurred - " + e);
			execution.setVariable(outputVar.getDataList().get(0).getName(), CESTaskDelegationConfig.ERROR_STRING);
		} finally{
			log.info("Proceeding to next activity.");
		}
	}
	
	/**
	 * This method prepares the {@link TContexts} element of {@link TTaskCESDefinition} 
	 * from the input in GUI of CES Task.
	 * @author Debasis Kar
	 * @param contextList
	 * @return TContexts
	 */
	private TContextDefinitions createRequiredContext(String contextList){
		String[] contextNames = null;
		TContextDefinitions contexts = new TContextDefinitions();
		List<TContextDefinition> contextNameList = new ArrayList<TContextDefinition>();
		//If the input contains commas(,), separate it and save as individual TContext
		if(contextList.contains(CESTaskDelegationConfig.COMMA_STRING)){
			contextNames = contextList.split(CESTaskDelegationConfig.COMMA_STRING);
			for(String context : contextNames){
				TContextDefinition con = new TContextDefinition();
				con.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName(context.trim());
				contextNameList.add(con);
			}
		}
		//Store TContext into TContexts
		for(TContextDefinition context : contextNameList){
			contexts.getContext().add(context);
		}
		return contexts;
	}
	
	/**
	 * This method prepares the Input {@link TDataList} element of {@link TTaskCESDefinition} 
	 * from the input in GUI of CES Task.
	 * @author Debasis Kar
	 * @param input
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
	 * @param output
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
	 * @param mainIntention
	 * @param subIntentions
	 * @param selectionStrategy
	 * @return TIntention
	 */
	private TCESIntentionDefinition createIntention(String mainIntention, String subIntentions, String selectionStrategy) {
		mainIntention = mainIntention.trim();
		String[] subIntentionArray = null;
		TCESIntentionDefinition intent = new TCESIntentionDefinition();
		List<TCESIntentionDefinition> subIntents = new ArrayList<TCESIntentionDefinition>();
		//If the input other than alphanumeric characters, ignore them
		Pattern noSpecialCharPattern = Pattern.compile(CESTaskDelegationConfig.REGEX1, Pattern.CASE_INSENSITIVE);
		Matcher noSpecialCharMatcher = noSpecialCharPattern.matcher(mainIntention);
		//Set Main-intention
		if (!noSpecialCharMatcher.find()){
			intent.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName(mainIntention);
		}
		//If the input contains commas(,), separate it and consider only the first element
		if(subIntentions.contains(CESTaskDelegationConfig.COMMA_STRING)){
			//Set Sub-intentions
			subIntentionArray = subIntentions.split(CESTaskDelegationConfig.COMMA_STRING);
			for(String subIntention : subIntentionArray){
				TCESIntentionDefinition subIntent = new TCESIntentionDefinition();
				subIntent.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName(subIntention.trim());
				subIntents.add(subIntent);
			}
		}
		else{
			//If only one sub-intetion is present
			subIntentions = subIntentions.trim();
			noSpecialCharMatcher = noSpecialCharPattern.matcher(subIntentions);
			if (!noSpecialCharMatcher.find()){
				TCESIntentionDefinition subintent = new TCESIntentionDefinition();
				subintent.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName(subIntentions);
			}
		}
		List<TCESIntentionDefinition> subIntentionsList = new ArrayList<TCESIntentionDefinition>();
		//Set selection strategy
		if(selectionStrategy.equals(CESTaskDelegationConfig.SELECTION_WEIGHT_NAMESPACE)){
			intent.setSelectionStrategy(CESTaskDelegationConfig.SELECTION_WEIGHT_NAMESPACE);
		} 
		else if(selectionStrategy.equals(CESTaskDelegationConfig.SELECTION_MOSTUSED_NAMESPACE)){
			intent.setSelectionStrategy(CESTaskDelegationConfig.SELECTION_MOSTUSED_NAMESPACE);
		}
		else {
			intent.setSelectionStrategy(CESTaskDelegationConfig.SELECTION_RANDOM_NAMESPACE);
		}
		//Store TSubIntention to TSubIntentions
		for(TCESIntentionDefinition subIntent : subIntents){
			subIntentionsList.add(subIntent);
		}
		intent.getSubIntentions().getCESIntentionDefinitions().addAll(subIntentionsList);
		return intent;
	}
	
	/**
	 * This method reads the output of the CES task from the RabbitMQ Result queue.
	 * @author Debasis Kar
	 * @param void
	 * @return TDataList
	 */
	public static TDataList getOutputOfProcess(){
      	try {
    		//Initializing Camel Context, RabbitMQ Factory and others for Content-based message Routing
    		CamelContext camelCon = new DefaultCamelContext();
          	ConnectionFactory conFac = new ConnectionFactory();
          	conFac.setHost(CESTaskDelegationConfig.RABBIT_SERVER);
      		Connection connection = conFac.newConnection();
      		Channel channel = connection.createChannel();
			camelCon.addRoutes(new RouteBuilder() {
	            public void configure() {
	            	//Process Dispatcher Invocation
	                from(CESTaskDelegationConfig.RABBIT_RESULT_QUEUE)
		              	//Set Respective Routing Key
	                	.process(new Processor() {
	            			public void process(Exchange exchange) throws Exception {
	            				CESTaskDelegation.output = CESTaskDelegation.getOutput(exchange);
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
			log.error("CESTD33: IOException has Occurred.");
		} catch (TimeoutException e) {
			log.error("CESTD32: TimeoutException has Occurred.");
		} catch (NullPointerException e) {
			log.error("CESTD31: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("CESTD30: Unknown Exception has Occurred - " + e);
		}
		return CESTaskDelegation.output;
	}
	
	/**
	 * This method reads the output of the CES Service received after polling.
	 * @author Debasis Kar
	 * @param exchange
	 * @return TDataList
	 */
	public static TDataList getOutput(Exchange exchange){
		try {
			//JAXB implementation for de-serializing the output generated by Executor of CES
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TTaskCESDefinition definition = (TTaskCESDefinition) rootElement.getValue();
			CESTaskDelegation.output = definition.getOutputVariable();
		} catch (JAXBException e) {
			log.error("CESTD42: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.error("CESTD41: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("CESTD40: Unknown Exception has Occurred - " + e);
		}
		return CESTaskDelegation.output;
	}
	
	/**
	 * This is a setter method to set the value of Hidden field.
	 * @author Debasis Kar
	 * @param hiddenField
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