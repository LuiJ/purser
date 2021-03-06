package root.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Access(AccessType.FIELD)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = {"account"})
public class Payment
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NonNull
    private BigDecimal amount;

    private String description;

    @NonNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @NonNull
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    @NonNull
    @OneToOne
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Label> labels;
}
