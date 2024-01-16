# java-was-2023

Java Web Application Server 2023

## 프로젝트 정보 

이 프로젝트는 우아한 테크코스 박재성님의 허가를 받아 https://github.com/woowacourse/jwp-was 
를 참고하여 작성되었습니다.

## 좋은 커밋 메세지
### 커밋 메세지 구조
타입(스코프)
1. 주제(제목)으로 50글 이내로 작성
2. 제목의 첫 글자는 대문자로 작성
3. 명령문으로 사용하며 과거형(x)
4. 마침표를 넣지 않는다 <br>

본문
1. 각 행은 72글자 내로 제한하여 작성
2. 어떻게 보다는 무엇과 왜를 설명<br>

바닥글 : 생략 가능하며 참조 정보들을 추가하는 용도<br>
+) 제목과 본문은 빈 행으로 구분<br><br>

|타입 이름|내용|
|------|---|
|feat	|새로운 기능에 대한 커밋|
|fix	|버그 수정에 대한 커밋|
|build|빌드 관련 파일 수정 / 모듈 설치 또는 삭제에 대한 커밋|
|chore|그 외 자잘한 수정에 대한 커밋|
|ci	|ci 관련 설정 수정에 대한 커밋|
|docs	|문서 수정에 대한 커밋|
|style	|코드 스타일 혹은 포맷 등에 관한 커밋|
|refactor	|코드 리팩토링에 대한 커밋|
|test	|테스트 코드 수정에 대한 커밋|
|perf	|성능 개선에 대한 커밋|

ex.
~~~~
git commit -m "fix: Safari에서 모달을 띄웠을 때 스크롤 이슈 수정
모바일 사파리에서 Carousel 모달을 띄웠을 때,
모달 밖의 상하 스크롤이 움직이는 이슈 수정.
resolves: #1137
~~~~

## STEP1
### http request의 내용을 읽고 적절하게 파싱하기

#### http 헤더<br>
1. General : 해당 정보를<br> 
+ Request URL : 클라이언트가 요청한 URL
+ Request Method : 클라이언트가 요청한 메서드 (get,post, put, patch, ...)
+ Remote Address : 정보의 출처
+ Referrer Policy : Remote Address를 어디까지 공개할 것인지 정책

2. Request<br>
+ Accept : 클라이언트가 서버로부터 받기 원하는 미디어 타입을 나타냄
+ Accept-Encoding: 클라이언트가 지원하는 콘텐츠 인코딩 방법을 나타냄
+ Accept-Language: 클라이언트가 지원하는 언어
+ Cache-Control: 캐시 동작을 지정하며, 캐시 제어에 대한 지시를 서버에 전달
+ Connection: 클라이언트와 서버 간의 연결 상태를 나타냄
+ Cookie: 이전 서버로부터 받은 쿠키를 서버에게 다시 제공
+ Host: 요청이 전공되는 대상 서버의 호스트 이름과 포트 번호를 지정
+ Sec-Ch-Ua: 클라이언트의 사용자 에이전트인 크롬과 같은 브라우저에서 정의한 정보
+ Sec-Ch-Ua-Mobile: 모바일 장치인지 여부를 나타냄
+ Sec-Ch-Ua-Platform: 클라이언트의 플랫폼 정보를 나타냄
+ Sec-Fetch-Dest: 요청의 목적지를 나타냄
+ Sec-Fetch-Mode: 리소스 획득 시 사용되는 프로세스를 나타냄
+ Sec-Fetch-Site: 요청의 출처를 식별
+ Sec-Fetch-User: 사용자의 행동에 기반하여 리소를 가져오는 방식을 나타냄
+ Upgrade-Insecure-Requests: 1이라면 http버전의 페이지를 열었을 때 https버전으로 업그레이드하도록 지시
+ User-Agent: 클라이언트의 사용자 에이전트, 브라우저나 애플리케이션의 정보를 나타냄

3. Request에서 필요한 정보
  + Accept : 서버는 이 정보를 활용하여 클라이언트가 어떤 종류의 컨텐츠를 선호하는지 이해하고 적절한 응답가능
  + Accept-Encoding : 클라이언트가 지원하는 컨텐츠 인코딩 정보를 통해 데이터를 압축하여 전달
  + Accept-Language : 클라이언트가 지원하는 언어로 전달
  + Upgrade-Insecure-Requests : 이 정보를 통해 https를 선호하는 클라이언트에게 안전한 연결 제공
  + General헤더


### java thread에 대해 공부(Concurrent 패키지)<br>

### 자바 스레드 모델의 역사
|버전|설명|
|----------|----------|
|java1.0-1.1|'Thread'클래스와 'Runnable' 인터페이스를 사용하여 스레드를 생성하고 제어|
|java1.2-1.4| java.util.concurrent 패키지가 소개되어 스레드 풀 및 다양한 동시성 유틸리티를 제공|
|java5-6|java.util.concurrent 패키지의 중요한 기능들과 java.util.concurrent 패키지의 Executor 인터페이스가 소개|
|java7| ForkJoinPool이라는 새로운 프레임워크|
|java8|람다 표현식이 도입|
|java9-14|람다 표현식이 도입, 스트림 api가 추가되어 병렬 처리를 보다 편리하게 할 수 있음|

#### Concurrent
java.util.concurrent 패키지는 다중 스레드 프로그래밍을 지원하는 일련의 클래스 및 인터페이스를 제공<br>
1. 실행자
+ Executor : execute(Runnable)
~~~
Runnable task = () -> {
    // Do something
};

Executor executor = Executors.newSingleThreadExecutor();
executor.execute(task);
~~~
2. 선물
'Future'는 비동기 계산의 결과를 나타냄
~~~
Future<Integer> future = executor.submit(task);

if (future.isDone()) {
    int result = future.get();
}
~~~
3. 스레드 풀
스레드 풀은 작업을 실행하는 데 사용할 수 있는 스레드의 모음. 풀에서 스레디를 괄리하고 재사용하는 메커니즘 제공
~~~
ExecutorService threadPool = Executors.newFixedThreadPool(4);
// Runnable 작업을 스레드풀에 제출
threadPool.submit(task);
// 스레드 풀 작업이 끝나면 종료할 수있음
threadPool.shutdown();
~~~
4. 잠금
공유 리소스에 대한 액세스를 제어하는 메커니즘
~~~
Lock lock = new ReentrantLock();

lock.lock();
try {
    // Access the shared resource
} finally {
    lock.unlock();
}
~~~
5. 원자 변수
원자적으로 업데이트할 수 있는 변수
~~~
AtomicInteger counter = new AtomicInteger();
int value = counter.incrementAndGet();
~~~
6. 싱크로나이저
CountDownLatch를 통해 작업이 완료될 때까지 기다림
~~~
CountDownLatch latch = new CountDownLatch(1);

executor.submit(() -> {
    try {
        // Do something
        latch.countDown();
    } catch (InterruptedException e) {
        // Handle the exception
    }
});

latch.await();
~~~

## STEP2
해당 과정을 처리하면서, request 핸들링할 때 HTTP URL을 통해 자원을 명시하고, HTTP Method를 통해 자원에 대한 CRUD 기능을 적용하므로 관련 공부가 필요하다고 느낌
### 메서드에 따른 헤더
 + GET    : STEP1에서 정리한 내용과 같음
 + POST   : get에서 content-type과 content-length가 추가되고 바디에 json형식으로 데이터를 받아옴
 + PUT    : Post와 같고 변경 데이터를 바디에 받음
 + DELETE : get과 같고 데이터를 바이데 넣지 않고, URL에 포함하여 전달


### REST, Rest API, RESTful

1. REST
+ 정의 : 클라이언트와 서버 사이의 통신 방법으로 웹의 잠정을 최대한 활용하는 아키텍처로 자원의 상태와 자원의 표현에 의한 전달
+ 구체적인 개념 : HTTP URL를 통해 자원을 명시하고 HTTP Method를 통해 해당 자원에 대한 CRUD 기능을 적용함<br>

|METHOD|설명|CRUD|
|---|--------|-----------|
|GET|자원을 받아오기만 함|Read|
|POST|새로운 자원을 추가|Create|
|PUT|존재하는 자원을 변경|Update|
|DELETE|자원을 삭제|Delete|
|PATCH|한 자원의 데이터를 부분적으로 변경|Update|

2. REST API
 +  REST 기반으로 서비스 API를 구현한 것
 +  설계 기본 규칙도 존재

3. RESTful
 + REST라는 아키텍쳐를 구현하는 웹 서비슬 나타내기 위해 사용 => REST API를 제공하는 웹사이트는 'RESTful'

### JUnit
 + 정의 : Java용 유닛 테스트 프레임워크로 프로그램을 작은 Unit 단위로 쪼개서 각각 잘 동작하는지 검사하므로서 안정성을 높힘
1. Given/when/then 패턴
 + given : 테스트에서 구체화하고자 하는 행동을 하기 전 테스트의 상태를 설명하는 부분
 + when  : 구체화하고자 하는 행동
 + then  : 예상되는 변화를 확인, 검증
~~~
    @Test
    void method1() {
        //given ( 멤버 생성 )이 주어졌을 때
        Member member = new Member(1L,"memberA",Grade.VIP);
        //when  ( 멤버를 회원가입 시킨 뒤, 멤버를 조회) 했을 때
        memberService.join(member);
        Member findMember = memberService.findMember(1L);
        //then   ( 아래 조건을 만족해야 한다. )
        Assertions.assertThat(member).isEqualTo(findMember);
    }
~~~
   
3. F.I.R.S.T : 좋은 테스트를 작성하는 5가지 속성
   
   |F.I.R.S.T|설명|
   |---------|---|
   |Fast|테스트는 빠르게 수행되어야함|
   |Isolated|하나의 테스트코드는 하나의 대상에 대해서만 수행|
   |Reqeatable|테스트는 어떤 환경에서도 반복 가능하도록 작성|
   |Self-validating|테스트 자체만으로 검증이 완료|
   |Timely|테스트하려는 애플리케이션 코드가 구현하기 전에 완성|

4. Assertions 클래스
~~~
import org.assertj.core.api.Assertions;
// isEqualTo 메서드 : obj1과 obj2가 같으면 테스트 통과
Assertions.assertThat(findMember).isEqualTo(savedMember);
// contains : list에 ob1,2가 포함되면 통과
Assertions.assertThat(List list).contains("hello","world");
// containsExactly : s1,s2,s3가 모두 존재해야 통과
assertThat(String[] arr).containsExactly(str1, str2, str3);
// assertThatThrownBy : 로직 실행시 IllegalStateException오류가 발생 시 통과
assertThatThrownBy( () -> logic()).isInstanceOf(IllegalStateException.class);
~~~

5. Annotations
 + @Test : 해당 메서드가 단위 테스트임을 명시
 + @DisplayName("...") : 테스트 이름을 지정
 + @BeforeEach/AferEach : 끝날 때마다 동작하는 메서드
## STEP3
2. 다양한 컨텐츠 타입
