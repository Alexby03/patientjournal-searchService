package core.services;

import api.dto.*;
import core.enums.ConditionType;
import core.enums.UserType;
import core.mappers.DTOMapper;
import data.entities.Patient;
import data.entities.Practitioner;
import data.repositories.PatientRepository;
import data.repositories.PractitionerRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PatientService {

    @Inject
    PatientRepository patientRepository;

    @Inject
    PractitionerRepository practitionerRepository;

    public Uni<List<PatientDTO>> getAllPatients(int pageIndex, int pageSize, boolean eager) {

        Uni<List<Patient>> patients = eager
                ? patientRepository.findAllPatientsWithRelations(pageIndex, pageSize)
                : patientRepository.findAllPatients(pageIndex, pageSize);

        return patients.map(ps -> ps.stream()
                .map(p -> DTOMapper.toPatientDTO(p, eager))
                .toList());
    }

    /**
     * Get patient by ID, optionally fetch relations
     */
    public Uni<PatientDTO> getPatientById(UUID patientId, boolean eager) throws IllegalArgumentException {

        Uni<Patient> patientUni = eager
                ? patientRepository.findByIdWithRelations(patientId)
                : patientRepository.findById(patientId);

        return patientUni
                .onItem().ifNull().failWith(() -> new IllegalArgumentException("Patient not found"))

                .map(p -> DTOMapper.toPatientDTO(p, eager));
    }


    /**
     * Get patient by email
     */
    public Uni<PatientDTO> getPatientByEmail(String email, boolean eager) throws IllegalArgumentException {
        if (email == null || email.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Email cannot be empty"));
        }

        Uni<Patient> patientUni = eager
                ? patientRepository.findByEmailWithRelations(email)
                : patientRepository.findByEmail(email);

        return patientUni
                .onItem().ifNull().failWith(() -> new IllegalArgumentException("Patient with matching email not found."))
                .map(p -> DTOMapper.toPatientDTO(p, eager));
    }

    /**
     * Search patients by name (partial match)
     */
    public Uni<List<PatientDTO>> searchPatientsByName(String namePattern, int pageIndex, int pageSize) throws IllegalArgumentException {
        if (namePattern == null || namePattern.isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }

        Uni<List<Patient>> patients = patientRepository.searchByName(namePattern, pageIndex, pageSize);

        return patients.map(ps -> ps.stream()
                .map(p -> DTOMapper.toPatientDTO(p, false))
                .toList());
    }

    public Uni<List<PatientDTO>> getPatientsByConditionType(ConditionType type) {
        Uni<List<Patient>> patientsUni = patientRepository.getPatientsByConditionType(type);
        return patientsUni.map(ps -> ps.stream()
                .map(p -> DTOMapper.toPatientDTO(p, false)).toList());
    }

    public Uni<List<PatientDTO>> getPatientsByPractitionerEncountersAndDate(UUID practitionerId, LocalDate searchDate) {
        Uni<List<Patient>> patients = patientRepository.getPatientsByPractitionerEncountersAndDate(practitionerId, searchDate);
        return patients.map(ps -> ps.stream()
                .map(p -> DTOMapper.toPatientDTO(p, false)).toList());
    }

    public Uni<List<PatientDTO>> getPatientsByPractitioner(UUID practitionerId) {
        Uni<List<Patient>> patients = patientRepository.getPatientsByPractitioner(practitionerId);
        return patients.map(ps -> ps.stream()
                .map(p -> DTOMapper.toPatientDTO(p, false)).toList());
    }

    public Uni<PractitionerDTO> getPractitionerByEmail(String email) throws IllegalArgumentException {
        if (email == null || email.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Email cannot be empty"));
        }

        Uni<Practitioner> practitionerUni = practitionerRepository.findByEmailOnlyIfDoctor(email);

        return practitionerUni
                .onItem().ifNull().failWith(() -> new IllegalArgumentException("Practitioner with matching email not found."))
                .map(p -> DTOMapper.toPractitionerDTO(p, false));
    }

}