package com.bori.christmas_letter.tree;

import com.bori.christmas_letter.common.AdminKeyHasher;
import com.bori.christmas_letter.letter.Letter;
import com.bori.christmas_letter.letter.LetterDtos;
import com.bori.christmas_letter.letter.LetterRepository;
import com.bori.christmas_letter.letter.OrnamentType;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChristmasRevealTests {

    @Test
    void senderNameIsRevealedOnlyAfterCorrectGuessOnChristmas() {
        String adminKey = "owner-secret-key";
        Instant revealAt = Instant.parse("2026-12-24T15:00:00Z");
        Instant christmasMorning = Instant.parse("2026-12-24T15:01:00Z");

        ChristmasTree tree = new ChristmasTree(
                "testslug",
                "션팡",
                TreeTheme.CLASSIC,
                OrnamentType.RED_BAUBLE,
                AdminKeyHasher.hash(adminKey),
                revealAt,
                Instant.parse("2026-11-15T00:00:00Z")
        );
        Letter letter = new Letter(
                tree,
                "루돌프",
                "올해 같이 개발 공부해서 즐거웠어!",
                OrnamentType.GOLD_STAR,
                48,
                40,
                Instant.parse("2026-11-15T01:00:00Z")
        );

        ChristmasTreeRepository treeRepository = mock(ChristmasTreeRepository.class);
        LetterRepository letterRepository = mock(LetterRepository.class);
        when(treeRepository.findBySlug("testslug")).thenReturn(Optional.of(tree));
        when(letterRepository.findByIdAndTree(1L, tree)).thenReturn(Optional.of(letter));

        ChristmasTreeService service = new ChristmasTreeService(
                treeRepository,
                letterRepository,
                Clock.fixed(christmasMorning, ZoneOffset.UTC)
        );

        LetterDtos.GuessResponse wrong = service.guessSender(
                "testslug",
                1L,
                adminKey,
                new LetterDtos.GuessRequest("산타")
        );
        LetterDtos.GuessResponse correct = service.guessSender(
                "testslug",
                1L,
                adminKey,
                new LetterDtos.GuessRequest("루돌프")
        );

        assertThat(wrong.correct()).isFalse();
        assertThat(wrong.senderName()).isNull();
        assertThat(correct.correct()).isTrue();
        assertThat(correct.senderName()).isEqualTo("루돌프");
        assertThat(letter.isIdentityRevealed()).isTrue();
    }
}
