package com.bori.christmas_letter.tree;

import com.bori.christmas_letter.letter.OrnamentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class TreeDtos {

    private TreeDtos() {
    }

    public record CreateTreeRequest(
            @NotBlank(message = "트리 주인 이름을 입력해 주세요.")
            @Size(max = 30, message = "이름은 30자 이하로 입력해 주세요.")
            String ownerName,

            @NotNull(message = "트리 테마를 선택해 주세요.")
            TreeTheme theme
    ) {
    }

    public record CreateTreeResponse(
            String slug,
            String adminKey,
            String sharePath,
            String managePath,
            Instant revealAt
    ) {
    }

    public record OrnamentResponse(
            Long id,
            OrnamentType ornamentType,
            int positionX,
            int positionY,
            Instant createdAt
    ) {
    }

    public record PublicTreeResponse(
            String slug,
            String ownerName,
            TreeTheme theme,
            Instant revealAt,
            boolean locked,
            long letterCount,
            List<OrnamentResponse> ornaments
    ) {
    }

    public record OwnerLetterResponse(
            Long id,
            String content,
            String senderName,
            boolean identityRevealed,
            OrnamentType ornamentType,
            int positionX,
            int positionY,
            Instant createdAt
    ) {
    }

    public record OwnerTreeResponse(
            String slug,
            String ownerName,
            TreeTheme theme,
            Instant revealAt,
            boolean locked,
            long letterCount,
            List<OrnamentResponse> ornaments,
            List<OwnerLetterResponse> letters
    ) {
    }
}
