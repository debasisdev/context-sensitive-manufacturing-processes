package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

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
 * A dummy Web Service call to show optimization process.
 * @author Debasis Kar
 */

public class OptimizationDemo implements JavaDelegate{
	
	/**Any processing that needs to be carried out must be implemented in this method which is defined 
	 * in JavaDelegate interface of Activiti Engine.
	 * @author Debasis Kar
	 * @param DelegateExecution
	 * @return void
	 * */
	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		//Request SOAP message to the Web Service based on some naive string
		if(delegateExecution.getCurrentActivityName().contains(Settings.SOAP_COMMAND_OPTIMIZE)){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall(Settings.SOAP_COMMAND_OPTIMIZE));
		}
	}

}
