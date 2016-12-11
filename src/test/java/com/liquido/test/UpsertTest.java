package com.liquido.test;

import static org.junit.Assert.*;

import com.doogie.liquido.models.AreaModel;
import com.doogie.liquido.mongoServices.AreaService;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpsertTest {
  Logger log = LoggerFactory.getLogger(this.getClass());  // Simple Logging Facade 4 Java

  AreaService areaService;

  @Before
  public void setupDb() {
    System.out.println("Connecting to db ...");
    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    MongoDatabase db = mongoClient.getDatabase("liquido-test");
    areaService = new AreaService(db);
  }

  @Test
  public void testInsertNew() throws Exception {
    log.info("insert new");
    long count1 = areaService.count();
    AreaModel newArea = new AreaModel("new title", "new description " + System.currentTimeMillis());

    areaService.insert(newArea);

    long count2 = areaService.count();
    assertEquals(count1+1, count2);
  }

  @Test
  public void testUpdate() throws Exception {
    log.info("update");
    long count1 = areaService.count();
    AreaModel firstArea = areaService.getAll().get(0);
    firstArea.setTitle("updated title "+(System.currentTimeMillis() % 10000));

    areaService.upsert(firstArea);  // mormal update

    long count2 = areaService.count();
    assertEquals(count1, count2);
  }

  @Test
  public void testUpsert() throws Exception {
    log.info("update");
    long count1 = areaService.count();
    AreaModel newArea = new AreaModel("upserted title", "new description " + System.currentTimeMillis());

    areaService.upsert(newArea);

    long count2 = areaService.count();
    assertEquals(count1+1, count2);
  }


}
