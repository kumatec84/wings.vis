import java.sql.*;
import javax.jms.*;
import java.math.BigDecimal;
import oracle.jms.*;

/** This is a sample class that publishes messages using the JMS Interface to AQ

	@author Rene Steiner, Akadia AG
*/
public class AQJmsPublisher extends AQApplication {

	/** Main program that starts this application
		@param arguments Arguments passed at program start
	*/
	static public void main(String[] arguments) {
		
		AQJmsPublisher application = new AQJmsPublisher(arguments);
	}	

	/** Constructor		@param arguments Arguments passed at program start	*/
  protected AQJmsPublisher(String[] arguments) {

		try {

    	JMSTopic aq = createJMSClient();
      doTest(aq);
		}
		catch (Exception ex) {
			System.err.println("AQJmsPublisher.AQJmsPublisher(): " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/** Test method

		@param session Used AQ connection and session

  	@exception javax.jms.JMSException Super exception for all JMS errors
  	@exception java.sql.SQLException JDBC SQL exceptions
	*/
	protected void doTest(JMSTopic aq) throws JMSException, SQLException {

    AQJmsMultiQueueItem messageData;

    // Start the connection
    aq.connection.start();

    // Create topic publisher
    TopicPublisher publisher = aq.session.createPublisher(
                null);              // No specific topic here
                                    // Different ones can be used with same publisher
    // Get topic object
    Topic topic = ((AQjmsSession) aq.session).getTopic(
                DB_AQ_ADMIN_NAME,   // Queue owner
                "MULTI_QUEUE");     // Queue name

    // Prepare messages
    messageData = new AQJmsMultiQueueItem(
                0, "Published message", "Message has been published to subscriber 0");
    ObjectMessage objectMessage0 = aq.session.createObjectMessage(messageData);

    messageData = new AQJmsMultiQueueItem(
                1, "Published message", "Message has been published to subscriber 1");
    ObjectMessage objectMessage1 = aq.session.createObjectMessage(messageData);

    messageData = new AQJmsMultiQueueItem(
                2, "Published message", "Message has been published to subscriber 2");
    ObjectMessage objectMessage2 = aq.session.createObjectMessage(messageData);

    messageData = new AQJmsMultiQueueItem(
                3, "Published message", "Message has been published to subscribers 1 and 2");
    ObjectMessage objectMessage3 = aq.session.createObjectMessage(messageData);

    // Prepare recipient list: subscriber 0
    AQjmsAgent[] recipientList0 = new AQjmsAgent[1];
    recipientList0[0] = new AQjmsAgent(
                "SUBSCRIPTION0",    // Name and identification of the subscription
                null);              // Address is set for remote subscribers only

    // Prepare recipient list: subscriber 1
    AQjmsAgent[] recipientList1 = new AQjmsAgent[1];
    recipientList1[0]  = new AQjmsAgent("SUBSCRIPTION1", null);

    // Prepare recipient list: subscriber 2
    AQjmsAgent[] recipientList2 = new AQjmsAgent[1];
    recipientList2[0]  = new AQjmsAgent("SUBSCRIPTION2", null);

    // Prepare recipient list: subscriber 1 + 2
    AQjmsAgent[] recipientList12 = new AQjmsAgent[2];
    recipientList12[0] = new AQjmsAgent("SUBSCRIPTION1", null);
    recipientList12[1] = new AQjmsAgent("SUBSCRIPTION2", null);

    // Publish messages
    ((AQjmsTopicPublisher) publisher).publish(topic, objectMessage0, recipientList0);
    ((AQjmsTopicPublisher) publisher).publish(topic, objectMessage1, recipientList1);
    ((AQjmsTopicPublisher) publisher).publish(topic, objectMessage2, recipientList2);
    ((AQjmsTopicPublisher) publisher).publish(topic, objectMessage3, recipientList12);
		System.out.println("publish() successfully");

    // Commit activities
    aq.session.commit();

    // Close session
    aq.session.close();

    // Close connection
    aq.connection.close();
	}

} // End of class AQJmsPublisher

