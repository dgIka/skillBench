package model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_result")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private Test test;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "true_answers")
    private int trueAnswers;

    @Column(name = "false_answers")
    private int falseAnswers;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        this.finishedAt = LocalDateTime.now();
    }

}
