package com._4.APIBangLaiXeA1.service;


import com._4.APIBangLaiXeA1.entity.Answer;
import java.util.List;

public interface AnswerService {
    /**
     * Lấy danh sách tất cả đáp án của một người dùng dựa trên userId.
     * @param userId ID của người dùng
     * @return Danh sách các đáp án
     */
    List<Answer> getAnswersByUserId(int userId);

    /**
     * Lấy danh sách đáp án của một người dùng theo userId và groupId.
     * @param userId ID của người dùng
     * @param groupId ID của nhóm câu hỏi
     * @return Danh sách các đáp án thuộc nhóm câu hỏi
     */
    List<Answer> getAnswersByUserIdAndGroupId(int userId, int groupId);

    /**
     * Lưu hoặc cập nhật một đáp án.
     * @param answer Đáp án cần lưu
     * @return Đáp án đã được lưu
     */
    Answer saveOrUpdateAnswer(Answer answer);

    /**
     * Lưu hoặc cập nhật hàng loạt đáp án.
     * @param answers Danh sách các đáp án cần lưu
     * @return Danh sách các đáp án đã được lưu
     */
    List<Answer> saveOrUpdateAnswers(List<Answer> answers);
}