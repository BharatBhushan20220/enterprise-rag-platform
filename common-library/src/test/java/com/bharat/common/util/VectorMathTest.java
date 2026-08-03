package com.bharat.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class VectorMathTest {

    @Test
    void cosineSimilarity_shouldReturnOneForIdenticalVectors() {
        List<Float> vector = List.of(1.0f, 0.0f, 0.0f);
        assertThat(VectorMath.cosineSimilarity(vector, vector)).isCloseTo(1.0, within(1e-6));
    }

    @Test
    void cosineSimilarity_shouldReturnZeroForOrthogonalVectors() {
        assertThat(VectorMath.cosineSimilarity(List.of(1.0f, 0.0f), List.of(0.0f, 1.0f)))
                .isCloseTo(0.0, within(1e-6));
    }
}
