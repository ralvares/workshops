package com.redhat.developer.demos.recommendation.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/")
public class RecommendationResource {

    private static final String RESPONSE_STRING_FORMAT = "recommendation %s from '%s': %d\n";

    private static final String RESPONSE_STRING_NOW_FORMAT = "recommendation v3 %s from '%s': %d\n";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

    /**
     * Flag for throwing a 503 when enabled
     */
    private boolean misbehave = false;

    /**
     * Timeout value for forcing timeout if > 0
     */
    private long timeout;

    private static final String HOSTNAME = parseContainerIdFromHostname(
            System.getenv().getOrDefault("HOSTNAME", "unknown"));
    private static final String VERSION = parseVersionFromHostname(
            System.getenv().getOrDefault("HOSTNAME", "unknown"));

    static String parseVersionFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-", "").replaceAll("-.*", "");
    }

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-v\\d+-", "");
    }

    @GET
    public Response getRecommendations() {
        count++;
        logger.info(String.format("recommendation request from %s: %d", HOSTNAME, count));

        if (timeout > 0) {
            sleep(timeout);
        }

        logger.debug("recommendation service ready to return");
        if (misbehave) {
            return doMisbehavior();
        }
        return Response.ok(String.format(RESPONSE_STRING_FORMAT, VERSION, HOSTNAME, count)).build();
        // return Response.ok(String.format(RESPONSE_STRING_NOW_FORMAT, getNow(), HOSTNAME, count)).build();
    }

    private void sleep(final long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            logger.info("Thread interrupted");
        }
    }

    private Response doMisbehavior() {
        logger.debug(String.format("Misbehaving %d", count));
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(String.format("recommendation misbehavior from '%s'\n", HOSTNAME)).build();
    }

    @GET
    @Path("/misbehave")
    public Response flagMisbehave() {
        this.misbehave = true;
        logger.debug("'misbehave' has been set to 'true'");
        return Response.ok("Following requests to / will return a 503\n").build();
    }

    @GET
    @Path("/behave")
    public Response flagBehave() {
        this.misbehave = false;
        logger.debug("'misbehave' has been set to 'false'");
        return Response.ok("Following requests to / will return 200\n").build();
    }

    @GET
    @Path("/timeout")
    public Response setTimeout(@QueryParam("timeout") int timeout) {
        this.timeout = timeout*1000L;
        logger.debug("'timeout' has been set to " + timeout + " seconds");
        return Response.ok("Timeout has been set to " + timeout + " seconds\n").build();
    }

    private String getNow() {
        final Client client = ClientBuilder.newClient();
        final Response res = client.target("http://worldclockapi.com/api/json/cet/now").request().get();
        final String jsonObject = res.readEntity(String.class);
        return Json.createReader(new ByteArrayInputStream(jsonObject.getBytes())).readObject().getString("currentDateTime");
    }

}
