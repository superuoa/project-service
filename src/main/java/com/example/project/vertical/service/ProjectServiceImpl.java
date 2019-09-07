package com.example.project.vertical.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.project.model.Project;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class ProjectServiceImpl implements ProjectService {

	private MongoClient client;
	//private Vertx vertx;
    public ProjectServiceImpl(Vertx vertx, JsonObject config, MongoClient client) {
    	this.client = client;
       
    }

    //test local mongo
    public MongoClient getMongoClient() {
		
    	Vertx vertx = Vertx.vertx();
	    String uri = "mongodb://localhost:27017";
	    
	    String db = "projectdb";
	    
	    JsonObject mongoconfig = new JsonObject()
	        .put("connection_string", uri)
	        .put("db_name", db);

	    MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);
	    
	    return mongoClient;
	}
	
    @Override
	public void getProjects(Handler<AsyncResult<List<Project>>> resulthandler) {
		JsonObject query = new JsonObject();
        client.find("project", query, ar -> {
            if (ar.succeeded()) {
                List<Project> projects = ar.result().stream()
                                           .map(json -> new Project(json))
                                           .collect(Collectors.toList());
                resulthandler.handle(Future.succeededFuture(projects));
            } else {
                resulthandler.handle(Future.failedFuture(ar.cause()));
            }
        });
		
	}

    @Override
	public void getProjectById(long projectId, Handler<AsyncResult<Project>> resulthandler) {
    	JsonObject query = new JsonObject().put("projectid", projectId);
        client.find("project", query, ar -> {
            if (ar.succeeded()) {
                Optional<JsonObject> result = ar.result().stream().findFirst();
                if (result.isPresent()) {
                    resulthandler.handle(Future.succeededFuture(new Project(result.get())));
                } else {
                    resulthandler.handle(Future.succeededFuture(null));
                }
            } else {
                resulthandler.handle(Future.failedFuture(ar.cause()));
            }
        });
		
	}

    @Override
	public void getProjectByStatus(String status, Handler<AsyncResult<List<Project>>> resulthandler) {
    	
    	JsonObject query = new JsonObject().put("project_status", status);
        client.find("project", query, ar -> {
            if (ar.succeeded()) {
                List<Project> projects = ar.result().stream()
                                           .map(json -> new Project(json))
                                           .collect(Collectors.toList());
                resulthandler.handle(Future.succeededFuture(projects));
            } else {
                resulthandler.handle(Future.failedFuture(ar.cause()));
            }
        });
		
	}

    @Override
	public void addProject(Project project, Handler<AsyncResult<String>> resulthandler) {
    	client.save("project", toDocument(project), resulthandler);
		
	}

	@Override
	public void updateProject(Project project, Handler<AsyncResult<String>> resulthandler) {

		JsonObject query = new JsonObject();
		query.put("projectid", project.getProjectid());
		
		// Set new value
		JsonObject update = new JsonObject().put("$set", Json.encode(project));
		client.updateCollection("project", query, update, res -> {
			if (res.succeeded()) {
				System.out.println("project updated id !" + project.getProjectid());
				String str = toDocument(project).toString();
				resulthandler.handle(Future.succeededFuture(str));
			} else {
				resulthandler.handle(Future.failedFuture(res.cause()));
			}
		});

	}

	@Override
	public void deleteProject(long projectId, Handler<AsyncResult<String>> resulthandler) {
		JsonObject query = new JsonObject().put("projectid", projectId);
		client.removeDocuments("project", query, res -> {
			if (res.succeeded()) {
				String msg = "Delete projectid "+projectId + " Success";
				resulthandler.handle(Future.succeededFuture(msg));
				
			} else {
				resulthandler.handle(Future.failedFuture(res.cause()));
			}
		});

	}
    
    private JsonObject toDocument(Project product) {
        JsonObject document = product.toJson();
        document.put("_id", product.getProjectid());
        return document;
    }

	@Override
	public void ping(Handler<AsyncResult<String>> resultHandler) {
		 resultHandler.handle(Future.succeededFuture("OK"));
		
	}
    
}
