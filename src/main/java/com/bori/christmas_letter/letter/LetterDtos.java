package com.bori.christmas_letter.letter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class LetterDtos {

    private LetterDtos() {
    }

    public record CreateLetterRequest(
            @NotBlank(message = "트리 주인이 알아볼 수 있는 이름을 입력해 주세요.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
            String senderName,

            @NotBlank(message = "편지 내용을 입력해 주세요.")
            @Size(max = 500, message = "편지는 500자 이하로 작성해 주세요.")
            String content,

            @NotNull(message = "오너먼트를 선택해 주세요.")
            OrnamentType ornamentType
    ) {
    }

    public record GuessRequest(
            @NotBlank(message = "생각나는 이름을 입력해 주세요.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
            String answer
    ) {
    }

    public record GuessResponse(
            boolean correct,
            String senderName,
            String message
    ) {
    }
}
