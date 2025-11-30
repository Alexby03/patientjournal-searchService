package data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("PATIENT")
public class Patient extends User {
    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Condition> conditions = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Encounter> encounters = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Observation> observations = new HashSet<>();

    public Patient() {}

    public void addCondition(Condition condition) {
        conditions.add(condition);
        condition.setPatient(this);
    }

    public void removeCondition(Condition condition) {
        conditions.remove(condition);
        condition.setPatient(null);
    }

    public void addEncounter(Encounter encounter) {
        encounters.add(encounter);
        encounter.setPatient(this);
    }

    public void removeEncounter(Encounter encounter) {
        encounters.remove(encounter);
        encounter.setPatient(null);
    }

    public void addObservation(Observation observation) {
        observations.add(observation);
        observation.setPatient(this);
    }

    public void removeObservation(Observation observation) {
        observations.remove(observation);
        observation.setPatient(null);
    }

    public void setConditions(List<Condition> list) {
        for (Condition condition : list) {
            addCondition(condition);
        }
    }

    public void setObservations(List<Observation> list) {
        for (Observation observation : list) {
            addObservation(observation);
        }
    }

    public void setEncounters(List<Encounter> list) {
        for (Encounter encounter : list) {
            addEncounter(encounter);
        }
    }

    public Set<Condition> getConditions() {
        return conditions;
    }

    public Set<Encounter> getEncounters() {
        return encounters;
    }

    public Set<Observation> getObservations() {
        return observations;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "conditions=" + conditions +
                ", encounters=" + encounters +
                ", observations=" + observations +
                '}';
    }
}
