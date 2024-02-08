package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findList = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("AAA");
        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA"); //동일건 2개이상 오류 발생 : IncorrectResultSizeDataAccessException
        System.out.println("optional " + optional);

        List<Member> optionalE = memberRepository.findListByUsername("sss");
        Member optionalN = memberRepository.findMemberByUsername("sss");
        Optional<Member> optionalY = memberRepository.findOptionalByUsername("sss");
        System.out.println("optionalE " + optionalE); //[]
        System.out.println("optionalN " + optionalN); //null
        System.out.println("optionalY " + optionalY); //Optional.empty
    }

    @Test
    void 스프링데이타JPA_paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); //구현체
//        PageRequest.of(0, 3); //sort 선택사항

        //when
        //total 카운트 쿼리 자동 생성
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy(); //참조 top만 가져오기도 가능
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest); //slice 하면 토탈카운트 조회안함
//        List<Member> page = memberRepository.findByAge(age, pageRequest); //단순히 데이터만 가져오기

        //간단하게 model로 변환가능
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void bulkAgePlus() {
        //given
        //벌크연산
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //벌크연산 이후 추가작업 있을시, 영속성컨텍스트 초기화 해주어야함. clearAutomatically로 대체 가능
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(member5.getAge()).isEqualTo(41);
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when (N + 1 문제)
        //select Member 1 -> EntityGraph 으로 패치조인 가능
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        //기존 - 10개면 추가 team조회쿼리 10번 나감
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //기존 - 프록시객체
            System.out.println("member.team = " + member.getTeam().getName()); //기존 - 각각 팀 쿼리 작동
        }

        //fetch join - join 하고 select 절 데이터도 넣어줌
//        List<Member> memberFetchJoin = memberRepository.findMemberFetchJoin();
//
//        for (Member member : memberFetchJoin) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.teamClass = " + member.getTeam().getClass());
//            System.out.println("member.team = " + member.getTeam().getName());
//        }
    }

    @Test
    void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); //결과 db 동기화
        em.clear(); //영속성컨텍스트 날리기

        //when
//        Member findMember = memberRepository.findById(member1.getId()).get(); //실무는 get 쓰면안됨
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); //readOnly로 성능최적화. 스냅샷 안만듬
        findMember.setUsername("member2"); //readOnly 변경감지안함. 변경쿼리 안나감

        em.flush();//readOnly 아닐시, 변경감지 등. 최적화가 되어있더라도 비용이 듬

        assertThat(findMember.getUsername()).isEqualTo("member2");
        assertThat(memberRepository.findReadOnlyByUsername("member2")).isNull();
    }

    @Test
    void lock() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        //rock - select쿼리 for update 뒤에 붙음
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    //사용자정의 메소드
    @Test
    void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }
}