package com.buildwithus.hackathon.service;

import com.buildwithus.hackathon.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HackathonService {
    HackathonDTO createHackathon(Long userId, CreateHackathonRequest request);
    HackathonDTO updateHackathon(Long hackathonId, CreateHackathonRequest request, Long userId);
    void deleteHackathon(Long hackathonId, Long userId);
    HackathonDTO getHackathonById(Long hackathonId);
    Page<HackathonDTO> getAllHackathons(Pageable pageable);
    Page<HackathonDTO> searchHackathons(String keyword, Pageable pageable);
    
    // Team Finder
    TeamFinderPostDTO createTeamFinderPost(Long userId, CreateTeamFinderPostRequest request);
    TeamFinderPostDTO updateTeamFinderPost(Long postId, CreateTeamFinderPostRequest request, Long userId);
    void deleteTeamFinderPost(Long postId, Long userId);
    Page<TeamFinderPostDTO> getAllTeamFinderPosts(Pageable pageable);
    Page<TeamFinderPostDTO> getTeamFinderPostsByType(String type, Pageable pageable);
    Page<TeamFinderPostDTO> getMyTeamFinderPosts(Long userId, Pageable pageable);
    Page<TeamFinderPostDTO> getTeamFinderPostsForHackathon(Long hackathonId, Pageable pageable);
    
    // Join Requests
    TeamJoinRequestDTO requestToJoinTeam(Long postId, Long userId, String message);
    TeamJoinRequestDTO respondToJoinRequest(Long requestId, Long userId, boolean accept, String responseMessage);
    Page<TeamJoinRequestDTO> getMyJoinRequests(Long userId, Pageable pageable);
    Page<TeamJoinRequestDTO> getJoinRequestsForMyPosts(Long userId, Pageable pageable);
}