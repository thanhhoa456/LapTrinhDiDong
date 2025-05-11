package com._4.APIBangLaiXeA1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private int id;
    private int userId;
    private int soSao;
    private String noiDung;
}