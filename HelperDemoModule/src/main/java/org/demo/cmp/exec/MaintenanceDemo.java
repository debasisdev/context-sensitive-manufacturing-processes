package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MaintenanceDemo implements JavaDelegate{

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		if(delegateExecution.getCurrentActivityName().contains("Maintain")){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("maintain"));
		}
	}

}
