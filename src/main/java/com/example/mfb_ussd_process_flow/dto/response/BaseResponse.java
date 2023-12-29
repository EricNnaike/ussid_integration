package com.example.mfb_ussd_process_flow.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BaseResponse<T> {
    private String message;
    private T data;
    private List<StatementResponse> response;


    public BaseResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public BaseResponse(String message) {
        this.message = message;
    }

    public BaseResponse() {
    }

    private static class StatementResponse {
        private String tranDate;

    }


}
