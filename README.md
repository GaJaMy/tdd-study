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
    - @ExtendWith : 
    - @Mock
    - @InjectMocks
    - 
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

   