package com.ebidding.bwic.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OnnxModelService {

    private OrtEnvironment env;
    private OrtSession session;

    public OnnxModelService() throws OrtException, IOException {
        env = OrtEnvironment.getEnvironment();
        // 从资源文件夹中加载模型
        try (InputStream modelStream = getClass().getClassLoader().getResourceAsStream("model.onnx")) {
            if (modelStream == null) {
                throw new IOException("Model file not found in resources: model.onnx");
            }
            byte[] modelBytes = modelStream.readAllBytes();
            session = env.createSession(modelBytes);
        }
    }

    public Map<String, Double> predict(String userId, List<String> bwicIds) {
        Map<String, Double> scores = new HashMap<>();
        try {
            int userIntId = Integer.parseInt(userId); // 这里暂时固定为1，你可以根据需要修改为 uuidToInt(userId) 的逻辑

            long[] userIds = new long[bwicIds.size()];
            long[] itemIds = new long[bwicIds.size()];

            for (int i = 0; i < bwicIds.size(); i++) {
                userIds[i] = userIntId;
                itemIds[i] = Long.parseLong(bwicIds.get(i));
            }

            OnnxTensor userTensor = OnnxTensor.createTensor(env, userIds);
            OnnxTensor itemTensor = OnnxTensor.createTensor(env, itemIds);

            Map<String, OnnxTensor> inputMap = new HashMap<>();
            inputMap.put("account_id", userTensor);
            inputMap.put("bwic_id", itemTensor);

            OrtSession.Result results = session.run(inputMap);
            float[] resultArray = (float[]) results.get(0).getValue();

            for (int i = 0; i < bwicIds.size(); i++) {
                scores.put(bwicIds.get(i), (double) resultArray[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 返回一个空的分数映射
            scores.clear();
        }
        return scores;
    }


    private int uuidToInt(String uuidStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(uuidStr.getBytes());
            int result = 0;
            for (int i = 0; i < 4; i++) {
                result <<= 8;
                result |= (hash[i] & 0xFF);
            }
            return result % 100000000;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
