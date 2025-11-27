package data.repositories;

import data.entities.Condition;
import data.entities.Encounter;
import data.entities.Observation;
import data.entities.Patient;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class PatientRepository implements PanacheRepository<Patient> {

    public Uni<Patient> findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Uni<Patient> findByEmailWithRelations(String email) {
        /*Patient patient = find("email", email).firstResult();
        if (patient == null) return null;
        Hibernate.initialize(patient.getConditions());
        Hibernate.initialize(patient.getEncounters());
        Hibernate.initialize(patient.getObservations());
        return patient;*/
        return null;
    }

    public Uni<Patient> findById(UUID id) {
        return find("id", id).firstResult();
    }

    public Uni<Patient> findByIdWithRelations(UUID id) {
        /*Patient patient = find("id", id).firstResult();
        if (patient == null) return null;
        Hibernate.initialize(patient.getConditions());
        Hibernate.initialize(patient.getEncounters());
        Hibernate.initialize(patient.getObservations());
        return patient;*/
        return null;
    }

    public Uni<List<Patient>> findAllPatients(int pageIndex, int pageSize) {
        return findAll().page(pageIndex, pageSize).list();
    }

    public Uni<List<Patient>> findAllPatientsWithRelations(int pageIndex, int pageSize) {
        /*List<Patient> patients = findAll().page(pageIndex, pageSize).list();
        patients.forEach(p -> {
            Hibernate.initialize(p.getConditions());
            Hibernate.initialize(p.getEncounters());
            Hibernate.initialize(p.getObservations());
        });
        return patients;*/
        return null;
    }

    public Uni<List<Patient>> searchByName(String namePattern, int pageIndex, int pageSize) {
        return find("fullName like ?1", "%" + namePattern + "%")
                .page(pageIndex, pageSize).list();
    }

    public Uni<List<Patient>> searchByNameWithRelations(String namePattern, int pageIndex, int pageSize) {
        /*List<Patient> patients = find("fullName like ?1", "%" + namePattern + "%")
                .page(pageIndex, pageSize).list();
        patients.forEach(p -> {
            Hibernate.initialize(p.getConditions());
            Hibernate.initialize(p.getEncounters());
            Hibernate.initialize(p.getObservations());
        });
        return patients;*/
        return null;
    }

    public Uni<List<Patient>> getPatientsByPractitioner(UUID practitionerId) {

        Uni<List<Encounter>> encountersUni = Encounter.list("practitionerId", practitionerId);
        Uni<List<Condition>> conditionsUni = Condition.list("practitionerId", practitionerId);
        Uni<List<Observation>> observationsUni = Observation.list("practitionerId", practitionerId);

        // 2. Kör alla tre parallellt (motsvarar Promise.all)
        return Uni.combine().all().unis(encountersUni, conditionsUni, observationsUni)
            .asTuple()
            .chain(tuple -> {
                // tuple.getItem1() är encounters, Item2 conditions, osv.

                // 3. Samla alla unika patient-IDn i ett Set (tar bort dubbletter automatiskt)
                Set<UUID> distinctPatientIds = new HashSet<>();

                // Strömma igenom resultaten och plocka IDn.
                // Anpassa .getPatientId() till vad dina fält faktiskt heter.
                tuple.getItem1().forEach(e -> distinctPatientIds.add(e.getPatient().getId()));
                tuple.getItem2().forEach(c -> distinctPatientIds.add(c.getPatient().getId()));
                tuple.getItem3().forEach(o -> distinctPatientIds.add(o.getPatient().getId()));

                // 4. Optimering: Om inga IDn hittades, returnera tom lista direkt
                // (annars kraschar Hibernate på en tom IN-clause)
                if (distinctPatientIds.isEmpty()) {
                    return Uni.createFrom().item(Collections.emptyList());
                }

                // 5. Hämta ALLA patienter i en enda query (WHERE id IN ...)
                // Vi sorterar direkt i databasen (ORDER BY) istället för i minnet
                return find("id in ?1 order by fullName", distinctPatientIds).list();
            });

    }
}