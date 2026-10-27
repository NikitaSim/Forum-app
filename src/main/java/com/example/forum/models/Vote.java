package com.example.forum.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "votes", uniqueConstraints = @UniqueConstraint(
        name = "uq_votes_user_post", columnNames = {"user_id", "post_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false, length = 4)
    private VoteType type;

    public Vote(Long id, User user, Post post, VoteType type) {
        this.id=id; this.user=user; this.post=post; this.type=type;
    }
}
