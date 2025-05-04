package org.riders.sharing.dto;

public record ChangePasswordDto(
    String customerId,
    String oldPassword,
    String newPassword
) {
}
