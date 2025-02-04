- 정적 팩토리 메서드와 빌더 패턴을 활용한 예시

1. 생성 의도를 확실히 하기위한 이름을 지을 수 있음
2. 빌더 패턴을 활용한 가독성
3. 빌더 패턴을 활용한 선택적 필드 지원

```java
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendInfoDto {
    private final String friendCode;
    private final String nickname;
    private final String profileImageUrl;
    private final String backgroundImageUrl;
    private final String bio;

    public static FriendInfoDto fromMember(Member member) {
        return FriendInfoDto.builder()
                .friendCode(member.getFriendCode())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileUrl())
                .backgroundImageUrl(member.getBackgroundUrl())
                .bio(member.getBio())
                .build();
    }
}
```

- 클래스와 멤버의 접근 권한을 최소화 하는 예시

1. 상수 필드인 액세스토큰, 리프레시 토큰의 만료일을 제외하곤 private 필드만 갖음
2. public 클래스는 상수 필드 외엔 어떠한 public 도 갖지 않아야 함

```java
public class JwtTokenizer {
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; //30분
    public static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; //7일
}
```

- 컴포지션을 활용한 예시

1. MemberLoginResponseDto를 활용해 재사용성 증가
2. 각자의 클래스의 역할을 담당하기에 응집도가 증가

```java
@Getter
@Builder
public class LoginResponseDto {
    private String accessToken;
    private MemberLoginResponseDto memberLoginResponseDto;
}

@Getter
@NoArgsConstructor
public class MemberLoginResponseDto {
    private String accountId;
    private String nickname;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String bio;

    public MemberLoginResponseDto(String accountId, String nickname, String profileImageUrl, String backgroundImageUrl, String bio) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.bio = bio;
    }
}
```

- 골격구현 + 위임 을 활용한 예시

1. JpaRepository 를 골격구현한 예시
2. JpaRepository 를 상속받아서 사용하고 save, delete 등 디폴트 메서드들을 제공
3. 추가적인 메서드들은 findByLoginId 같이 직접 작성
4. 예시와 같은 위임은 아니지만 원리는 같음

- JpaRepository 의 위임 흐름
1. memberRepository.findByLoginId("example") 호출
2. MemberRepository 구현한 프록시객체 생성
3. 프록시 객체에서 메서드명을 분석하여 쿼리 생성
4. 생성된 쿼리를 entityManager 에 위임
5. 결과 반환

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByFriendCode(String friendCode);
}
```

- 클래스는 꼭 필요한 경우가 아니면 불변으로 설계할것

1. 정적 팩토리 메서드를 이용해 생성 의도 전달
2. 캡슐화를 통해 생성 로직을 유연하게 관리

```java
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberLoginResponseDto {
    private final String accountId;
    private final String nickname;
    private final String profileImageUrl;
    private final String backgroundImageUrl;
    private final String bio;

    public static MemberLoginResponseDto createMemberLoginResponseDto (String accountId, String nickname, String profileImageUrl, String backgroundImageUrl, String bio) {
        return new MemberLoginResponseDto(accountId, nickname, profileImageUrl, backgroundImageUrl, bio);
    }
}
```


- 접근자 활용해서 클래스내 에서만 재활용
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() ->
                new CustomException(ExceptionCode.ID_MISSMATCH));
    }

    @Transactional
    public void uploadProfileImage(MultipartFile multipartFile, String accountId) {
        String profileUrl = uploadFile(multipartFile);
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.getProfileUrl().equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png")) {
            deleteFileByUrl(member.getProfileUrl());
        }
        member.updateProfileUrl(profileUrl);
        memberRepository.save(member);
    }

    @Transactional
    public void uploadBackgroundImage(MultipartFile multipartFile, String accountId) {
        String backgroundUrl = uploadFile(multipartFile);
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (!member.getBackgroundUrl().equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg")) {
            deleteFileByUrl(member.getBackgroundUrl());
        }
        member.updateBackgroundUrl(backgroundUrl);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteProfileImage(String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        String profileUrl = member.getProfileUrl();
        if (profileUrl.equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png")) {
            throw new CustomException(ExceptionCode.CANNOT_DELETE_DEFAULT_IMAGE);
        }
        deleteFileByUrl(profileUrl);
        member.deleteProfileImage();
        memberRepository.save(member);
    }

    @Transactional
    public void deleteBackgroundImage(String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        String backgroundUrl = member.getBackgroundUrl();
        if (backgroundUrl.equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg")) {
            throw new CustomException(ExceptionCode.CANNOT_DELETE_DEFAULT_IMAGE);
        }
        deleteFileByUrl(backgroundUrl);
        member.deleteBackgroundImage();
        memberRepository.save(member);
    }

    private String uploadFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_FILE_FORMAT);
        }

        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAILED);
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }


    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 : " + fileName + " 입니다.");
        }
    }

    private void deleteFileByUrl(String fileUrl) {
        if (!fileUrl.contains(bucket)) {
            throw new CustomException(ExceptionCode.INVALID_FILE_URL);
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
            log.info("S3에서 파일 삭제 : " + fileName);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.FILE_DELETE_FAILED);
        }

    }

    @Transactional
    public void updateBio(String bio, String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        member.updateBio(bio);
        memberRepository.save(member);
    }
    
}
```


