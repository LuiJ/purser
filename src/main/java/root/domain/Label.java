package root.domain;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "label")
@Access(AccessType.FIELD)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(of = {"name"})
public class Label
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;
}
