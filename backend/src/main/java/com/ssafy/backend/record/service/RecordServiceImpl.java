package com.ssafy.backend.record.service;

import com.ssafy.backend.record.domain.Record;
import com.ssafy.backend.record.domain.RecordDetail;
import com.ssafy.backend.record.dto.request.RequestRecordModify;
import com.ssafy.backend.record.dto.request.RequestRegistCommentDto;
import com.ssafy.backend.record.dto.request.RequestRegistRecordDto;
import com.ssafy.backend.record.dto.response.ResponseListDto;
import com.ssafy.backend.record.repository.RecordDetailRepository;
import com.ssafy.backend.record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

    private final RecordDetailRepository recordDetailRepository;

    public Long startRecord(Long memberSeq) {
        Record record = Record.builder()
                .memberSeq(memberSeq)
                .build();

        Record returnRecord = recordRepository.save(record);

        return returnRecord.getSeq();
    }

    public boolean registRecord(Long memberSeq, RequestRegistRecordDto requestRegistRecordDto) {
        // record table
        Long recordSeq = requestRegistRecordDto.getSeq();

        if (!validateRecord(recordSeq, memberSeq)) {
            return false;
        }

        Record record = Record.builder()
                .seq(recordSeq)
                .memberSeq(memberSeq)
                .title(requestRegistRecordDto.getTitle())
                .duration(requestRegistRecordDto.getDuration())
                .distance(requestRegistRecordDto.getDistance())
                .starRating(requestRegistRecordDto.getStarRating())
                .comment(requestRegistRecordDto.getComment())
                .build();

        recordRepository.save(record);

        // record detail table
        String[][] tmpPoint = requestRegistRecordDto.getPoints();
        List<RecordDetail> list = new ArrayList<>();

        for (String[] p : tmpPoint) {
            RecordDetail recordDetail = RecordDetail.builder()
                    .recordSeq(recordSeq)
                    .latitude(p[0])
                    .longitude(p[1])
                    .time(p[2])
                    .build();
            list.add(recordDetail);
        }

        recordDetailRepository.saveAll(list);

        return true;
    }

    public boolean registComment(Long memberSeq, RequestRegistCommentDto requestRegistCommentDto) {
        Long recordSeq = requestRegistCommentDto.getSeq();

        if (!validateRecord(recordSeq, memberSeq)) {
            return false;
        }

        RecordDetail recordDetail = RecordDetail.builder()
                .recordSeq(recordSeq)
                .pointComment(requestRegistCommentDto.getComment())
                .latitude(requestRegistCommentDto.getLatitude())
                .longitude(requestRegistCommentDto.getLongitude())
                .build();

        recordDetailRepository.save(recordDetail);

        return true;
    }

    public List<ResponseListDto> list(Long memberSeq) {
        return recordRepository.findResponseListDtoByMemberSeq(memberSeq);
    }

    public boolean modify(Long memberSeq, Long recordSeq, RequestRecordModify requestRecordModify) {
        if (!validateRecord(recordSeq, memberSeq)) {
            return false;
        }

        Record record = Record.builder()
                .seq(recordSeq)
                .memberSeq(memberSeq)
                .starRating(requestRecordModify.getStarRating())
                .comment(requestRecordModify.getComment())
                .build();

        recordRepository.save(record);

        return true;
    }

    private boolean validateRecord(Long recordSeq, Long memberSeq) {
        if (!recordRepository.existsById(recordSeq)) {
            // 입력 받은 기록 식별번호에 해당하는 기록이 없는 경우이므로
            // 이는 사용자가 시작을 누르지 않은 비정상 요청임
            return false;
        }

        if (!memberSeq.equals(recordRepository.findMemberSeqBySeq(recordSeq).getMemberSeq())) {
            // 기존 저장되어있던 기록 식별번호를 등록한 사용자와
            // 지금 가져온 사용자 아이디에 해당하는 사람이 다르면 비정상 요청임
            return false;
        }

        return true;
    }

}
