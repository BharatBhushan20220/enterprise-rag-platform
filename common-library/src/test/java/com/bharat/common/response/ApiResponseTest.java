package com.bharat.common.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void ok_shouldBuildSuccessfulResponse() {
        ApiResponse<String> response = ApiResponse.ok("payload", "Created");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Created");
        assertThat(response.getData()).isEqualTo("payload");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void failure_shouldBuildFailedResponse() {
        ApiResponse<Void> response = ApiResponse.failure("boom");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("boom");
        assertThat(response.getData()).isNull();
    }
}
