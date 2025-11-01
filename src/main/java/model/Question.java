package model;

import jakarta.persistence.*;
import lombok.*;

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
    private List<Answer> answers;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "text")
    private String text;

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
        answers.forEach(a -> a.setMainQuestion(this));
    }

}
