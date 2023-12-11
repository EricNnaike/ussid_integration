package com.example.mfb_ussd_process_flow.exceptions;

public class MFBException extends Exception{
    public MFBException(String message){
        super(message);
    }

    public MFBException(String message, Throwable cause) {
        super(message, cause);
    }



}
