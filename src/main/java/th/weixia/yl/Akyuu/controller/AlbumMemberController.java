package th.weixia.yl.Akyuu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import th.weixia.yl.Akyuu.dto.MemberRequest;
import th.weixia.yl.Akyuu.entity.AlbumMember;
import th.weixia.yl.Akyuu.security.UserPrincipal;
import th.weixia.yl.Akyuu.service.AlbumMemberService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/albums/{albumId}/members")
@RequiredArgsConstructor
public class AlbumMemberController {

    private final AlbumMemberService albumMemberService;

    @PostMapping
    public ResponseEntity<AlbumMember> inviteMember(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody MemberRequest request) {
        AlbumMember member = albumMemberService.inviteMember(albumId, currentUser.getId(), request);
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/{memberUserId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long albumId,
            @PathVariable Long memberUserId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        albumMemberService.removeMember(albumId, memberUserId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AlbumMember>> getAlbumMembers(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<AlbumMember> members = albumMemberService.getAlbumMembers(albumId, currentUser.getId());
        return ResponseEntity.ok(members);
    }
}