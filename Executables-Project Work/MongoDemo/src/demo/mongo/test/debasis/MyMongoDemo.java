package demo.mongo.test.debasis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class MyMongoDemo {
	public static void main(String[] args) throws Exception {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB db = mongoClient.getDB("contextdb");
		Set<String> mongoCollections = db.getCollectionNames();
		for(String coll : mongoCollections){
			if(coll.equals("packagecontext")){
				DBCursor mongoCursor = db.getCollection(coll).find();
				while(mongoCursor.hasNext()) {
					BasicDBObject obj = (BasicDBObject) mongoCursor.next();
				    System.out.println(obj.getString("unitsOrdered"));
				    System.out.println(obj.getString("deliverydate"));
				    DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
				    Date date = (Date)formatter.parse(obj.getString("deliverydate"));
				    Calendar cal = Calendar.getInstance();
				    cal.setTime(date);
				    String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				    System.out.println("formatedDate : " + formatedDate);  
				    BasicDBList valueslist = (BasicDBList) obj.get("sensordata");
				    BasicDBObject values = (BasicDBObject) valueslist.get(0);
				    System.out.println(values.get("infraredSensorStatus")); 
				}
			}
		}
	}
}
