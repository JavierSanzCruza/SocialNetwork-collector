/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.data;

/**
 *
 * @author Javier
 */
public class AnswerPost extends Post
{
    private String answer;
    private String question;
    private String askingName;
    private String askingUrl;

    public AnswerPost()
    {
        this.setType(TumblrType.ANSWER_POST);
    }
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAskingName() {
        return askingName;
    }

    public void setAskingName(String askingName) {
        this.askingName = askingName;
    }

    public String getAskingUrl() {
        return askingUrl;
    }

    public void setAskingUrl(String askingUrl) {
        this.askingUrl = askingUrl;
    }
    
    
}
