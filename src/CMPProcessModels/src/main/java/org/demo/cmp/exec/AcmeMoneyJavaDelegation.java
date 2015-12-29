package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

public class AcmeMoneyJavaDelegation implements JavaDelegate {

  private static final String ECHO_FORMAT = "%s: %s";

  private Expression accountNumber;
  private Expression maximumProcessingTime;
  private Expression vipCustomer;
  private Expression comments;
  private Expression accountType;
  private Expression withdrawlLimit;
  private Expression expiryDate;
  private Expression hiddenField;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    System.out.println("Debasis" + this.accountNumber.getExpressionText());
  }

  public void setEchoPrefix(Expression accountNumber) {
    this.accountNumber = accountNumber;
  }

public void setMaximumProcessingTime(Expression maximumProcessingTime) {
	this.maximumProcessingTime = maximumProcessingTime;
}

public void setAccountNumber(Expression accountNumber) {
	this.accountNumber = accountNumber;
}

public void setVipCustomer(Expression vipCustomer) {
	this.vipCustomer = vipCustomer;
}

public void setComments(Expression comments) {
	this.comments = comments;
}

public void setAccountType(Expression accountType) {
	this.accountType = accountType;
}

public void setWithdrawlLimit(Expression withdrawlLimit) {
	this.withdrawlLimit = withdrawlLimit;
}

public void setExpiryDate(Expression expiryDate) {
	this.expiryDate = expiryDate;
}

public void setHiddenField(Expression hiddenField) {
	this.hiddenField = hiddenField;
}

}