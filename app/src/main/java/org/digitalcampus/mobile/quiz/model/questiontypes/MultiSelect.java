/*
 * This file is part of OppiaMobile - https://digital-campus.org/
 *
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.mobile.quiz.model.questiontypes;

import android.util.Log;

import com.splunk.mint.Mint;

import java.io.Serializable;

import org.digitalcampus.mobile.quiz.Quiz;
import org.digitalcampus.mobile.quiz.model.QuizQuestion;
import org.digitalcampus.mobile.quiz.model.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class MultiSelect extends QuizQuestion implements Serializable {

    private static final long serialVersionUID = 936284577467681053L;
    public static final String TAG = MultiSelect.class.getSimpleName();

    @Override
    public void mark(String lang){
        // loop through the responses
        // find whichever are set as selected and add up the responses

        float total = 0;

        for (Response r : responseOptions){
            for (String ur : userResponses) {
                if (ur.equals(r.getTitle(lang))) {
                    total += r.getScore();
                    if(r.getFeedback(lang) != null && !(r.getFeedback(lang).equals(""))){
                        this.feedback += ur + ": " + r.getFeedback(lang) + "\n\n";
                    }
                }
            }

        }

        // fix marking so that if one of the incorrect scores is selected final mark is 0
        for (Response r : responseOptions){
            for(String ur: userResponses){
                if (r.getTitle(lang).equals(ur) && r.getScore() == 0){
                    total = 0;
                }
            }
        }
        int maxscore = Integer.parseInt(this.getProp(Quiz.JSON_PROPERTY_MAXSCORE));
        if (total > maxscore){
            userscore = maxscore;
        } else {
            userscore = total;
        }
    }

    @Override
    public String getFeedback(String lang) {
        // reset feedback back to nothing
        this.feedback = "";
        this.mark(lang);
        return this.feedback;
    }

    @Override
    public int getMaxScore() {
        return Integer.parseInt(this.getProp(Quiz.JSON_PROPERTY_MAXSCORE));
    }

    @Override
    public JSONObject responsesToJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(Quiz.JSON_PROPERTY_QUESTION_ID, this.id);
            jo.put(Quiz.JSON_PROPERTY_SCORE,userscore);
            String qrtext = "";
            for(String ur: userResponses ){
                qrtext += ur + Quiz.RESPONSE_SEPARATOR;
            }
            jo.put(Quiz.JSON_PROPERTY_TEXT, qrtext);
        } catch (JSONException jsone) {
            Log.d(TAG,"Error creating json object", jsone);
            Mint.logException(jsone);
        }
        return jo;
    }

}