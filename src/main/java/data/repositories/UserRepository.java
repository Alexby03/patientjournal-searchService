package data.repositories;

import data.entities.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    @WithTransaction
    public Uni<User> findByEmail(String email) {
        return find("email", email).firstResult();
    }

    @WithTransaction
    public Uni<User> findByFullName(String fullName) {
        return find("fullName", fullName).firstResult();
    }

    @WithTransaction
    public Uni<List<User>> listAllUsers(int pageIndex, int pageSize) {
        return findAll().page(pageIndex, pageSize).list();
    }

    @WithTransaction
    public Uni<Long> countTotalUsers() {
        return count();
    }

}
