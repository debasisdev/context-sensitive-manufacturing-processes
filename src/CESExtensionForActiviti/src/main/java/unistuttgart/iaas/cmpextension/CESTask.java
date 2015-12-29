/**
 * 
 */
package unistuttgart.iaas.cmpextension;

import org.activiti.designer.integration.annotation.Help;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.annotation.PropertyItems;
import org.activiti.designer.integration.annotation.Runtime;
import org.activiti.designer.integration.annotation.TaskName;
import org.activiti.designer.integration.annotation.TaskNames;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;

/**
 * Defines the CES Task node.
 * @author Debasis Kar
 * @version 1
 * @since 27.12.2015
 */
@Runtime(javaDelegateClass = "unistuttgart.iaas.cmpextension.runtime.CESTaskDelegation")
@Help(displayHelpShort = "CES Task", 
displayHelpLong = "Creates a Context-sensitive task that will deicde which process is best at a certain time. ")
@TaskNames(
    {
      @TaskName(locale = "en", name = "English CES Task"),
      @TaskName(locale = "de", name = "Deutsche CES Aufgabe")
    }
)

public class CESTask extends AbstractCustomServiceTask {
	
  @Property(type = PropertyType.TEXT, displayName = "Main Intention/Goal", required = true, defaultValue = "SealAndSortPackets")
  @Help(displayHelpShort = "Define the main goal/inention.", displayHelpLong = "You can provide the main intention of the task. It shouldn't contain any special character.")
  private String mainIntention;
     
  @Property(type = PropertyType.TEXT, displayName = "Sub Intention(s)/Goal(s)", required = true, defaultValue = "highProfit, highLaborUtilization")
  @Help(displayHelpShort = "Define the sub-goals/inentions.", displayHelpLong = "You can provide the sub intentions (if any) of the task separated by commas(,).")
  private String subIntentions;
  
  @Property(type = PropertyType.TEXT, displayName = "Required Context Data", required = true, defaultValue = "machine2Status, infraredSensorStatus")
  @Help(displayHelpShort = "Mentioned required context data.", displayHelpLong = "You can provide the list of required context names to be looked for separted by commas(,).")
  private String requiredContext;
  
  @Property(type = PropertyType.TEXT, displayName = "Process Repository Location", required = true, defaultValue = "F:\\GitLab\\src\\main\\resources\\AutomatedHR.bpmn")
  @Help(displayHelpShort = "Give the path to the process models' store.", displayHelpLong = "You can provide the list of required context names to be looked for separted by commas(,).")
  private String processRepositoryPath;

  @Property(type = PropertyType.TEXT, displayName = "Result Variable", required = false)
  @Help(displayHelpShort = "Give a variable to store final result.", displayHelpLong = "Final output would be stored in this variable.")
  private String resultVariable;
  
  @Property(type = PropertyType.RADIO_CHOICE, displayName = "Require Optimization", required = false)
  @Help(displayHelpShort = "Should BPMN engine do any optimization?", displayHelpLong = "Choose the optimization criterion depending on the requirement and need of process model optimization in rumtime.")
  @PropertyItems({"Yes (Do It, If Strategies are Available!)", "1", "No (Don't Do It!)", "0"})
  private String performOptimization;
 
  
  @Property(type = PropertyType.TEXT, visible = false, defaultValue = "test")
  private String hiddenField;

  @Override
  public String contributeToPaletteDrawer() {
    return "IAAS - University of Stuttgart";
  }

  public String getName() {
    return "CES Task";
  }
  
  @Override
  public String getSmallIconPath() {
    return "icons/cessquareicon.png";
  }

}
