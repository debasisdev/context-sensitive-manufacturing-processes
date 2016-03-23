package uni_stuttgart.iaas.spi.cmp.delutils;

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
 * An utility class to all other classes that stores all the Constants/Strings/URIs used throughout the project.
 * @author Debasis Kar
 */

public final class CESTaskDelegationConfig {
	
	public static final String SELECTION_WEIGHT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/weight-based";
	public static final String SELECTION_RANDOM_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/random";
	public static final String SELECTION_MOSTUSED_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/most-used";
	public static final String SERVICE_NAMESPACE = "http://service.cmp.spi.iaas.uni_stuttgart/";
	public static final String IPSM_NAMESPACE = "http://www.uni-stuttgart.de/iaas/ipsm/v0.2/";
	public static final String CMP_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v0.1/";
	public static final String TOSCA_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12";
	
	public static final String SOAPSERVICE_URI = "http://localhost:9090/CESExecutorService/services/cesexecutorservice";
	
	public static final String RABBIT_SERVER = "localhost";
	public static final String RABBIT_RESULT_QUEUE = "rabbitmq://localhost/cmp_messages?routingKey=StopRabbit&autoDelete=false&durable=false&queue=result_queue&prefetchEnabled=true";
	public static final String RABBIT_CONSOLE_OUT = "stream:out";
	
	public static final String ERROR_STRING = "error";
	public static final String SUCCESS_STRING = "done";
	public static final String BLANK_STRING = "";
	public static final String COMMA_STRING = ",";
	public static final String EQUAL_STRING = "=";
	public static final String REGEX1 = "[^a-z0-9 ]";
	
	public static final String SOAP_FIELD_INTENTION = "Intention";
	public static final String SOAP_FIELD_DATALIST = "DataList";
	public static final String SOAP_FIELD_OUTPUT = "OutputVariable";
	public static final String SOAP_FIELD_INPUT = "InputData";
	public static final String SOAP_FIELD_NAME = "name";
	public static final String SOAP_FIELD_VALUE = "value";
	public static final String SOAP_FIELD_SUBINTENTION = "SubIntention";
	public static final String SOAP_FIELD_SUBINTENTIONS = "SubIntentions";
	public static final String SOAP_FIELD_SUBINTENTIONRELATIONS = "SubIntentionRelations";
	public static final String SOAP_FIELD_CONTEXT = "Context";
	public static final String SOAP_FIELD_REQUIREDCONTEXTS = "RequiredContexts";
	public static final String SOAP_FIELD_PROCESSREPOSTYPE = "DomainKnowHowRepositoryType";
	public static final String SOAP_FIELD_PROCESSREPOS = "DomainKnowHowRepository";
	public static final String SOAP_FIELD_OPTIMIATIONREQUIRED = "OptimizationRequired";
	public static final String SOAP_FIELD_CESDEFINITION = "CESDefinition";
	public static final String SOAP_FIELD_CESEXECUTOR = "CESExecutor";
	public static final String SOAP_FIELD_COMMANDACTION = "isCommandAction";
	public static final String SOAP_FIELD_EVENTDRIVEN = "isEventDriven";
	public static final String SOAP_FIELD_TARGETNAMESPACE = "targetNamespace";
	public static final String SOAP_FIELD_SER = "ser";
	public static final String SOAP_FIELD_V0 = "v0";
	public static final String SOAP_FIELD_NS = "ns";
	public static final String SOAP_FIELD_V01 = "v01";

}

/**
 * Mongo Schema
	db.packagecontext.insert([
      {
         orderid: 'DE37464358BY',	  
         deliverydate: new Date(2015,12,10),
   	   	 language: 'en_US',
         by: 'Sealing Machine: SMEX207',
         url: 'http://www.uni-stuttgart.de/iaas/cmp/v1/packaging',
         timestamp: new Timestamp(),
	   	 latitude: 48.145198,
	   	 longitude: 11.5765667,
	   	 unitsOrdered: 1000,
	   	 sensordata: [	
            {
   				availableWorkers: 4,
              	infraredSensorStatus: 'Malfunctioned',
   				shockDetectorStatus: 'Okay'
            }
         ]
      }
	])
*/
