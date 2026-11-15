package com.bori.christmas_letter.letter;

import com.bori.christmas_letter.tree.ChristmasTree;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "letters")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tree_id", nullable = false)
    private ChristmasTree tree;

    @Column(name = "sender_name", nullable = false, length = 20)
    private String senderName;

    @Column(nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "ornament_type", nullable = false, length = 30)
    private OrnamentType ornamentType;

    @Column(name = "position_x", nullable = false)
    private int positionX;

    @Column(name = "position_y", nullable = false)
    private int positionY;

    @Column(name = "identity_revealed", nullable = false)
    private boolean identityRevealed;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Letter() {
    }

    public Letter(
            ChristmasTree tree,
            String senderName,
            String content,
            OrnamentType ornamentType,
            int positionX,
            int positionY,
            Instant createdAt
    ) {
        this.tree = tree;
        this.senderName = senderName;
        this.content = content;
        this.ornamentType = ornamentType;
        this.positionX = positionX;
        this.positionY = positionY;
        this.identityRevealed = false;
        this.createdAt = createdAt;
    }

    public void revealIdentity() {
        this.identityRevealed = true;
    }

    public Long getId() {
        return id;
    }

    public ChristmasTree getTree() {
        return tree;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public OrnamentType getOrnamentType() {
        return ornamentType;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public boolean isIdentityRevealed() {
        return identityRevealed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
