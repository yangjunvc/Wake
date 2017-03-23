package com.android.wako.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.json.QuestionDetailJson;
import com.android.wako.model.QuestionModel;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看问答
 * Created by duanmulirui
 */
public class QuestionDetailActivity extends CommonTitleActivity{
    private static final int REQUEST_DETAIL = 10013;

    private TextView mQuestion,mAnswer;
    private View answerView;
    private String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail_main);
        initViews();

        questionId = getIntent().getStringExtra("questionId");
        getDetailData();
    }

    private void getDetailData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("questionId",questionId));
        startHttpRequst(Constants.HTTP_POST, Constants.MYQUESTION_DETAIL, parameter, false, REQUEST_DETAIL, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.my_question_detail);

        mQuestion = (TextView) findViewById(R.id.detail_question);
        mAnswer = (TextView) findViewById(R.id.detail_answer);
        answerView = findViewById(R.id.detail_answer_view);
        answerView.setVisibility(View.GONE);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_DETAIL:
                if(resStatus == ResStatus.Success){
                    QuestionDetailJson json = gson.fromJson(resultJson, QuestionDetailJson.class);
                    if(json != null && json.header != null && json.header.status == 1 && json.content != null){
                        QuestionModel model = json.content;
                        mQuestion.setText(model.questionContent);
                        if(model.answerStatus != 0){//已解答
                            answerView.setVisibility(View.VISIBLE);
                            mAnswer.setText(model.answerContent);
                        }else{
                            answerView.setVisibility(View.GONE);
                        }
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            default:
                break;
        }
    }
}
