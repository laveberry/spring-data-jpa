package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;

/*
항상 사용자정의 레포지토리가 필요한것은 아니다.
그냥 임의 리포지토리 만들어도됨.
클래스로 만들고 스프링빈 등록해서 그냥 직접 사용해도된다.
이경우 스프링 데이터 JPA와는 별도로 작동한다.
* */
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    List<Member> findAllMembers() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }

}
