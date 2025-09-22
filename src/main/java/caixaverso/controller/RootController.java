package caixaverso.controller;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.net.URI;

@Path("/")
@IfBuildProfile("dev")
public class RootController {

    @GET
    @Operation(hidden = true)
    public Response redirectToDevUI() {
        return Response.seeOther(URI.create("/q/dev")).build();
    }
}