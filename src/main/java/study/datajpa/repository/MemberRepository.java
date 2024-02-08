package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//MemberRepositoryCustom 인터페이스 상속 추가
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    //이름으로 회원찾기 - 도메인에 특화된 기능은 공통으로 만들기 불가. 구현하지않아도 springDataJpa가 작동시킴(쿼리메소드기능)
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //find... : 아무거나 들어가도됨, By 이후 안넣으면 전체조회
    List<Member> findTop3HelloBy();

    //@Param 은 jpql을 명확하게 작성했을때 필요함(:username)
    @Query(name = "Member.findByUsername") //이거 없어도됨
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //DTO는 전체경로 넣어야함
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names); //List말구 Collection으룽

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    //조인이 많을떄 카운트쿼리 따로 분리하여 성능개선 가능 > 하이버네이트6 이후로는 자동최적화 되므로 fetch써야
//    @Query(value = "select m from Member m left join m.team t",
//            countQuery = "select count(m.username) from Member m")
//    @Query(value = "select m from Member m left join fetch m.team t") //하이버네이트6 이후로는 자동최적화 되므로 fetch써야 join나감(불필요)
    Page<Member> findByAge(int age, Pageable pageable);

    //Slice 사용하면 토탈쿼리 조회안함
//    Slice<Member> findByAge(int age, Pageable pageable);

    //List는 단순조회
//    List<Member> findByAge(int age, Pageable pageable);

    //fetch join jpql 작성해야함
    @Modifying(clearAutomatically = true) //SpringDataJpa에서는 있어야 update 실행함. clearAutomatically : 자동 영속성콘텍스트 초기화
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //fetch join : 연관된 쿼리 LAZY아니고 바로 조회해옴
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //간편하게 fetch join 만들기
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") //Member의 @NamedEntityGraph 작동
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
