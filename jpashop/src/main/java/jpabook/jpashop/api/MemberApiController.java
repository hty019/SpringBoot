package jpabook.jpashop.api;

import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PatchMapping("/api/v2/members/{id}")
    @Transactional
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<MemberFindDTO> collect = memberService.findMembers()
                .stream().map(member ->
                        new MemberFindDTO(member.getName())
                ).collect(Collectors.toList());
        return new Result(collect, collect.size());
    }

    @Data
    @AllArgsConstructor
    class Result<T> {
        private List<T> data;
        private int count;
    }

    @Data
    @AllArgsConstructor
    class MemberFindDTO {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    class CreateMemberResponse {
        private Long id;
    }
}
