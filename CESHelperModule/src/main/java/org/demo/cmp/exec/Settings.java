package org.demo.cmp.exec;

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
 * A helper class to all filters that stores all the Constants/Strings/URIs used throughout the project.
 * @author Debasis Kar
 */

public final class Settings {
	
	public static final String SERVICE_NAMESPACE = "http://service.cmp.spi.iaas.uni_stuttgart/";
	
	public static final String AUTOSERVICE_URI = "http://localhost:9090/AutomatedDummyService/services/automatedwebservicemain";
	
	public static final String SOAP_FIELD_AUTOMATE = "automate";
	public static final String SOAP_FIELD_TASK = "task";
	public static final String SOAP_FIELD_SER = "ser";
	
	public static final String PROCESSREPOSITORY_FILEPATH = "D:\\MyWorkThesis\\SPIExtension\\src\\main\\resources\\dataRepository\\ProcessRepository.xml";
	
	public static final String SOAP_COMMAND_REPAIR = "Repair";
	public static final String SOAP_COMMAND_INSTALL = "Install";
	public static final String SOAP_COMMAND_MAINTAIN = "Maintain";
	public static final String SOAP_COMMAND_OPTIMIZE = "Optimize";
	public static final String SOAP_COMMAND_SEAL = "Seal";
	public static final String SOAP_COMMAND_ENDSEAL = "Complete";
	public static final String SOAP_COMMAND_SHIP = "Ship";
	public static final String SOAP_COMMAND_PREV = "Prev";
}