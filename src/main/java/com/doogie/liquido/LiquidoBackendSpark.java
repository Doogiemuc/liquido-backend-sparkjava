package com.doogie.liquido;

import com.doogie.liquido.models.AreaModel;
import com.doogie.liquido.mongoServices.AreaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.get;
import static spark.Spark.post;

public class LiquidoBackendSpark {
  Logger log = LoggerFactory.getLogger(LiquidoBackendSpark.class);  // Simple Logging Facade 4 Java

  private static final int HTTP_BAD_REQUEST = 400;

  AreaService areaService;

  public LiquidoBackendSpark() {
    MongoDatabase database = connectToDB();
    areaService = new AreaService(database);
    this.setupRoutes();
  }

  public MongoDatabase connectToDB() {
    System.out.println("Connecting to db ...");
    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    return mongoClient.getDatabase("liquido-test");
  }

  public void setupRoutes() {
    // get all post (using HTTP get method)
    get("/areas", (request, response) -> {
      log.info("=> GET /areas");
      String json = areaService.getAllAsMongoJson();

      /*
      List<AreaModel> allAreas = areaService.getAll();
      log.info(allAreas+"");
      */

      log.info("TESTTTTTTTTTTTTTTT");
      String modelAsJson = "{ \"_id\" : { \"$oid\" : \"57892d793d5b352b9b0134be\" }, \"title\" : \"Area 1\", \"description\" : \"Department/Area of interest or ministry 1\", \"createdAt\" : { \"$date\" : 1476651229717 }, \"updatedAt\" : { \"$date\" : 1476651229717 } }";
      AreaModel model = areaService.fromJson(modelAsJson);
      log.info("Model="+model);
      log.info("TESTTTTTTTTTTTTTTT");


      response.status(200);
      response.type("application/json");
      log.info("<= GET /areas: "+json);
      return json;
    });

    post("/areas", (request, response) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        AreaModel newArea = mapper.readValue(request.body(), AreaModel.class);
        areaService.insert(newArea);
        response.status(200);
        response.type("application/json");
        return "Successfully saved area.";
      } catch (Exception e) {
        response.status(HTTP_BAD_REQUEST);
        return "ERROR in POST /areas : "+e.toString();
      }
    });
  }

  public static void main( String[] args) {
    LiquidoBackendSpark backend = new LiquidoBackendSpark();
  }
}
