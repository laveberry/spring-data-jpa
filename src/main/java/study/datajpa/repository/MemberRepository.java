package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //이름으로 회원찾기 - 도메인에 특화된 기능은 공통으로 만들기 불가. 구현하지않아도 springDataJpa가 작동시킴(쿼리메소드기능)
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //find... : 아무거나 들어가도됨, By 이후 안넣으면 전체조회
    List<Member> findTop3HelloBy();

    //@Param 은 jpql을 명확하게 작성했을때 필요함(:username)
    @Query(name = "Member.findByUsername") //이거 없어도됨
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
}
