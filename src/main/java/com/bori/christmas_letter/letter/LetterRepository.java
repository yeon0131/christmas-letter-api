package com.bori.christmas_letter.letter;

import com.bori.christmas_letter.tree.ChristmasTree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findAllByTreeOrderByCreatedAtAsc(ChristmasTree tree);

    long countByTree(ChristmasTree tree);

    Optional<Letter> findByIdAndTree(Long id, ChristmasTree tree);
}
