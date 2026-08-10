package com.reserix.api.chat.ai;

import com.reserix.api.chat.dto.IntentDetectionResult;

public interface AiIntentClassifier {

    IntentDetectionResult classify(String message);
}
