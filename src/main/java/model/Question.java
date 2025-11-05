package model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@Table(name = "question")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Question {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private Test mainTest;

    @ToString.Exclude
    @OneToMany(mappedBy = "mainQuestion", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<Answer> answers;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "text")
    private String text;

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
        answers.forEach(a -> a.setMainQuestion(this));
    }

}
