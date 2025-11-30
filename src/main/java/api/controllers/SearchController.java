package api.controllers;

import api.dto.*;
import core.enums.ConditionType;
import core.services.*;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchController {

    @Inject
    PatientService patientService;

    /**
     * Get all patients
     */
    @GET
    @WithTransaction
    @Path("/search/patients/all")
    public Uni<List<PatientDTO>> getAllPatients(@QueryParam("pageIndex") @DefaultValue("0") int pageIndex,
                                                @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                                                @QueryParam("eager") @DefaultValue("true") boolean eager) {
        return patientService.getAllPatients(pageIndex, pageSize, eager);
    }

    /**
     * Get patient by ID, optionally fetch relations
     */
    @GET
    @WithTransaction
    @Path("/search/patient/id/{id}")
    public Uni<PatientDTO> getPatientById(@PathParam("id") UUID id,
                                          @QueryParam("eager") @DefaultValue("true") boolean eager) {
        return patientService.getPatientById(id, eager);
    }

    /**
     * Get patient by email
     */
    @GET
    @WithTransaction
    @Path("/search/patient/email/{email}")
    public Uni<PatientDTO> getPatientByEmail(@PathParam("email") String email,
                                             @QueryParam("eager") @DefaultValue("true") boolean eager) {
        return patientService.getPatientByEmail(email, eager);
    }

    /**
     * Search patients by name (partial match)
     */
    @GET
    @WithTransaction
    @Path("/search/patients/name/{name}")
    public Uni<List<PatientDTO>> searchPatientsByName(@PathParam("name") String name,
                                                      @QueryParam("pageIndex") @DefaultValue("0") int pageIndex,
                                                      @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return patientService.searchPatientsByName(name, pageIndex, pageSize);
    }

    /**
     * Search patients by Condition
     */
    @GET
    @WithTransaction
    @Path("/search/patients/condition/{conditionType}")
    public Uni<List<PatientDTO>> getPatientsByConditionType(@PathParam("conditionType") ConditionType type) {
        return patientService.getPatientsByConditionType(type);
    }

    /**
     * Search patients by Practitioner's encounters by date
     */
    @GET
    @WithTransaction
    @Path("/search/patients/practitioner/id/{id}/date")
    public Uni<List<PatientDTO>> getPatientsByPractitionerEncountersAndDate(@QueryParam("localDate") LocalDate date,
                                                                            @PathParam("id") UUID id) {
        return patientService.getPatientsByPractitionerEncountersAndDate(id, date);
    }

    /**
     * Get all patients by associated with a practitioner
     */
    @GET
    @WithTransaction
    @Path("/search/patients/practitioner/id/{id}")
    public Uni<List<PatientDTO>> getPatientsByPractitioner(@PathParam("id") UUID id) {
        return patientService.getPatientsByPractitioner(id);
    }

    /**
     * Get practitioner by email
     */
    @GET
    @WithTransaction
    @Path("/search/practitioner/email/{email}")
    public Uni<PractitionerDTO> getPractitionerByEmail(@PathParam("email") String email) {
        return patientService.getPractitionerByEmail(email);
    }
}