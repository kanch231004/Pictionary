package com.cnx.pictionary

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cnx.pictionary.models.QuestionModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var questionList = ArrayList<QuestionModel>()
    private var score = 0
    private var difficultyLevel = startingDiffiLevel
    private var attemptedQues = 0
    private var maxQuestion = maxQues
    private var timer : CountDownTimer? = null
    private var maxTime : Long = 30000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadResultFromAssets()
    }


    private fun loadResultFromAssets() {

        try {

            applicationContext.assets.open(FEED_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->

                    val feedModelType = object : TypeToken<ArrayList<QuestionModel>>() {}.type
                    val feedModel : ArrayList<QuestionModel> = Gson().fromJson(jsonReader, feedModelType)

                    questionList.addAll(feedModel)
                    Log.d("feedModel ","${feedModel.toString()}")
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error ${ ex.printStackTrace()}")
        }

        selectQuestionFromPool(difficultyLevel )
    }


    private fun selectQuestionFromPool(difficultyLevel : Int) {

        Log.e(TAG,"difficulty Level ${difficultyLevel}")
        for (question in questionList) {

            if (question.difficulty == difficultyLevel ) {

                if (!question.isAsked) {
                    displayQuestion(question)
                    break
                }
            }
        }
    }



    private fun showTimer() {

       timer =  object : CountDownTimer(maxTime, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                tvTimer.text = "00:${millisUntilFinished / 1000}"

            }

            override fun onFinish() {

                /** Edge case No questions answered and time is off */

                --difficultyLevel
                if (difficultyLevel == 0) {

                    finish()
                    Toast.makeText(applicationContext,"Game Over $score",Toast.LENGTH_SHORT).show()
                }

                selectQuestionFromPool(difficultyLevel)
            }

        }.start()
    }


    private fun displayQuestion(question : QuestionModel ) {

        timer?.cancel()
        showTimer()
        attemptedQues++
        etAnswer.setText("")
        tbRound.setTitle("Round ${attemptedQues}/${maxQuestion}")
        tvQuesAnsw.text = question.answer.toString()

        Log.d(TAG,"maxQuest $attemptedQues")
        Glide.with(this).load(question.imageUrl).into(ivImage)
        question.isAsked = true

        btnSubmit.setOnClickListener { checkAns(etAnswer.text.trim().toString(),question) }

    }

    private fun checkAns(answer : String, question: QuestionModel) {

        if (answer.equals(question.answer , true)) {

            ++score

            if (difficultyLevel < 5) {
                difficultyLevel++
            }

            selectQuestionFromPool(difficultyLevel)

            Toast.makeText(applicationContext,"Correct",Toast.LENGTH_SHORT).show()

        }

        else {

            difficultyLevel--

            Toast.makeText(applicationContext,"Wrong Answer",Toast.LENGTH_SHORT).show()
            if (difficultyLevel <= 0) {
                Toast.makeText(applicationContext,"Game over!",Toast.LENGTH_SHORT).show()

                finish()
            }

            selectQuestionFromPool(difficultyLevel)
        }

        if (attemptedQues > maxQuestion  ) {

            Toast.makeText(applicationContext,"Your score $score",Toast.LENGTH_SHORT).show()
            finish()
        }


    }
}
