package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //이름으로 회원찾기 - 도메인에 특화된 기능은 공통으로 만들기 불가. 구현하지않아도 springDataJpa가 작동시킴(쿼리메소드기능)
    List<Member> findByUsername(String username);

}
