package root.domain;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "category")
@Access(AccessType.FIELD)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(of = {"name"})
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NonNull
    private String name;

    private Integer iconCode;

    @NonNull
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;
}
