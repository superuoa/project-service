package com.example.project.vertical.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.project.model.Project;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ProjectServiceTest extends MongoTestBase {

    private Vertx vertx;

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        JsonObject config = getConfig();
        mongoClient = MongoClient.createNonShared(vertx, config);
        Async async = context.async();
        dropCollection(mongoClient, "project", async, context);
        async.await(10000);
    }

    @After
    public void tearDown() throws Exception {
        mongoClient.close();
        vertx.close();
    }

    @Test
    public void testAddProduct(TestContext context) throws Exception {
        long itemId = 999999;
        String name = "Test project_title";
        Project project = new Project();
        project.setProjectid(itemId);
        project.setOwner_email("test@gmail.com");
        project.setOwner_firstname("testF");
        project.setOwner_lastname("testL");
        project.setProject_description("testDesc");
        project.setProject_status("open");
        project.setProject_title("Test project_title");
        
        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.addProject(project, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                JsonObject query = new JsonObject().put("_id", itemId);
                mongoClient.findOne("project", query, null, ar1 -> {
                    if (ar1.failed()) {
                        context.fail(ar1.cause().getMessage());
                    } else {
                        assertThat(ar1.result().getString("project_title"), equalTo(name));
                        async.complete();
                    }
                });
            }
        });
    }

    @Test
    public void testGetProjects(TestContext context) throws Exception {
        Async saveAsync = context.async(2);
        long itemId1 = 111111;
        JsonObject json1 = new JsonObject()
                .put("projectid", itemId1)
                .put("owner_firstname", "owner_lastname1")
                .put("owner_lastname", "owner_lastname1")
                .put("owner_email", "owner_email1")
                .put("project_title", "project_title1")
                .put("project_description", "project_description1")
                .put("project_description", "project_description1");

        mongoClient.save("project", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        long itemId2 = 222222;
        JsonObject json2 = new JsonObject()
                .put("projectid", itemId2)
                .put("owner_firstname", "222")
                .put("owner_lastname", "222")
                .put("owner_email", "222")
                .put("project_title", "222")
                .put("project_description", "222")
                .put("project_description", "222");

        mongoClient.save("project", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProjects(ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().size(), equalTo(2));
                Set<Long> itemIds = ar.result().stream().map(p -> p.getProjectid()).collect(Collectors.toSet());
                assertThat(itemIds.size(), equalTo(2));
                assertThat(itemIds, allOf(hasItem(itemId1),hasItem(itemId2)));
                async.complete();
            }
        });
    }

   

}
