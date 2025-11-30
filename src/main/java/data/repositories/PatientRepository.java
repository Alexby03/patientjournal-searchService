package data.repositories;

import core.enums.ConditionType;
import core.enums.EncounterType;
import data.entities.Condition;
import data.entities.Encounter;
import data.entities.Observation;
import data.entities.Patient;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class PatientRepository implements PanacheRepository<Patient> {

    public Uni<Patient> findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Uni<Patient> findByEmailWithRelations(String email) {
        return find("""
        select distinct p from Patient p
        left join fetch p.conditions
        left join fetch p.encounters
        left join fetch p.observations
        where p.email = ?1
    """, email).firstResult();
    }

    public Uni<Patient> findById(UUID id) {
        return find("id", id).firstResult();
    }

    public Uni<Patient> findByIdWithRelations(UUID id) {
        return find("""
        select distinct p from Patient p
        left join fetch p.conditions
        left join fetch p.encounters
        left join fetch p.observations
        where p.id = ?1
    """, id).firstResult();
    }

    public Uni<List<Patient>> findAllPatients(int pageIndex, int pageSize) {
        return findAll().page(pageIndex, pageSize).list();
    }

    public Uni<List<Patient>> findAllPatientsWithRelations(int pageIndex, int pageSize) {
        return find("""
        select distinct p from Patient p
        left join fetch p.conditions
        left join fetch p.encounters
        left join fetch p.observations
    """).page(pageIndex, pageSize).list();
    }

    public Uni<List<Patient>> searchByName(String namePattern, int pageIndex, int pageSize) {
        return find("fullName like ?1", "%" + namePattern + "%")
                .page(pageIndex, pageSize).list();
    }

    public Uni<List<Patient>> searchByNameWithRelations(String namePattern, int pageIndex, int pageSize) {
        return find("""
        select distinct p from Patient p
        left join fetch p.conditions
        left join fetch p.encounters
        left join fetch p.observations
        where p.fullName like ?1
    """, "%" + namePattern + "%")
                .page(pageIndex, pageSize).list();
    }

    public Uni<List<Patient>> getPatientsByPractitioner(UUID practitionerId) {
        return Encounter.<Encounter>list("practitioner.id", practitionerId)
                .chain(encounters ->
                        Condition.<Condition>list("practitioner.id", practitionerId)
                                .chain(conditions ->
                                        Observation.<Observation>list("practitioner.id", practitionerId)
                                                .chain(observations -> {

                                                    Set<UUID> patientIds = new HashSet<>();

                                                    for (Encounter e : encounters) {
                                                        if (e.getPatient() != null) {
                                                            patientIds.add(e.getPatient().getId());
                                                        }
                                                    }

                                                    for (Condition c : conditions) {
                                                        if (c.getPatient() != null) {
                                                            patientIds.add(c.getPatient().getId());
                                                        }
                                                    }

                                                    for (Observation o : observations) {
                                                        if (o.getPatient() != null) {
                                                            patientIds.add(o.getPatient().getId());
                                                        }
                                                    }

                                                    if (patientIds.isEmpty()) {
                                                        return Uni.createFrom().item(Collections.emptyList());
                                                    }

                                                    return find("id in ?1", patientIds).list();
                                                })
                                )
                );
    }

    public Uni<List<Patient>> getPatientsByConditionType(ConditionType type) {
        return find("""
        SELECT DISTINCT p
        FROM Patient p
        JOIN p.conditions c
        WHERE c.conditionType = ?1
        ORDER BY p.fullName
    """, type).list();
    }

    public Uni<List<Patient>> getPatientsByPractitionerEncountersAndDate(UUID practitionerId, LocalDate searchDate) {
        return find("""
        SELECT DISTINCT p
        FROM Patient p
        JOIN p.encounters e
        WHERE e.practitioner.id = ?1
          AND cast(e.encounterDate as date) = ?2
        ORDER BY p.fullName
    """, practitionerId, searchDate).list();
    }
}