package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.dto.FeedbackDTO;
import com._4.APIBangLaiXeA1.entity.Feedback;
import com._4.APIBangLaiXeA1.entity.UserAccount;
import com._4.APIBangLaiXeA1.repo.FeedbackRepo;
import com._4.APIBangLaiXeA1.repo.UserAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Autowired
    private UserAccountRepo userAccountRepo;

    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        UserAccount user = userAccountRepo.findById(feedbackDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setSoSao(feedbackDTO.getSoSao());
        feedback.setNoiDung(feedbackDTO.getNoiDung());

        Feedback savedFeedback = feedbackRepo.save(feedback);

        return mapToDTO(savedFeedback);
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private FeedbackDTO mapToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUser().getId());
        dto.setSoSao(feedback.getSoSao());
        dto.setNoiDung(feedback.getNoiDung());
        return dto;
    }
}