package root.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>
{
    Optional<Payment> findByIdAndAccountAndCategory(UUID id, Account account, Category category);

    Long countByAccountAndCategory(Account account, Category category);

    Long countByAccountAndLabelsContains(Account account, Label label);
}