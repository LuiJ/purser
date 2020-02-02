package root.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabelRepository extends JpaRepository<Label, UUID>
{
    @Query("SELECT l FROM Label l WHERE l.account = :account AND l.name LIKE CONCAT(:namePrefix, '%')")
    List<Label> findByNamePrefixAndAccount(String namePrefix, Account account);

    Label findByName(String name);

    Optional<Label> findByIdAndAccount(UUID id, Account account);

    List<Label> findAllByAccount(Account account);
}