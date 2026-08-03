package com.bharat.common.util;

import java.util.List;

public final class VectorMath {

    private VectorMath() {
    }

    public static double cosineSimilarity(List<Float> left, List<Float> right) {
        if (left == null || right == null || left.isEmpty() || left.size() != right.size()) {
            return 0.0;
        }
        double dot = 0.0;
        double leftNorm = 0.0;
        double rightNorm = 0.0;
        for (int i = 0; i < left.size(); i++) {
            float a = left.get(i);
            float b = right.get(i);
            dot += a * b;
            leftNorm += a * a;
            rightNorm += b * b;
        }
        if (leftNorm == 0.0 || rightNorm == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }
}
