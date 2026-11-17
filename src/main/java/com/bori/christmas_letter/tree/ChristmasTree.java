package com.bori.christmas_letter.tree;

import com.bori.christmas_letter.letter.OrnamentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "trees")
public class ChristmasTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String slug;

    @Column(name = "owner_name", nullable = false, length = 30)
    private String ownerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TreeTheme theme;

    @Enumerated(EnumType.STRING)
    @Column(name = "favorite_fish", nullable = false, length = 30)
    private OrnamentType favoriteFish;

    @Column(name = "admin_key_hash", nullable = false, length = 64)
    private String adminKeyHash;

    @Column(name = "reveal_at", nullable = false)
    private Instant revealAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ChristmasTree() {
    }

    public ChristmasTree(
            String slug,
            String ownerName,
            TreeTheme theme,
            String adminKeyHash,
            Instant revealAt,
            Instant createdAt
    ) {
        this.slug = slug;
        this.ownerName = ownerName;
        this.theme = theme;
        this.favoriteFish = OrnamentType.RED_BAUBLE;
        this.adminKeyHash = adminKeyHash;
        this.revealAt = revealAt;
        this.createdAt = createdAt;
    }

    public boolean isLocked(Instant now) {
        return now.isBefore(revealAt);
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public TreeTheme getTheme() {
        return theme;
    }

    public String getAdminKeyHash() {
        return adminKeyHash;
    }

    public Instant getRevealAt() {
        return revealAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
