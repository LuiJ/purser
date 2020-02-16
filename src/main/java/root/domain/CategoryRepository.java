package root.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>
{
    Optional<Category> findByIdAndAccount(UUID id, Account account);

    Optional<Category> findByNameAndAccount(String name, Account account);
}