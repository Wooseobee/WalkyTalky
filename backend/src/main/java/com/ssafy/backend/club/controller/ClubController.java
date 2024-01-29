package com.ssafy.backend.club.controller;

import com.ssafy.backend.club.dto.request.RequestCheckNameDto;
import com.ssafy.backend.club.dto.request.RequestClubCreateDto;
import com.ssafy.backend.club.dto.response.ResponseClubDetailDto;
import com.ssafy.backend.club.dto.response.ResponseClubListDto;
import com.ssafy.backend.club.dto.response.ResponseCheckNameDto;
import com.ssafy.backend.club.dto.response.ResponseClubCreateDto;
import com.ssafy.backend.club.service.ClubService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/club")
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/check-name")
    public ResponseEntity<ResponseCheckNameDto> checkName(@RequestBody RequestCheckNameDto requestCheckNameDto) {

        ResponseCheckNameDto responseCheckNameDto = new ResponseCheckNameDto();

        if (clubService.checkName(requestCheckNameDto)) {
            responseCheckNameDto.setMessage("이미 존재하는 소모임 명입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseCheckNameDto);
        } else {
            responseCheckNameDto.setMessage("OK");
            return ResponseEntity.status(HttpStatus.OK).body(responseCheckNameDto);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseClubCreateDto> clubCreate(
            @RequestPart("profileImg") MultipartFile multipartFile,
            @RequestPart("json") RequestClubCreateDto requestClubCreateDto,
            HttpServletRequest httpServletRequest) throws IOException {

        ResponseClubCreateDto responseClubCreateDto = new ResponseClubCreateDto();

        clubService.clubCreate(multipartFile, requestClubCreateDto, httpServletRequest);

        responseClubCreateDto.setMessage("OK");

        return ResponseEntity.status(HttpStatus.OK).body(responseClubCreateDto);

    }

    @GetMapping("/list")
    public ResponseEntity<ResponseClubListDto> clubList(HttpServletRequest httpServletRequest) {

        ResponseClubListDto responseClubListDto = clubService.clubList(httpServletRequest);

        responseClubListDto.setMessage("OK");

        return ResponseEntity.status(HttpStatus.OK).body(responseClubListDto);
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseClubDetailDto> clubDetail(@RequestParam("clubSeq") String clubSeq) {

        ResponseClubDetailDto responseClubDetailDto = clubService.clubDetail(Long.parseLong(clubSeq));

        responseClubDetailDto.setMessage("OK");

        return ResponseEntity.status(HttpStatus.OK).body(responseClubDetailDto);
    }

}


