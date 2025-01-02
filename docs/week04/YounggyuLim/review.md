- 정적 팩토리 메서드와 빌더 패턴을 활용한 예시

1. 생성 의도를 확실히 하기위한 이름을 지을 수 있음
2. 빌더 패턴을 활용한 가독성
3. 빌더 패턴을 활용한 선택적 필드 지원

```java
@Getter
@Builder
public class FriendInfoDto {
    private String friendCode;
    private String nickname;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String bio;

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
2. JpaRepository를 상속받아서 사용하고 save, delete 등 디폴트 메서드들을 제공
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


