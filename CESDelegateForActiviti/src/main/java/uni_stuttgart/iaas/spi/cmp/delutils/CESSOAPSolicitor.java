package uni_stuttgart.iaas.spi.cmp.delutils;

import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntentions;
import uni_stuttgart.iaas.spi.cmp.delegates.CESTaskDelegation;

/**
 * This utility class helps {@link CESTaskDelegation} in creating {@link SOAPMessage} and sending them as a request
 * to the SOAP based Web Service endpoint.
 * @author Debasis Kar
 */

public class CESSOAPSolicitor {
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(CESSOAPSolicitor.class);
	
	/**
	 * This method prepares the {@link SOAPMessage} out of {@link TTaskCESDefinition} to be sent to the Web-service.
	 * @author Debasis Kar
	 * @param cesDefinition
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
	        SOAPElement domainRepoType = cesDefinitionElem.addChildElement(CESTaskDelegationConfig.SOAP_FIELD_PROCESSREPOSTYPE);
	        domainRepoType.addTextNode(cesDefinition.getDomainKnowHowRepositoryType());
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
			log.error("CESSO13: SOAPException has Occurred.");
		} catch (IOException e) {
			log.error("CESSO12: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.error("CESSO11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("CESSO10: Unknown Exception has Occurred - " + e);
		}
		return soapMessage;
    }
	
	/**
	 * This method sends the {@link SOAPMessage} to the specified URL (Web-Service) and returns whether it's
	 * been successfully or not by a boolean value.
	 * @author Debasis Kar
	 * @param soapMessage
	 * @param url
	 * @return boolean
	 */
	public static void sendSOAPRequest(SOAPMessage soapMessage, String url) {
		try {
	        //Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			//Retrieve Response
	        SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
	        soapResponse.writeTo(System.err);
	        soapConnection.close();
	        System.out.println();
		} catch (SOAPException e) {
			log.error("CESSO23: SOAPException has Occurred.");
		} catch (IOException e) {
			log.error("CESSO22: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.error("CESSO21: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("CESSO20: Unknown Exception has Occurred - " + e);
		}
    }
}
