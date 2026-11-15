package com.bori.christmas_letter.tree;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChristmasTreeRepository extends JpaRepository<ChristmasTree, Long> {

    Optional<ChristmasTree> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
