package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL(){
        //member 1을 찾아라.
        String qlString =
                "select  m from Member m " +
                "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl(){

        QMember m1 = new QMember("m1");

        Member findMember = queryFactory
                // static import 사용 QMember.member -> member
                .select(m1)
                .from(m1)
                .where(m1.username.eq("member1")) // 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    @Test
    public void search(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.between(10, 30)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    @Test
    public void searchAndParam(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    // fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
    // fetchOne() : 단건 조회
    // 결과 없으면 null
    // 결과 둘 이상이면 com.querydsl.core.NonUniqueResultException
    // fetchFirst() : limit(1).fetchOne()
    // fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
    // fetchCount() : count 쿼리로 변경해서 count 수 조회

    @Test
    public void resultFetch() {
//        List<Member> fetch = queryFactory
//                .selectFrom(member)
//                .fetch();
//
//        // 단건
//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne();
//
//        // .limit(1).fetchOne();과 같은 역할
//        Member fetchFirst = queryFactory
//                .selectFrom(member)
//                .fetchFirst();

//        QueryResults<Member> results = queryFactory
//                .selectFrom(member)
//                .fetchResults();

//        results.getTotal();
//        List<Member> content = results.getResults();

        // select절을 count로 바꿈
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }
    /**
     화면 정렬 순서
     1. 회원 나이 내림차순
     2. 회원 이름 올림차순
     * 단 2에서 회원 이름이 없으면 마지막 출력
   **/

    @Test
    public void sort(){
        em.persist(new Member(null, 100));
        em.persist(new Member("member5" , 100));
        em.persist(new Member("member6" , 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))   // 나이가 100인 사람 조회
                // nullLast , nullFirst 는 데이터 순서 여부
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                // .orderBy(member.age.desc(), member.username.asc().nullsFirst())
                .fetch(); //메서드 사용하여 쿼리 실행후 결과!

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }


}