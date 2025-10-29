package com.contextoia.identityaccess.application.dto;


public interface UserAuthDetails {
    String getUsername();
    String getPasswordHash();

    boolean isEnabled();

    boolean isLocked();

}