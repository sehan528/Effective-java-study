# Item 5: 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

많은 클래스가 하나 이상의 자원에 의존합니다. 이러한 의존성을 관리하는 방법에 따라 코드의 유연성, 재사용성, 테스트 용이성이 크게 달라질 수 있습니다.

## 잘못된 구현 방식

### 1. 정적 유틸리티 클래스 사용
```java
public class SpellChecker {
    private static final Lexicon dictionary = new KoreanDictionary();
    
    private SpellChecker() {} // 인스턴스화 방지
    
    public static boolean isValid(String word) {
        // dictionary를 사용한 검사 로직
        return dictionary.contains(word);
    }
}
```
**문제점:**
- 특정 사전에 종속됨
- 테스트 시 다른 사전으로 대체 불가능
- 유연성이 떨어짐

### 2. 싱글턴으로 구현
```java
public class SpellChecker {
    private final Lexicon dictionary = new KoreanDictionary();
    
    private static final SpellChecker INSTANCE = new SpellChecker();
    
    private SpellChecker() {}
    
    public static SpellChecker getInstance() { return INSTANCE; }
    
    public boolean isValid(String word) {
        return dictionary.contains(word);
    }
}
```
**문제점:**
- 여전히 하나의 사전에 종속됨
- 테스트 어려움
- 유연성 부족

## 올바른 구현: 의존 객체 주입

### 1. 생성자 주입
```java
public class SpellChecker {
    private final Lexicon dictionary;
    
    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }
    
    public boolean isValid(String word) {
        return dictionary.contains(word);
    }
}

// 사용 예시
Lexicon koreanDictionary = new KoreanDictionary();
SpellChecker spellChecker = new SpellChecker(koreanDictionary);
```

### 2. 팩터리 메서드 패턴 사용
```java
public class SpellChecker {
    private final Lexicon dictionary;
    
    public SpellChecker(Supplier<? extends Lexicon> dictionaryFactory) {
        this.dictionary = dictionaryFactory.get();
    }
    
    // 메서드 구현
}

// 사용 예시
SpellChecker checker = new SpellChecker(() -> new KoreanDictionary());
```

## Spring 프레임워크에서의 의존성 주입

### 1. Configuration을 통한 의존성 주입

#### 잘못된 구현
```java
@Configuration
public class ZeroBaseConfig {
    private static final String ADDRESS = "서울시 강남구"; // 하드코딩된 값
    
    @Bean
    public ServiceClass serviceClass() {
        return new ServiceClass(ADDRESS);
    }
}
```

#### 올바른 구현
```java
@Configuration
public class ZeroBaseConfig {
    @Value("${zerobase.address}")
    private String address;
    
    @Bean
    public ServiceClass serviceClass() {
        return new ServiceClass(address);
    }
}

# application.yml
zerobase:
    address: '서울시 강남구'
```

### 2. 생성자 주입을 통한 테스트 용이성 향상
```java
@Service
public class PhonePatternChecker {
    private final String pattern;
    
    public PhonePatternChecker(@Value("${phone.pattern}") String pattern) {
        this.pattern = pattern;
    }
    
    public boolean isValid(String phone) {
        return phone.matches(pattern);
    }
}

// 테스트 코드
@Test
void testPhonePattern() {
    PhonePatternChecker checker = new PhonePatternChecker("^\\d{3}-\\d{4}-\\d{4}$");
    assertTrue(checker.isValid("010-1234-5678"));
    assertFalse(checker.isValid("010-123-5678"));
}
```

## 의존 객체 주입의 장점

1. **유연성**
    - 구현체를 쉽게 교체 가능
    - 환경별로 다른 구현 사용 가능

2. **테스트 용이성**
    - 목(mock) 객체 주입 가능
    - 단위 테스트 작성 용이

3. **재사용성**
    - 의존성을 외부에서 주입받아 다양한 상황에서 재사용 가능

4. **관심사의 분리**
    - 객체 생성과 사용의 분리
    - 설정과 구현의 분리

## 의존성 주입 프레임워크 활용

### Spring Framework 사용
```java
@Service
public class EmailService {
    private final EmailSender emailSender;
    
    @Autowired  // 생성자가 하나면 생략 가능
    public EmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
}
```

## 결론

1. 클래스가 하나 이상의 자원에 의존한다면 의존 객체 주입을 사용하라
2. 정적 유틸리티 클래스나 싱글턴을 사용하지 말라
3. 의존성을 생성자로 주입받아 불변성을 보장하라
4. 필요한 경우 팩터리 메서드 패턴을 활용하라
5. Spring과 같은 DI 프레임워크를 적극 활용하라

### 체크리스트
- [ ] 클래스가 자원을 직접 생성하고 있지 않은가?
- [ ] 의존성이 생성자를 통해 주입되고 있는가?
- [ ] 필드가 final로 선언되어 불변성이 보장되는가?
- [ ] 테스트가 용이한 구조인가?