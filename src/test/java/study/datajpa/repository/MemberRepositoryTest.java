package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all).hasSize(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThen() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();

        Member m2 = Member.builder()
                .username("AAA")
                .age(20)
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.getFirst().getUsername()).isEqualTo("AAA");
        assertThat(result.getFirst().getAge()).isEqualTo(20);
        assertThat(result).hasSize(1);
    }

    @Test
    void testNamedQuery() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();

        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.getFirst();
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    void testQuery() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();

        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.getFirst()).isEqualTo(m1);
    }

    @Test
    void findUsernameList() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();

        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void findMemberDto() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();

        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    @Test
    void findByNames() {
        Team team = new Team("teamA");
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .team(team)
                .build();


        teamRepository.save(team);
        memberRepository.save(m1);
    }

    @Test
    void returnType() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();

        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMemberCollection = memberRepository.findListByUsername("AAA");
        Member findMemberOne = memberRepository.findMemberByUsername("AAA");
        Optional<Member> findMemberOptional = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // 정렬 기준이 복잡해질 땐 JPQL 로 해결
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content).hasSize(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }
}