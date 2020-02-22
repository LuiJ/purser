package root.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>
{
    Optional<Payment> findByIdAndAccount(UUID id, Account account);

    Long countByAccountAndCategory(Account account, Category category);

    Long countByAccountAndLabelsContains(Account account, Label label);
}