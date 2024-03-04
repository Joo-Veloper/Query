# Query DSL

[레퍼런스](http://querydsl.com/static/querydsl/4.0.1/reference/ko-KR/html_single/)
</br>
</br>
</br>
### JPQL, Querydsl

---
자바 퍼시스턴스 API(JPA)를 사용할 때 EntityManager를 이용하여 JPAQueryFactory를 생성할 수 있습니다. Querydsl은 JPQL(Java Persistence Query Language)을 빌드하는 도구입니다. 이 둘의 주요 차이점은 다음과 같습니다:

JPQL vs. Querydsl 코드:

 - JPQL은 문자열로 쿼리를 작성하며 실행 시점에 오류가 발생할 수 있습니다. 반면에 Querydsl은 코드를 사용하여 쿼리를 작성하므로 컴파일 시점에 오류를 확인할 수 있습니다.
파라미터 바인딩:

 - JPQL에서는 파라미터를 직접 바인딩해야 합니다. 이는 오타나 잘못된 형식으로 인해 런타임 오류가 발생할 수 있습니다. 반면에 Querydsl은 자동으로 파라미터 바인딩을 처리하므로 오류가 줄어듭니다.

### QType
---

사용법
```
QMember qMember = new QMember("m");
QMember qMember = QMember.member;
```

### 결과 조회
fetch() </br>
리스트 형태로 결과를 조회합니다.</br>
데이터가 없으면 빈 리스트를 반환합니다.</br>

fetchOne()</br>
결과를 하나만 조회합니다.</br>
결과가 없으면 null을 반환합니다.</br>
결과가 둘 이상인 경우 com.querydsl.core.NonUniqueResultException이 발생합니다.</br>

fetchFirst()</br>
limit(1)을 적용하여 첫 번째 결과만 조회합니다.</br>
내부적으로는 fetchOne()과 유사합니다.</br>

fetchResults()</br>
페이징 정보와 함께 결과를 조회합니다.</br>
전체 결과의 개수를 조회하기 위해 추가적인 count 쿼리를 실행합니다.</br>
반환되는 객체에는 페이지의 결과 및 전체 결과의 개수를 포함합니다</br>.

fetchCount()</br>
count 쿼리를 실행하여 결과의 개수를 조회합니다.</br>
count 수만을 반환합니다.</br>


### 정렬, 페이징
desc(), asc(), nullsLast(), 그리고 nullsFirst()는 Querydsl에서 사용되는 정렬 관련 메서드입니다. </br>

desc()</br>
내림차순(DESC)으로 정렬합니다.</br>
큰 값부터 작은 값으로 정렬됩니다.</br>

asc()</br>
오름차순(ASC)으로 정렬합니다.</br>
작은 값부터 큰 값으로 정렬됩니다.</br>

nullsLast()</br>
NULL 값은 정렬 후에 마지막에 위치하도록 합니다.</br>
기본적으로 NULL 값은 정렬 시 가장 먼저 나타납니다.</br>

nullsFirst()</br>
NULL 값은 정렬 후에 가장 앞에 위치하도록 합니다.</br>
기본적으로 NULL 값은 정렬 시 가장 마지막에 나타납니다.</br>


건수 제한 & 전체 조회
```
```java
@Test
public void paging1() {
List<Member> result = queryFactory
.selectFrom(member)
.orderBy(member.username.desc())
.offset(1) //0부터 시작(zero index)
.limit(2) //최대 2건 조회
.fetch();
assertThat(result.size()).isEqualTo(2);
}
```

```
@Test
public void paging2() {
QueryResults<Member> queryResults = queryFactory
.selectFrom(member)
.orderBy(member.username.desc())
.offset(1)
.limit(2)
.fetchResults();
assertThat(queryResults.getTotal()).isEqualTo(4);
assertThat(queryResults.getLimit()).isEqualTo(2);
assertThat(queryResults.getOffset()).isEqualTo(1);
assertThat(queryResults.getResults().size()).isEqualTo(2);
}
```
주의 페이징 쿼리 작성시 데이터 조회 쿼리는 여러 테이블을 조인하지만 Count는 조인이 필요 없느 ㄴ경우 있음 count 쿼리에 조인이 필요없는 최적화 할때 count 전용 쿼리 별도 작성 !



