package data.repositories;

import core.enums.ConditionType;
import core.enums.UserType;
import data.entities.*;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class PractitionerRepository implements PanacheRepository<Practitioner> {

    public Uni<Practitioner> findByEmailOnlyIfDoctor(String email) {
        return find("email = ?1 and userType = ?2", email, UserType.Doctor).firstResult();
    }


    public Uni<Practitioner> findByEmailWithRelations(String email) {
        return find("""
        select distinct p from Practitioner p
        left join fetch p.conditions
        left join fetch p.encounters
        left join fetch p.observations
        where p.email = ?1
    """, email).firstResult();
    }
}