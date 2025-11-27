package api.controllers;

import api.dto.*;
import core.enums.ConditionType;
import core.enums.LocationType;
import core.services.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchController {

    @Inject
    UserService userService;

    @Inject
    PatientService patientService;

    @Inject
    PractitionerService practitionerService;

    @Inject
    OrganizationService organizationService;

    @Inject
    ObservationService observationService;

    @Inject
    EncounterService encounterService;

    @Inject
    ConditionService conditionService;

    // =======================
    // GET
    // =======================

    // Users =======================

    /**
     * Get all users with pagination
     */
    @GET
    @Path("/users")
    public List<UserDTO> getAllUsers(@QueryParam("pageIndex") @DefaultValue("0") int pageIndex,
                                     @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return userService.getAllUsers(pageIndex, pageSize);
    }







}