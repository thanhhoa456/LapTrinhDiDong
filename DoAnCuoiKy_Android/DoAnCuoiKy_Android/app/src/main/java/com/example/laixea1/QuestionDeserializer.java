package com.example.laixea1.api;

import android.util.Base64;

import com.example.laixea1.entity.Question;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class QuestionDeserializer implements JsonDeserializer<Question> {
    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Lấy các trường từ JSON, xử lý null an toàn
        int id = jsonObject.get("id").getAsInt();
        int groupId = jsonObject.get("groupId").getAsInt();

        String question = jsonObject.has("question") && !jsonObject.get("question").isJsonNull() ?
                jsonObject.get("question").getAsString() : "";
        String option1 = jsonObject.has("option1") && !jsonObject.get("option1").isJsonNull() ?
                jsonObject.get("option1").getAsString() : "";
        String option2 = jsonObject.has("option2") && !jsonObject.get("option2").isJsonNull() ?
                jsonObject.get("option2").getAsString() : "";
        String option3 = jsonObject.has("option3") && !jsonObject.get("option3").isJsonNull() ?
                jsonObject.get("option3").getAsString() : "";
        String option4 = jsonObject.has("option4") && !jsonObject.get("option4").isJsonNull() ?
                jsonObject.get("option4").getAsString() : "";
        int answer = jsonObject.get("answer").getAsInt();
        String explainQuestion = jsonObject.has("explainQuestion") && !jsonObject.get("explainQuestion").isJsonNull() ?
                jsonObject.get("explainQuestion").getAsString() : "";
        boolean failingScore = jsonObject.get("failingScore").getAsBoolean();

        // Xử lý trường image (chuỗi Base64 -> byte[])
        byte[] image = null;
        if (jsonObject.has("image") && !jsonObject.get("image").isJsonNull()) {
            String imageBase64 = jsonObject.get("image").getAsString();
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    image = Base64.decode(imageBase64, Base64.DEFAULT);
                } catch (IllegalArgumentException e) {
                    throw new JsonParseException("Invalid Base64 string for image", e);
                }
            }
        }

        // Tạo đối tượng Question
        Question questionObj = new Question();
        questionObj.setId(id);
        questionObj.setGroupId(groupId);
        questionObj.setQuestion(question);
        questionObj.setOption1(option1);
        questionObj.setOption2(option2);
        questionObj.setOption3(option3);
        questionObj.setOption4(option4);
        questionObj.setAnswer(answer);
        questionObj.setImage(image);
        questionObj.setExplainQuestion(explainQuestion);
        questionObj.setFailingScore(failingScore);

        return questionObj;
    }
}