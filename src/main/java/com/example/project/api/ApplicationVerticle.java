package com.example.project.api;

import java.util.List;

import com.example.project.model.Project;
import com.example.project.vertical.service.ProjectService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ApplicationVerticle extends AbstractVerticle {

	private ProjectService projectService;

	public ApplicationVerticle(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		System.out.println("Application verticle is starting....");

		Router router = Router.router(vertx);

		router.route("/addProject").handler(BodyHandler.create());
		router.post("/addProject").handler(this::addProject);
		router.get("/projects").handler(this::getProjects);
		router.get("/projects/:projectId").handler(this::getProjectById);
		router.get("/projects/status/:theStatus").handler(this::getProjectByStatus);
		
		 //Health Checks
        router.get("/health/readiness").handler(rc -> rc.response().end("OK"));
        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
                .register("health", f -> health(f));
        router.get("/health/liveness").handler(healthCheckHandler);
        
        vertx.createHttpServer()
        .requestHandler(router::accept)
        .listen(config().getInteger("project.http.port", 8080), result -> {
            if (result.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(result.cause());
            }
        });

	}

	private void getProjects(RoutingContext rc) {
		projectService.getProjects(ar -> {
			if (ar.succeeded()) {
				List<Project> projects = ar.result();
				JsonArray json = new JsonArray();

				projects.stream().map(p -> p.toJson()).forEach(p -> json.add(p));

				rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void getProjectById(RoutingContext rc) {
		System.out.println("getProductById");
		long projectId = Long.parseLong(rc.request().getParam("projectId"));
		projectService.getProjectById(projectId, ar -> {
			if (ar.succeeded()) {
				Project project = ar.result();
				JsonObject json;
				if (project != null) {
					json = project.toJson();
					rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
				} else {
					rc.fail(404);
				}
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void getProjectByStatus(RoutingContext rc) {
		System.out.println("getProductByStatus");
		String status = rc.request().getParam("theStatus");
		projectService.getProjectByStatus(status, ar -> {
			if (ar.succeeded()) {
				List<Project> projectList = ar.result();
				JsonArray json;
				if (projectList != null) {
					json = new JsonArray(projectList);
					rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
				} else {
					rc.fail(404);
				}
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void addProject(RoutingContext rc) {
		System.out.println("addProject");
		JsonObject json = rc.getBodyAsJson();
		projectService.addProject(new Project(json), ar -> {
			if (ar.succeeded()) {
				rc.response().setStatusCode(201).end();
			} else {
				rc.fail(ar.cause());
			}
		});
	}
	
	private void health(Future<Status> future) {
        projectService.ping(ar -> {
            if (ar.succeeded()) {
                // HealthCheckHandler has a timeout of 1000s. If timeout is exceeded, the future will be failed
                if (!future.isComplete()) {
                    future.complete(Status.OK());
                }
            } else {
                if (!future.isComplete()) {
                    future.complete(Status.KO());
                }
            }
        });
    }
	
}
