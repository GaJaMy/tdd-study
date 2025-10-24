# 테스트 코드

## 테스트 코드

### 단위 테스트
- 단일 기능, 혹은 작은 단위의 함수 / 객체등을 테스트 하는 것
- 시스템 특정 단위 만을 대상으로 의도대로 동작하는 지 검증
- Entity의 비즈니스로직, Service의 비즈니스 로직이 해당

### 슬라이스 테스트
- 특정 계층을 분리하여 해당 계층의 기능을 테스트 하는 것
- 대상은 데이터 접근, 서비스, API 계층이다.
- 가장 햇갈리는 것은 서비스 계층 슬라이스 테스트와 단위 테스트다.
- 다른 계층 같은 경우는 각 계층을 위한 어노테이션들이 존재한다.
  - API 계층(보통 컨트롤러를 테스트 한다고 생각하면 된다.)
    - @WebMvcTest : 웹 레이어 테스트를 위한 어노테이션
      - @Controller, @ControllerAdvice, @JpaComponent, @Converter, GenericConverter, Filter, WebMvcConfigurer, HandlerMethodArgumentResolver 등만 Bean 등록
      - @Service, @Repository 등은 Bean으로 등록되지 않는다.
    - @MockBean : @WebMvcTest를 사용할 때 @Service를 Mock으로 등록하기 위한 어노테이션 
      - @WebMvcTest에 의해 @Service Bean이 등록 되지 않기 때문에, 가짜 Bean으로 등록하는 것이다.
  - 서비스 계층(보통 @Service 어노테이션이나 UseCase를 테스트 한다고 보면 된다.)
    - @ExtendWith : JUnit 5에서 제공하는 어노테이션으로 실행 환경을 제공하는 클래스를 선택할 수 있다.
      - @ExtendWith(SpringExtension.class)
        - 스프링 컨테이너를 로드하지만 컴포넌트 스캔은 하지 않아 프로젝트 내에 정의된 컴포넌트들이 들어가있지 않다.
          따라서 테스트 코드상에서 직접 Bean을 등록하고 @Autowired를 통해 의존성 주입해야한다.
          스프링 컨테이너를 로드하기 때문에 Bean을 Mocking하기위한 @MockBean 기능도 사용 가능하다.
          서블릿 컨테이너를 띄울 필요는 없지만 스프링 컨테이너 로딩이 필요할 때 사용한다.
      - @ExtendWith(MockitoExtension.class)
        - 스프링 컨테이너를 로드하지않고 Mokito 프레임 워크를 사용해 테스트에서 Mocking이 필요한 경우 사용한다.
          @Mock, @InjectMocks, @Spy 등의 어노테이션과 함께 사용한다.
          스프링의 도움 없이 테스트에 Mocking을 사용한 순수한 단위 테스트가 필요할 때 사용한다. 
          서비스 레이어에서 @ExtendWith(MockitoExtension.class)를 사용한 이유는
          Servcie는 웹 계층이 아닌 비지니스 로직에 집중되어 있으며
          Controller와 Repository의 중간에 있는 계층으로 이 계층만을 테스트하기 위해서는 두 의존관계를 끊어줄 필요가 있다.
          따라서 이 어노테이션을 통해 비지니스 로직을 테스트하기 위해서 스프링과 관련된 추가 비용 없이
          Service가 의존하는 Repository를 Mock으로 주입해 초기화 시 의존성 문제만 해결해
          순수한 자바 코드로 비즈니스 로직만을 테스트하는데 집중할 수 있다.
    - @Mock
      - Mockito 라이브러리에서 제공하는 어노테이션으로, Mock 객체를 생성하기 위해 사용된다.
        MockBean과 다른 점으로는 스프링 컨테이너에 Bean으로 등록되지 않기 때문에
        스프링 컨테이너를 로드할 필요가 없을 경우 사용되고 @InjectMocks을 사용해 직접 의존성 주입해주어야 한다.
        말 그대로 특정 객체를 모방한 객체이므로 이 Mock 객체가 갖고 있는 메서드의 반환 값을 모의로 설정할 수 있다.
        위 코드에서는 특정 Service의 비즈니스 로직이 정상적으로 작동되는지를 확인 하기 위해
        Service가 의존하는 Mock 객체의 반환 값을 임의로 설정해 테스트한 것이다.
    - @InjectMocks
      - Mockito에서 제공하는 어노테이션으로, Mock 객체를 테스트하고자 하는 클래스에 자동으로 주입하기 위해 사용된다.
        @Mock이나 @Spy로 생성된 객체들을 해당 어노테이션이 붙은 클래스의 인스턴스에 주입한다.
        위 예시에서는 ReservationRepository Mock 객체를 ReservationService에 주입해 의존성 문제를 해결하고
        ReservationService의 비즈니스 로직들을 테스트하고 있다.
  - Repository 계층
    - @DataJpaTest
      - Spring Date Jpa를 테스트할 때 사용하는 어노테이션이다.
        해당 테스트는 기본적으로 인메모리 임베디드 DB를 생성하고 @Entity가 붙은 엔티티 클래스들을 스캔한다.
        또한 Spring Data JPA 관련 설정만 Bean 등록되며 내부에 @Transactional 어노테이션이 선언되어 있어
        테스트마다 롤백되어 테스트의 독립성을 보장해준다.
        추가로 테스트하기 위한 클래스를 Bean 등록해 사용해야 한다면 @Import 어노테이션을 사용할 수 있다.
    - @JdbcTest
      - @Entity가 붙은 엔티티 클래스를 스캔한다는 점만 제외하면
        위와 동일하게 인메모리 임베디드 DB를 생성하고 JDBC 관련 설정만 Bean 등록하며 @Transactional 어노테이션이 선언되어있다.
        이름 그대로 JdbcTemplate를 사용하는 Repository를 테스트할 때 이 어노테이션을 사용해 테스트한다.
    - @AutoConfigureTestDatabase
      - 기본설정인 인메모리 임베디드 DB를 사용하지 않고 실제 사용하는 DB를 테스트하고자 한다면
        다음과 같은 옵션을 통해 실제 사용되는 DB에 접근하여 테스트할 수 있다.
        (properties 또는 yml에 설정 DB를 사용한다.)
      - @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
## 테스트 코드 작성법
1. 내가 개발 할 기능에 대한 도메인, 엔티티, 서비스 등에 대해서 정의를 한다.
   - 정말 단순히 클래스와 비어있는 메서드만 만들어 둔다.
2. 테스트 케이스를 생각한다.
   - 테스트 할 메서드 코드를 진행 하기 위해 필요한 파라미터와 그에 대한 결과를 예상해서 만든다.
3. 테스트에 필요한 의존성을 생각한다.
   - 서비스의 단위 테스트를 생각하면, repository등의 의존성이 필요하다. 이때 mock 객체의 주입을 생각한다.
4. 테스트 코드를 작성하고 테스트를 진행한다.
   - 실패 하는게 당연하다. 아직 아무것도 안에 없으니까.
5. 실제 구현을 진행한다.
   - 각 테스트 케이스를 성공으로 만들며 실제 구현을 반복한다.


### 출처
https://hstory0208.tistory.com/entry/Spring-스프링-테스트-어노테이션-알아보기-feat-슬라이스-테스트 [< Hyun / Log >:티스토리]
   