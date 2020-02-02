package root.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.domain.Account;
import root.domain.AccountRepository;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AccountService
{
    private final AccountRepository accountRepository;

    Account get(String id)
    {
        UUID accountId = UUID.fromString(id);
        return accountRepository.findById(accountId)
                .orElseThrow(noSuchElementException(id));
    }

    private Supplier<NoSuchElementException> noSuchElementException(String id)
    {
        String message = String.format("Account [%s] was not found", id);
        return () -> new NoSuchElementException(message);
    }
}
