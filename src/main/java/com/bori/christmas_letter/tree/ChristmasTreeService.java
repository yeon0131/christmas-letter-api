package com.bori.christmas_letter.tree;

import com.bori.christmas_letter.common.AdminKeyHasher;
import com.bori.christmas_letter.common.ApiException;
import com.bori.christmas_letter.letter.Letter;
import com.bori.christmas_letter.letter.LetterDtos;
import com.bori.christmas_letter.letter.LetterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.bori.christmas_letter.tree.TreeDtos.CreateTreeRequest;
import static com.bori.christmas_letter.tree.TreeDtos.CreateTreeResponse;
import static com.bori.christmas_letter.tree.TreeDtos.OrnamentResponse;
import static com.bori.christmas_letter.tree.TreeDtos.OwnerLetterResponse;
import static com.bori.christmas_letter.tree.TreeDtos.OwnerTreeResponse;
import static com.bori.christmas_letter.tree.TreeDtos.PublicTreeResponse;

@Service
public class ChristmasTreeService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final int[][] ORNAMENT_POSITIONS = {
            {48, 22}, {39, 29}, {58, 31}, {31, 38}, {49, 40}, {66, 41},
            {24, 48}, {40, 50}, {57, 49}, {73, 52}, {31, 58}, {49, 57},
            {64, 60}, {20, 65}, {38, 66}, {55, 67}, {74, 69}, {29, 74},
            {46, 73}, {63, 76}, {18, 79}, {37, 81}, {55, 82}, {77, 80}
    };

    private final ChristmasTreeRepository treeRepository;
    private final LetterRepository letterRepository;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public ChristmasTreeService(
            ChristmasTreeRepository treeRepository,
            LetterRepository letterRepository,
            Clock clock
    ) {
        this.treeRepository = treeRepository;
        this.letterRepository = letterRepository;
        this.clock = clock;
    }

    @Transactional
    public CreateTreeResponse createTree(CreateTreeRequest request) {
        Instant now = clock.instant();
        String adminKey = generateAdminKey();
        String slug = generateUniqueSlug();
        Instant revealAt = nextChristmas(now);

        ChristmasTree tree = new ChristmasTree(
                slug,
                request.ownerName().trim(),
                request.theme(),
                AdminKeyHasher.hash(adminKey),
                revealAt,
                now
        );
        treeRepository.save(tree);

        return new CreateTreeResponse(
                slug,
                adminKey,
                "/tree/" + slug,
                "/my-tree/" + slug + "#key=" + adminKey,
                revealAt
        );
    }

    @Transactional(readOnly = true)
    public PublicTreeResponse getPublicTree(String slug) {
        ChristmasTree tree = findTree(slug);
        Instant now = clock.instant();
        List<Letter> letters = letterRepository.findAllByTreeOrderByCreatedAtAsc(tree);

        return new PublicTreeResponse(
                tree.getSlug(),
                tree.getOwnerName(),
                tree.getTheme(),
                tree.getRevealAt(),
                tree.isLocked(now),
                letters.size(),
                letters.stream().map(this::toOrnament).toList()
        );
    }

    @Transactional
    public OrnamentResponse addLetter(String slug, LetterDtos.CreateLetterRequest request) {
        ChristmasTree tree = findTree(slug);
        Instant now = clock.instant();
        if (!tree.isLocked(now)) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "LETTER_SUBMISSION_CLOSED",
                    "편지가 공개된 뒤에는 새 편지를 달 수 없습니다."
            );
        }

        long letterCount = letterRepository.countByTree(tree);
        int[] position = ORNAMENT_POSITIONS[(int) (letterCount % ORNAMENT_POSITIONS.length)];
        int round = (int) (letterCount / ORNAMENT_POSITIONS.length);
        int xOffset = (round % 3) - 1;

        Letter letter = new Letter(
                tree,
                request.senderName().trim(),
                request.content().trim(),
                request.ornamentType(),
                position[0] + xOffset,
                position[1],
                now
        );

        return toOrnament(letterRepository.save(letter));
    }

    @Transactional(readOnly = true)
    public OwnerTreeResponse getOwnerTree(String slug, String adminKey) {
        ChristmasTree tree = findTree(slug);
        validateAdminKey(tree, adminKey);

        Instant now = clock.instant();
        boolean locked = tree.isLocked(now);
        List<Letter> letters = letterRepository.findAllByTreeOrderByCreatedAtAsc(tree);
        List<OwnerLetterResponse> ownerLetters = locked
                ? letters.stream().map(this::toLockedOwnerLetter).toList()
                : letters.stream().map(this::toOwnerLetter).toList();

        return new OwnerTreeResponse(
                tree.getSlug(),
                tree.getOwnerName(),
                tree.getTheme(),
                tree.getRevealAt(),
                locked,
                letters.size(),
                letters.stream().map(this::toOrnament).toList(),
                ownerLetters
        );
    }

    @Transactional
    public LetterDtos.GuessResponse guessSender(
            String slug,
            Long letterId,
            String adminKey,
            LetterDtos.GuessRequest request
    ) {
        ChristmasTree tree = findTree(slug);
        validateAdminKey(tree, adminKey);
        if (tree.isLocked(clock.instant())) {
            throw new ApiException(
                    HttpStatus.LOCKED,
                    "LETTERS_LOCKED",
                    "편지는 크리스마스에 열 수 있습니다."
            );
        }

        Letter letter = findLetter(tree, letterId);
        if (letter.isIdentityRevealed()) {
            return new LetterDtos.GuessResponse(true, letter.getSenderName(), "이미 정답을 맞혔어요!");
        }

        boolean correct = normalizeName(letter.getSenderName()).equals(normalizeName(request.answer()));
        if (correct) {
            letter.revealIdentity();
            return new LetterDtos.GuessResponse(true, letter.getSenderName(), "정답이에요! 편지를 쓴 친구를 찾았어요.");
        }

        return new LetterDtos.GuessResponse(false, null, "아직 아니에요. 편지를 다시 읽어보세요.");
    }

    @Transactional
    public void deleteLetter(String slug, Long letterId, String adminKey) {
        ChristmasTree tree = findTree(slug);
        validateAdminKey(tree, adminKey);
        letterRepository.delete(findLetter(tree, letterId));
    }

    private ChristmasTree findTree(String slug) {
        return treeRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "TREE_NOT_FOUND",
                        "존재하지 않는 트리입니다."
                ));
    }

    private Letter findLetter(ChristmasTree tree, Long letterId) {
        return letterRepository.findByIdAndTree(letterId, tree)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "LETTER_NOT_FOUND",
                        "존재하지 않는 편지입니다."
                ));
    }

    private void validateAdminKey(ChristmasTree tree, String adminKey) {
        if (!AdminKeyHasher.matches(adminKey, tree.getAdminKeyHash())) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "INVALID_ADMIN_KEY",
                    "트리 관리 키가 올바르지 않습니다."
            );
        }
    }

    private OrnamentResponse toOrnament(Letter letter) {
        return new OrnamentResponse(
                letter.getId(),
                letter.getOrnamentType(),
                letter.getPositionX(),
                letter.getPositionY(),
                letter.getCreatedAt()
        );
    }

    private OwnerLetterResponse toOwnerLetter(Letter letter) {
        return new OwnerLetterResponse(
                letter.getId(),
                letter.getContent(),
                letter.getSenderName(),
                letter.isIdentityRevealed(),
                letter.getOrnamentType(),
                letter.getPositionX(),
                letter.getPositionY(),
                letter.getCreatedAt()
        );
    }

    private OwnerLetterResponse toLockedOwnerLetter(Letter letter) {
        return new OwnerLetterResponse(
                letter.getId(),
                null,
                letter.getSenderName(),
                letter.isIdentityRevealed(),
                letter.getOrnamentType(),
                letter.getPositionX(),
                letter.getPositionY(),
                letter.getCreatedAt()
        );
    }

    private String generateUniqueSlug() {
        String slug;
        do {
            slug = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        } while (treeRepository.existsBySlug(slug));
        return slug;
    }

    private String generateAdminKey() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Instant nextChristmas(Instant now) {
        ZonedDateTime nowInSeoul = ZonedDateTime.ofInstant(now, SEOUL);
        int year = nowInSeoul.getYear();
        ZonedDateTime christmas = LocalDate.of(year, 12, 25).atStartOfDay(SEOUL);
        if (!nowInSeoul.isBefore(christmas)) {
            christmas = LocalDate.of(year + 1, 12, 25).atStartOfDay(SEOUL);
        }
        return christmas.toInstant();
    }

    private String normalizeName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFKC)
                .trim()
                .replace(" ", "")
                .toLowerCase(Locale.ROOT);
    }
}
