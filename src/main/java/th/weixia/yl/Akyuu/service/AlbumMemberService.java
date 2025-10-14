package th.weixia.yl.Akyuu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import th.weixia.yl.Akyuu.dto.MemberRequest;
import th.weixia.yl.Akyuu.entity.Album;
import th.weixia.yl.Akyuu.entity.AlbumMember;
import th.weixia.yl.Akyuu.entity.User;
import th.weixia.yl.Akyuu.repository.AlbumMemberRepository;
import th.weixia.yl.Akyuu.repository.AlbumRepository;
import th.weixia.yl.Akyuu.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumMemberService {
    
    private final AlbumMemberRepository albumMemberRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public AlbumMember inviteMember(Long albumId, Long currentUserId, MemberRequest request) {
        // 检查相册是否存在且当前用户是否是所有者
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "相册不存在"));
        
        if (!album.getOwnerId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有相册所有者可以邀请成员");
        }
        
        // 检查被邀请用户是否存在
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        
        // 检查是否已经是成员
        if (albumMemberRepository.existsByAlbumIdAndUserId(albumId, request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "用户已经是相册成员");
        }
        
        // 创建成员关系
        AlbumMember member = new AlbumMember();
        member.setAlbum(album);
        member.setUser(user);
        member.setRole(request.getRole());
        
        return albumMemberRepository.save(member);
    }
    
    @Transactional
    public void removeMember(Long albumId, Long memberUserId, Long currentUserId) {
        // 检查相册是否存在
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "相册不存在"));
        
        // 检查当前用户是否有权限（所有者或移除自己）
        boolean isOwner = album.getOwnerId().equals(currentUserId);
        boolean isRemovingSelf = memberUserId.equals(currentUserId);
        
        if (!isOwner && !isRemovingSelf) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有相册所有者可以移除其他成员");
        }
        
        // 检查成员关系是否存在
        if (!albumMemberRepository.existsByAlbumIdAndUserId(albumId, memberUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "成员关系不存在");
        }
        
        // 不能移除所有者
        if (album.getOwnerId().equals(memberUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能移除相册所有者");
        }
        
        albumMemberRepository.deleteByAlbumIdAndUserId(albumId, memberUserId);
    }
    
    public List<AlbumMember> getAlbumMembers(Long albumId, Long currentUserId) {
        // 检查相册访问权限
        if (!canAccessAlbum(albumId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此相册");
        }
        
        return albumMemberRepository.findByAlbumId(albumId);
    }
    
    public boolean canAccessAlbum(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) return false;
        
        // 公共相册所有人都可以访问
        if (album.getIsPublic()) return true;
        
        // 私有相册：所有者或成员可以访问
        return album.getOwnerId().equals(userId) || 
               albumMemberRepository.existsByAlbumIdAndUserId(albumId, userId);
    }
    
    public boolean canManageAlbum(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) return false;
        
        // 只有所有者可以管理相册
        return album.getOwnerId().equals(userId);
    }
}