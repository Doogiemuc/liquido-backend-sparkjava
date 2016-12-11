package com.doogie.liquido.mongoServices;

import com.doogie.liquido.models.AreaModel;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Date;

public class AreaService extends BaseService<AreaModel> {

  public AreaService(MongoDatabase database) {
    super(database, "areas", AreaModel.class);
  }

  public void someTests() throws Exception {
    long count = col.count();
    System.out.println("We have "+ count + " areas");


    System.out.println("Get first area");
    FindIterable<Document> res = col.find().limit(1);

    Document doc = res.first();
    System.out.println("Got from mongoDB as String: "+doc.toString());
    System.out.println("Got from mongoDB as JSON: "+doc.toJson());


    AreaModel firstArea = fromJson(doc.toJson());   // do not use toString() !!


    // ===== update
    /*
    firstArea.setTitle("Title from test"+System.currentTimeMillis());
    System.out.println("Updating with new title: "+firstArea.getTitle());
    Bson query = Filters.eq("_id", new ObjectId(firstArea.getId()));

    //This works
    Bson update = Updates.combine(
      Updates.set("title", "updated title"),
      Updates.currentDate("updatedAt")
    );

    // update all fields  ONLY WORKS WHITH mongoDB $set command
    firstArea.setId(null);
    String areaJson = toJson(firstArea);
    System.out.println("now updating JSON "+areaJson);
    Bson update2 = Document.parse(areaJson);

    System.out.println("BSON update: "+update2.toString());

    this.col.updateOne(query, update);
    */

    long count2 = col.count();
    if (count != count2) throw new Exception("count should still be "+count);
    System.out.println("Now we still have "+ count2 + " areas");

    System.out.println("Replacing the whole area");
    firstArea.setTitle("replaced title");
    firstArea.setUpdatedAt(new Date());
    this.replace(firstArea);

  }



}