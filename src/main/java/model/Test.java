package model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "test")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Test {

    @ToString.Exclude
    @OneToMany(mappedBy = "mainTest", cascade = CascadeType.PERSIST)
    private List<Question> questions;

    @ToString.Exclude
    @OneToMany(mappedBy = "test")
    private List<TestResult> results;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "theme")
    private String theme;

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        questions.forEach(question -> question.setMainTest(this));
    }


}
