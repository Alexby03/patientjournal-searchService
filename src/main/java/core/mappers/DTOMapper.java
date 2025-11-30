package core.mappers;

import api.dto.*;
import data.entities.*;

import java.util.stream.Collectors;

public class DTOMapper {

    // Condition
    public static ConditionDTO toConditionDTO(Condition condition) {
        return new ConditionDTO(
                condition.getConditionId(),
                condition.getConditionName(),
                condition.getConditionType(),
                condition.getSeverityLevel(),
                condition.getDiagnosedDate(),
                condition.getPractitioner() != null ? condition.getPractitioner().getId() : null,
                condition.getPatient() != null ? condition.getPatient().getId() : null
        );
    }

    // Encounter
    public static EncounterDTO toEncounterDTO(Encounter encounter) {
        return new EncounterDTO(
                encounter.getEncounterId(),
                encounter.getEncounterDate(),
                encounter.getDescription(),
                encounter.getPatient() != null ? encounter.getPatient().getId() : null,
                encounter.getPractitioner() != null ? encounter.getPractitioner().getId() : null
        );
    }

    // Observation
    public static ObservationDTO toObservationDTO(Observation observation) {
        return new ObservationDTO(
                observation.getObservationId(),
                observation.getDescription(),
                observation.getObservationDate(),
                observation.getPatient() != null ? observation.getPatient().getId() : null,
                observation.getPractitioner() != null ? observation.getPractitioner().getId() : null
        );
    }

    // Patient
    public static PatientDTO toPatientDTO(Patient patient, boolean eager) {
        PatientDTO dto = new PatientDTO();
        dto.id = patient.getId();
        dto.fullName = patient.getFullName();
        dto.email = patient.getEmail();

        if (eager) {
            dto.conditions = patient.getConditions().stream()
                    .map(DTOMapper::toConditionDTO)
                    .collect(Collectors.toList());

            dto.encounters = patient.getEncounters().stream()
                    .map(DTOMapper::toEncounterDTO)
                    .collect(Collectors.toList());

            dto.observations = patient.getObservations().stream()
                    .map(DTOMapper::toObservationDTO)
                    .collect(Collectors.toList());
        }
        return dto;
    }

    public static PractitionerDTO toPractitionerDTO(Practitioner practitioner, boolean eager) {
        PractitionerDTO dto = new PractitionerDTO();
        dto.id = practitioner.getId();
        dto.fullName = practitioner.getFullName();
        dto.email = practitioner.getEmail();
        dto.userType = practitioner.getUserType();
        dto.organizationType = practitioner.getOrganization() != null
                ? practitioner.getOrganization().getOrganizationType().name()
                : null;

        if (eager) {
            dto.conditions = practitioner.getConditions().stream()
                    .map(DTOMapper::toConditionDTO)
                    .collect(Collectors.toList());

            dto.encounters = practitioner.getEncounters().stream()
                    .map(DTOMapper::toEncounterDTO)
                    .collect(Collectors.toList());

            dto.observations = practitioner.getObservations().stream()
                    .map(DTOMapper::toObservationDTO)
                    .collect(Collectors.toList());
        }

        return dto;
    }
}
