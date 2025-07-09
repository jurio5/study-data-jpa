package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;

// 사용자 정의 리포지터리를 사용하는 것 보다 이런 식으로 따로 리포지터리를 만들어서 사용하며 관리하는게 좋은 설계인 경우도 있다.
// 커맨드와 쿼리를 분리
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    public List<Member> findAllMembers() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
