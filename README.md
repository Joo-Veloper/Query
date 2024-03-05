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
---
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


건수 제한 & 전체 조회</br>
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
주의 페이징 쿼리 작성시 데이터 조회 쿼리는 여러 테이블을 조인하지만 Count는 조인이 필요 없는 경우 있음 count 쿼리에 조인이 필요없는 최적화 할때 count 전용 쿼리 별도 작성 !</br>


### 조인 - on절
JPA 2.1에서는 ON 절을 사용하여 조인 대상 필터링 및 연관관계 없는 엔티티의 외부 조인을 할 수 있습니다.</br>

조인 대상 필터링</br>
```
java
Copy code
SELECT e
FROM Employee e
LEFT JOIN e.department d ON d.active = true
```
Employee 엔티티를 Department 엔티티와 외부 조인하고 있습니다.</br> 
그러나 Department가 활성 상태인 경우에만 조인 대상으로 고려</br>

연관관계 없는 엔티티 외부 조인</br>
```
java
Copy code
SELECT c, a
FROM Customer c
LEFT JOIN Address a ON c.address_id = a.id
```
위의 예제에서는 Customer 엔티티와 Address 엔티티 사이에 직접적인 연관관계가 없는 경우에도 Customer 엔티티와 Address 엔티티를 조인하고 있습니다. 이러한 경우에는 ON 절을 사용하여 수동으로 조인 조건을 지정할 수 있습니다.

### 페치 조인
페치 조인은 SQL 에서 제공하는 기능은 아니다. SQL 조인을 활용해 연관된 Entity를 성능 최적화에 사용 </br>

##### 사용방법
join(). leftJoin()등 조인 기능 뒤에 fetchJoin()이라고 추가 </br>


### 서브 쿼리

JPA JPQL 서브쿼리의 한계점으로 from 절의 서브 쿼리는 지원하지 않음 </br>
Hibernate 구현체를 사용하면 select 절의 서브 쿼리 지원하며 Querydsl도 하이버네이트 구현체를 사용하면 select 절의 서브 쿼리 지원</br>

##### From절 서브 쿼리 해결방안
1. 서브 쿼리를 조인으로 변경: 서브 쿼리를 조인으로 변경하여 메인 쿼리와 함께 실행할 수 있습니다. 이 방법은 성능에 영향을 줄 수 있으므로 주의가 필요합니다. </br>

2. 애플리케이션에서 쿼리를 2번 분리하여 실행: 서브 쿼리와 메인 쿼리를 분리하여 두 번의 쿼리를 실행하는 방법입니다. 이 방법은 가독성을 높일 수 있지만, 두 번의 쿼리 실행으로 인한 성능 저하가 있을 수 있습니다.</br>

3. nativeSQL 사용: 네이티브 SQL 쿼리를 사용하여 직접 서브 쿼리를 작성할 수 있습니다. 이 방법은 특정 데이터베이스에 의존적이며, JPQL의 객체 지향적 특성을 잃을 수 있습니다.</br>




