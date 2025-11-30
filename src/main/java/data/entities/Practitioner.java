package data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.enums.UserType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("PRACTITIONER")
public class Practitioner extends User {

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @JsonIgnore
    @OneToMany(mappedBy = "practitioner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Condition> conditions = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "practitioner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Encounter> encounters = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "practitioner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Observation> observations = new HashSet<>();

    public Practitioner() {}

    public Practitioner(String fullName, String email, String password, UserType userType, Organization organization) {
        super(fullName, email, password, userType);
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
        condition.setPractitioner(this);
    }

    public void removeCondition(Condition condition) {
        conditions.remove(condition);
        condition.setPractitioner(null);
    }

    public void addEncounter(Encounter encounter) {
        encounters.add(encounter);
        encounter.setPractitioner(this);
    }

    public void removeEncounter(Encounter encounter) {
        encounters.remove(encounter);
        encounter.setPractitioner(null);
    }

    public void addObservation(Observation observation) {
        observations.add(observation);
        observation.setPractitioner(this);
    }

    public void removeObservation(Observation observation) {
        observations.remove(observation);
        observation.setPractitioner(null);
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
}
