# Golden Master 자동화 보고서 Prompting

## 사용 프롬프트

### 1. Golden Master 자동화 구현 프롬프트

```text
@TexttestFixture.java @GildedRose.java

[P] 회귀 테스트(Approval/Golden Master) 설계 전문가입니다.
[C] Java 21, JUnit 5, Maven
[T] TexttestFixture 출력 기반 Golden Master를 설계/구현해줘.
    1) 기준 출력(golden_master_expected.txt)을 생성/보관하는 방법
    2) 테스트에서 actual 출력과 파일 비교하는 방법
    3) CI에서 mvn test로 자동 실행되는 구성
    4) (선택) Approvals 테스트 적용 가능성
[F] 테스트 코드 + 파일 저장/비교 구현 + 실행 방법
```

### 2. 보고서 내보내기 프롬프트

```text
이번에 한 내용을 report 폴더에 07_GoldenMaster자동화 _보고서.md 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더에 07_GoldenMaster자동화 _보고서-Prompting.md 파일로 내보내줘
```

## 산출물

# Gilded Rose Golden Master 자동화 보고서

## 1. 목적과 범위

본 문서는 `TexttestFixture.java`의 콘솔 출력을 기준으로 Golden Master 회귀 테스트를 설계하고 구현한 결과를 정리한다.

대상 환경과 파일은 다음과 같다.

- 기술 환경: Java 21, Maven, JUnit 5
- 기준 출력 생성 대상: `src/test/java/com/gildedrose/TexttestFixture.java`
- 검증 대상 로직: `src/main/java/com/gildedrose/GildedRose.java`
- 테스트 코드: `src/test/java/com/gildedrose/GoldenMasterTest.java`
- 기준 출력 파일: `src/test/resources/golden_master_expected.txt`

Golden Master 테스트의 목적은 리팩토링이나 신규 기능 추가 이후에도 기존 `TexttestFixture` 출력이 의도치 않게 변하지 않았는지 자동으로 확인하는 것이다.

## 2. 기준 출력 생성 및 보관 방법

기준 출력은 현재 정상으로 판단한 `TexttestFixture` 실행 결과를 파일로 고정한다.

이번 구현에서는 `TexttestFixture`를 30일 기준으로 실행한 출력을 Golden Master로 저장했다.

```powershell
mvn -q -DskipTests test-compile
cmd /c "java -cp target/classes;target/test-classes com.gildedrose.TexttestFixture 30 > src\test\resources\golden_master_expected.txt"
```

저장 위치는 Maven 표준 테스트 리소스 경로인 `src/test/resources/golden_master_expected.txt`이다.

이 위치에 보관하면 `mvn test` 실행 시 Maven이 자동으로 `target/test-classes`에 복사하므로 테스트 코드에서 클래스패스 리소스로 읽을 수 있다.

기준 파일은 다음 원칙으로 관리한다.

1. 의도하지 않은 출력 변경이 발생하면 테스트 실패로 감지한다.
2. 요구사항 변경으로 출력 변화가 의도된 경우에만 `golden_master_expected.txt`를 갱신한다.
3. 갱신 후에는 변경된 Golden Master 파일을 코드 리뷰 대상으로 포함해 동작 변화가 합당한지 확인한다.

## 3. 테스트에서 Actual 출력과 파일 비교 방법

`GoldenMasterTest`는 `TexttestFixture.main(new String[] { "30" })`을 직접 호출한다.

테스트는 `System.out`을 `ByteArrayOutputStream`으로 임시 교체해 실제 콘솔 출력을 캡처하고, 클래스패스에서 읽은 `golden_master_expected.txt`와 비교한다.

핵심 비교 절차는 다음과 같다.

```java
String expected = readGoldenMaster();
String actual = captureTexttestFixtureOutput("30");

assertEquals(normalizeLineEndings(expected), normalizeLineEndings(actual));
```

Windows와 Unix 계열 환경의 줄바꿈 차이로 인한 불필요한 실패를 줄이기 위해 `\r\n`을 `\n`으로 정규화한 뒤 비교한다.

구현된 테스트 클래스의 주요 책임은 다음과 같다.

1. `readGoldenMaster()`: `src/test/resources/golden_master_expected.txt`를 UTF-8 문자열로 읽는다.
2. `captureTexttestFixtureOutput(String days)`: `TexttestFixture` 실행 결과를 문자열로 캡처한다.
3. `normalizeLineEndings(String value)`: 운영체제별 줄바꿈 차이를 정규화한다.
4. `assertEquals(...)`: 기준 출력과 실제 출력이 완전히 동일한지 검증한다.

## 4. CI에서 자동 실행되는 구성

테스트 클래스 이름을 `GoldenMasterTest`로 작성했기 때문에 Maven Surefire의 기본 테스트 탐색 규칙에 의해 `mvn test`에서 자동 실행된다.

별도 CI 설정이 없어도 CI 서버에서 다음 명령만 실행하면 기존 단위 테스트와 Golden Master 테스트가 함께 수행된다.

```bash
mvn test
```

이번 실행 결과는 다음과 같다.

```text
Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

실행된 테스트는 다음과 같다.

1. `com.gildedrose.GildedRoseTest`: 기존 규칙 기반 단위 테스트 34개
2. `com.gildedrose.GoldenMasterTest`: `TexttestFixture` 출력 기반 Golden Master 테스트 1개

## 5. ApprovalTests 적용 가능성

현재 `pom.xml`에는 `com.approvaltests:approvaltests` 의존성이 이미 포함되어 있어 ApprovalTests 방식으로 확장할 수 있다.

ApprovalTests를 적용하면 직접 `golden_master_expected.txt`를 읽고 비교하는 대신 다음과 같은 흐름을 사용할 수 있다.

```java
Approvals.verify(actual);
```

이 방식은 실패 시 `.received.txt`와 `.approved.txt`를 생성해 차이를 승인하는 워크플로를 제공한다.

다만 현재 프로젝트에서는 직접 파일 비교 방식이 더 단순하고 CI 친화적이다. 별도 승인 도구나 IDE 연동 없이 `mvn test`만으로 실패 여부가 명확히 드러나며, 기준 파일 위치도 `src/test/resources/golden_master_expected.txt`로 고정되어 관리하기 쉽다.

ApprovalTests는 다음 상황에서 도입을 검토할 수 있다.

1. Golden Master 파일이 여러 개로 늘어나는 경우
2. 승인 전후 파일 비교 워크플로가 필요한 경우
3. 팀에서 `.approved` / `.received` 파일 관리를 표준화하려는 경우

## 6. 실행 및 유지보수 방법

일반적인 회귀 테스트 실행 명령은 다음과 같다.

```bash
mvn test
```

출력이 의도적으로 변경된 경우 Golden Master 갱신 절차는 다음과 같다.

```powershell
mvn -q -DskipTests test-compile
cmd /c "java -cp target/classes;target/test-classes com.gildedrose.TexttestFixture 30 > src\test\resources\golden_master_expected.txt"
mvn test
```

갱신 시에는 다음 사항을 확인해야 한다.

1. `GildedRose.java`의 변경이 요구사항에 맞는지 검토한다.
2. `golden_master_expected.txt`의 diff가 기대한 출력 변화와 일치하는지 확인한다.
3. 기존 `GildedRoseTest`의 세부 규칙 테스트와 `GoldenMasterTest`가 모두 Green인지 확인한다.

## 7. 결론

이번 작업으로 `TexttestFixture` 기반 Golden Master 회귀 테스트가 Maven/JUnit 5 테스트 흐름에 통합되었다.

기준 출력은 `src/test/resources/golden_master_expected.txt`에 보관되며, `GoldenMasterTest`가 실행 시점의 실제 출력과 완전 비교한다.

따라서 향후 `GildedRose.java`를 리팩토링하거나 신규 규칙을 추가할 때, 세부 단위 테스트뿐 아니라 전체 fixture 출력의 회귀 여부도 `mvn test` 한 번으로 확인할 수 있다.
